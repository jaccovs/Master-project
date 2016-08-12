package org.exquisite.protege.model;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.costestimators.CardinalityCostEstimator;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.exquisite.core.costestimators.SimpleCostsEstimator;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Answer;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.*;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinMaxFormulaWeights;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinQueryCardinality;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinSumFormulaWeights;
import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.exquisite.protege.model.configuration.SearchConfiguration;
import org.exquisite.protege.model.error.ErrorHandler;
import org.exquisite.protege.model.error.SearchErrorHandler;
import org.exquisite.protege.ui.list.AxiomListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.ErrorStatus.NO_ERROR;
import static org.exquisite.protege.model.OntologyDiagnosisSearcher.ErrorStatus.SOLVER_EXCEPTION;

public class OntologyDiagnosisSearcher {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(OntologyDiagnosisSearcher.class.getName());

    public enum TestcaseType {ORIGINAL_ENTAILED_TC, ORIGINAL_NON_ENTAILED_TC, ACQUIRED_ENTAILED_TC, ACQUIRED_NON_ENTAILED_TC}

    public enum ErrorStatus {NO_CONFLICT_EXCEPTION, SOLVER_EXCEPTION, INCONSISTENT_THEORY_EXCEPTION,
        NO_QUERY, ONLY_ONE_DIAG, NO_ERROR, UNKNOWN_RM, UNKNOWN_SORTCRITERION}

    public enum QuerySearchStatus { IDLE, SEARCH_DIAG, GENERATING_QUERY, MINIMZE_QUERY, ASKING_QUERY }

    private DebuggingSession debuggingSession;

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;

    private DiagnosisEngineFactory diagnosisEngineFactory;

    private ErrorStatus errorStatus = NO_ERROR;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new HashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> previousDiagnoses = null;

    //private Query<OWLLogicalAxiom> actualQuery;

    //private Query<OWLLogicalAxiom> previousQuery;

    private Answer<OWLLogicalAxiom> answer = new Answer<>();

    private Answer<OWLLogicalAxiom> previousAnswer = new Answer<>();

    /*
    private Set<OWLLogicalAxiom> axiomsMarkedEntailed = new LinkedHashSet<>();

    private Set<OWLLogicalAxiom> previousAxiomsMarkedEntailed = new LinkedHashSet<>();

    private Set<OWLLogicalAxiom> axiomsMarkedNonEntailed = new LinkedHashSet<>();

    private Set<OWLLogicalAxiom> previousAxiomsMarkedNonEntailed = new LinkedHashSet<>();
    */

    private List<Set<OWLLogicalAxiom>> queryHistory = new LinkedList<>();

    private Map<Set<OWLLogicalAxiom>,TestcaseType> queryHistoryType = new LinkedHashMap<>();

    private IQueryComputation<OWLLogicalAxiom> qc = null;

    private final OWLModelManager modelManager;

    private final OWLReasonerManager reasonerManager;

    private Double cautiousParameter, previousCautiousParameter;

    private Testcases testcases;

    public OntologyDiagnosisSearcher(OWLEditorKit editorKit) {
        modelManager = editorKit.getModelManager();
        reasonerManager = modelManager.getOWLReasonerManager();
        OWLOntology ontology = modelManager.getActiveOntology();
        diagnosisEngineFactory = new DiagnosisEngineFactory(ontology, reasonerManager);
        debuggingSession = new DebuggingSession();
        this.testcases = new Testcases(this);
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /*************************             G E T T E R   M E T H O D S               **********************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    public DiagnosisEngineFactory getDiagnosisEngineFactory() {
        return diagnosisEngineFactory;
    }

    public Query<OWLLogicalAxiom> getActualQuery() {
        return answer.query;
    }

    public Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
    }

    private ErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public List<Set<OWLLogicalAxiom>> getQueryHistory() {
        return queryHistory;
    }

    public Map<Set<OWLLogicalAxiom>, TestcaseType> getQueryHistoryType() {
        return queryHistoryType;
    }

    public QuerySearchStatus getQuerySearchStatus() {
        return querySearchStatus;
    }

    public boolean isMarkedEntailed(OWLLogicalAxiom axiom) {
        return answer.positive.contains(axiom);
    }

    public boolean isMarkedNonEntailed(OWLLogicalAxiom axiom) {
        return answer.negative.contains(axiom);
    }

    public int sizeOfEntailedAndNonEntailedAxioms() {
        return answer.positive.size() + answer.negative.size();
    }

    public boolean isSessionRunning() {
        return debuggingSession.getState() == DebuggingSession.State.STARTED;
    }

    public Testcases getTestcases() {
        return this.testcases;
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /*************************                 L I S T E N E R S                     **********************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    private void notifyListeners() {
        for (ChangeListener listener : changeListeners)
            listener.stateChanged(new ChangeEvent(this));
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /*************************               S T A T E S   &   R E S E T S         ************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Starts a new debugging session. This step initiates the search for a diagnosis and the presentation of a
     * query.
     *
     * @param errorHandler An error handler.
     */
    public void doStartDebugging(SearchErrorHandler errorHandler) {
        if (reasonerManager.getReasonerStatus() == ReasonerStatus.NO_REASONER_FACTORY_CHOSEN) {
            JOptionPane.showMessageDialog(null, "No Reasoner set. Select a reasoner from the Reasoner menu.", "No Reasoner set", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        debuggingSession.startSession();                // start session
        this.testcases = new Testcases(this);
        doCalculateDiagnosesAndGetQuery(errorHandler);  // calculate diagnoses and compute query

        switch (diagnoses.size()) {
            case 0:
                JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);
                doStopDebugging();
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "The faulty axioms corresponding to your preferences (test cases) are found!", "Faulty Axioms found!", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                notifyListeners();
                break;
        }
    }

    /**
     * Commit the response from the expert, calculate the new diagnoses and get the new queries.
     *
     * @param errorHandler An error handler.
     */
    public void doCommitAndGetNewQuery(ErrorHandler errorHandler) {
        this.previousDiagnoses = new HashSet<>(this.diagnoses);
        doCommitQuery();
        doCalculateDiagnosis(errorHandler);

        switch (diagnoses.size()) {
            case 0:
                JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);
                doStopDebugging();
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "The faulty axioms corresponding to your preferences (test cases) are found!", "Faulty Axioms found!", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                doGetQuery(errorHandler);
                break;
        }
    }

    /**
     * Stop diagnosis session -> reset engine, diagnoses, conflicts, queries and history.
     */
    public void doStopDebugging() {
        diagnosisEngineFactory.reset();                             // reset engine
        resetDiagnoses();                                           // reset diagnoses, conflicts
        resetQuery();                                               // reset queries
        resetQueryHistory();                                        // reset history
        testcases.reset();
        debuggingSession.stopSession();                             // stop session
        this.cautiousParameter = null;
        this.previousCautiousParameter = null;
        notifyListeners();
    }

    /**
     * Reset debugger ->  reset test cases + doStopDebugging()
     */
    public void doResetDebugger() {
        this.testcases.reset();
        doStopDebugging();
    }

    public void doResetAll() throws OWLOntologyCreationException {
        this.modelManager.reload(modelManager.getActiveOntology());
    }

    void reasonerChanged() {
        getDiagnosisEngineFactory().reasonerChanged();
        doStopDebugging();
    }

    /**
     * Reset the diagnoses engine and doFullReset().
     */
    void doReload() {
        this.diagnosisEngineFactory.reset();
        doResetDebugger();
    }

    private void resetQuery() {
        this.previousAnswer = this.answer;
        this.answer = new Answer<>();

        this.querySearchStatus = QuerySearchStatus.IDLE;
        if (this.qc!=null) qc.reset();
    }

    private void resetQueryHistory() {
        this.queryHistory.clear();
        this.queryHistoryType.clear();
    }

    private void resetDiagnoses() {
        this.diagnoses.clear();
        this.previousDiagnoses = null;
        this.diagnosisEngineFactory.getDiagnosisEngine().resetEngine();
    }

    /**
     * Moves a set of correct axioms to the set of possibly faulty axioms in the diagnosis model.
     *
     * @param selectedCorrectAxioms The selected, yet correct, axioms that shall become possibly faulty.
     */
    public void moveToPossiblyFaultyAxioms(List<AxiomListItem> selectedCorrectAxioms) {
        logger.debug("moving " + selectedCorrectAxioms + " from background to possiblyFaultyAxioms");
        List<OWLLogicalAxiom> axioms = extract(selectedCorrectAxioms);
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel();
        diagnosisModel.getCorrectFormulas().removeAll(axioms);
        diagnosisModel.getPossiblyFaultyFormulas().addAll(axioms);
        notifyListeners();
    }

    /**
     * Moves a list of possibly faulty axioms to the set of correct axioms in the diagnosis model.
     *
     * @param selectedPossiblyFaultyAxioms The selected, yet possibly faulty, axioms that shall become correct axioms.
     */
    public void moveToToCorrectAxioms(List<AxiomListItem> selectedPossiblyFaultyAxioms) {
        logger.debug("moving " + selectedPossiblyFaultyAxioms + " from possiblyFaultyAxioms to correctAxioms");
        List<OWLLogicalAxiom> axioms = extract(selectedPossiblyFaultyAxioms);
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel();
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(axioms);
        diagnosisModel.getCorrectFormulas().addAll(axioms);
        notifyListeners();
    }

    private List<OWLLogicalAxiom> extract(List<AxiomListItem> selectedValuesList) {
        return selectedValuesList.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
    }

    public void doRemoveTestcase(Set<OWLLogicalAxiom> testcases, TestcaseType type) {
        this.testcases.removeTestcase(testcases, type);
        if (isSessionRunning())
            doCalculateDiagnosesAndGetQuery(new ErrorHandler());
        notifyListeners();
    }

    public void doAddTestcase(Set<OWLLogicalAxiom> testcaseAxioms, TestcaseType type, ErrorHandler errorHandler) {
        this.testcases.addTestcase(testcaseAxioms, type);
        if(!getErrorStatus().equals(NO_ERROR))
            errorHandler.errorHappend(getErrorStatus());
        notifyListeners();
    }

    /**
     * Check if the set of new acquired test cases is empty. This check is called by the ResetDebuggingSessionAction.
     *
     * @return <code>true</code> if there are no acquired test cases yet, otherwise <code>false</code>.
     */
    public boolean areTestcasesEmpty() {
        return testcases.areTestcasesEmpty();
    }

    public void updateConfig(SearchConfiguration newConfiguration) {
        getDiagnosisEngineFactory().updateConfig(newConfiguration);
    }

    public void doAddAxiomsMarkedEntailed(OWLLogicalAxiom axiom) {
        this.answer.positive.add(axiom);
        notifyListeners();
    }

    public void doAddAxiomsMarkedNonEntailed(OWLLogicalAxiom axiom) {
        this.answer.negative.add(axiom);
        notifyListeners();
    }

    public void doRemoveAxiomsMarkedEntailed(OWLLogicalAxiom axiom) {
        this.answer.positive.remove(axiom);
        notifyListeners();
    }

    public void doRemoveAxiomsMarkedNonEntailed(OWLLogicalAxiom axiom) {
        this.answer.negative.remove(axiom);
        notifyListeners();
    }

    private void doCommitQuery() {
        if (!this.answer.positive.isEmpty()) {
            doAddTestcase(new TreeSet<>(this.answer.positive),
                    TestcaseType.ACQUIRED_ENTAILED_TC, new ErrorHandler());
            addToQueryHistory(this.answer.positive, TestcaseType.ACQUIRED_ENTAILED_TC);
        }
        if (!this.answer.negative.isEmpty()) {
            doAddTestcase(new LinkedHashSet<>(this.answer.negative),
                    TestcaseType.ACQUIRED_NON_ENTAILED_TC, new ErrorHandler());
            addToQueryHistory(this.answer.negative, TestcaseType.ACQUIRED_NON_ENTAILED_TC);
        }
        resetQuery();
        notifyListeners();
    }

    private void doCalculateDiagnosis(ErrorHandler errorHandler) {
        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();

        diagnosisEngine.resetEngine();

        // set the cost estimator
        if (diagnosisEngine instanceof AbstractDiagnosisEngine) {
            final AbstractDiagnosisEngine abstractDiagnosisEngine = ((AbstractDiagnosisEngine) diagnosisEngine);
            final ICostsEstimator currentCostsEstimator = abstractDiagnosisEngine.getCostsEstimator();
            switch (diagnosisEngineFactory.getSearchConfiguration().costEstimator) {
                case EQUAL:
                    if (! (currentCostsEstimator instanceof SimpleCostsEstimator))
                        abstractDiagnosisEngine.setCostsEstimator(new SimpleCostsEstimator());
                    break;
                case CARD:
                    if (! (currentCostsEstimator instanceof CardinalityCostEstimator))
                        abstractDiagnosisEngine.setCostsEstimator(new CardinalityCostEstimator());
                    break;
                case SYNTAX:
                    if (! (currentCostsEstimator instanceof OWLAxiomKeywordCostsEstimator))
                        abstractDiagnosisEngine.setCostsEstimator(new OWLAxiomKeywordCostsEstimator(diagnosisEngine.getSolver().getDiagnosisModel()));
                    break;
                default:
                    logger.warn("Cost estimator " + diagnosisEngineFactory.getSearchConfiguration().costEstimator + " is unknown. Using " + currentCostsEstimator + " as cost estimator.");
            };

        }

        // set the maximum number of diagnoses to be calculated
        final int n = diagnosisEngineFactory.getSearchConfiguration().numOfLeadingDiags;
        diagnosisEngine.setMaxNumberOfDiagnoses(n);
        try {
            logger.debug("maxNumberOfDiagnoses: " + n);
            logger.debug("diagnosisEngine: " + diagnosisEngine);
            logger.debug("solver: " + diagnosisEngine.getSolver());
            logger.debug("diagnosisModel: " + diagnosisEngine.getSolver().getDiagnosisModel());
            logger.debug("start searching maximal " + n + " diagnoses ...");
            diagnoses = diagnosisEngine.calculateDiagnoses();
            logger.debug("found these " + diagnoses.size() + " diagnoses: " + diagnoses);
            logger.debug("based on these conflicts: " + diagnosisEngine.getConflicts());
            notifyListeners();
        } catch (DiagnosisException e) {
            errorHandler.errorHappend(SOLVER_EXCEPTION);
        }

    }

    /**
     * First calculate diagnoses and compute queries afterwards.
     *
     * @param errorHandler The error handler.
     */
    private void doCalculateDiagnosesAndGetQuery(ErrorHandler errorHandler) {
        doCalculateDiagnosis(errorHandler);
        if (diagnoses.size() > 1)
            doGetQuery(errorHandler);
    }

    /**
     * The main method to calculate a new query.
     * The calling method has to check that the size of diagnoses is at least 2.
     *
     * @param errorHandler An error handler.
     */
    private void doGetQuery(ErrorHandler errorHandler) {

        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
        final SearchConfiguration preference = diagnosisEngineFactory.getSearchConfiguration();
        HeuristicConfiguration<OWLLogicalAxiom> heuristicConfiguration = new HeuristicConfiguration<>((AbstractDiagnosisEngine)diagnosisEngine);

        int minimalQueries = preference.minimalQueries;
        int maximalQueries = preference.maximalQueries;
        if (maximalQueries < minimalQueries) {
            maximalQueries = minimalQueries;
            preference.maximalQueries = maximalQueries;
        }

        heuristicConfiguration.setMinQueries(minimalQueries);
        heuristicConfiguration.setMaxQueries(maximalQueries);

        heuristicConfiguration.setEnrichQueries(preference.enrichQuery);
        switch (preference.rm) {
            case ENT:
                heuristicConfiguration.setRm(
                        new EntropyBasedMeasure<>(new BigDecimal(String.valueOf(preference.entropyThreshold))));
                break;
            case SPL:
                heuristicConfiguration.setRm(
                        new SplitInHalfMeasure<>(new BigDecimal(String.valueOf(preference.entropyThreshold))));
                break;
            case RIO:

                heuristicConfiguration.setRm(
                        new RiskOptimizationMeasure<>(new BigDecimal(String.valueOf(preference.entropyThreshold)),
                        new BigDecimal(String.valueOf(preference.cardinalityThreshold)),
                        new BigDecimal(String.valueOf(updateCautiousParameter(preference.cautiousParameter)))));
                break;
            case KL:
                heuristicConfiguration.setRm(
                        new KLMeasure(new BigDecimal(String.valueOf(preference.entropyThreshold))));
                break;
            case EMCb:
                heuristicConfiguration.setRm(new EMCbMeasure());
                break;
            case BME:
                heuristicConfiguration.setRm(
                        new BMEMeasure(new BigDecimal(String.valueOf(preference.cardinalityThreshold))));
                break;
            default:
                errorHandler.errorHappend(ErrorStatus.UNKNOWN_RM);
        }

        switch (preference.sortCriterion) {
            case MINCARD:
                heuristicConfiguration.setSortCriterion(new MinQueryCardinality<>());
                break;
            case MINSUM:
                heuristicConfiguration.setSortCriterion(new MinSumFormulaWeights<>(new HashMap<>())); // TODO find a method to automatically calculate formula weights
                break;
            case MINMAX:
                heuristicConfiguration.setSortCriterion(new MinMaxFormulaWeights<>(new HashMap<>())); // TODO find a method to automatically calculate formula weights
                break;
            default:
                errorHandler.errorHappend(ErrorStatus.UNKNOWN_SORTCRITERION);
        }

        qc = new HeuristicQueryComputation<>(heuristicConfiguration);

        try {
            qc.initialize(diagnoses);

            if ( qc.hasNext()) {
                this.answer.query = qc.next();
                logger.debug("query configuration: " + qc);
            } else {
                errorHandler.errorHappend(ErrorStatus.NO_QUERY);
                errorStatus = ErrorStatus.NO_QUERY;
                resetQuery();
            }
            querySearchStatus = QuerySearchStatus.ASKING_QUERY;

        } catch (DiagnosisException e) {
            errorHandler.errorHappend(ErrorStatus.SOLVER_EXCEPTION);
        } finally {
            notifyListeners();
        }
    }

    /**
     * Method that learns the cautious parameter for RIO for each new query generation.
     *
     * @param preferenceCautiousParameter the unmodifiable cautious parameter from the preferences.
     */
    private Double updateCautiousParameter(Double preferenceCautiousParameter) {
        if (previousDiagnoses != null) {
            previousCautiousParameter = cautiousParameter;
            final double epsilon = 0.25;
            final double intervalLength = (Math.floor((double)previousDiagnoses.size() / 2d) - 1d) / (double)previousDiagnoses.size();

            logger.debug("epsilon: " + epsilon);
            logger.debug("intervalLength: " + intervalLength);
            logger.debug("old cautiousParameter: " + previousCautiousParameter);
            logger.debug("previousDiagnoses#: " + previousDiagnoses.size());
            logger.debug("diagnoses#: " + diagnoses.size());

            double eliminationRate = calculateEliminationRate();
            logger.debug("eliminationRate: " + eliminationRate);

            double adjustmentFactor = ((Math.floor((double)previousDiagnoses.size() / 2.0 - epsilon) + 0.5) / (double)previousDiagnoses.size()) - eliminationRate;
            logger.debug("adjustmentFactor: " + adjustmentFactor);

            double adjustedCautiousParameter = previousCautiousParameter + (2 * intervalLength * adjustmentFactor);
            final double minCautiousValue = 1.0 / (double) diagnoses.size();
            final double maxCautiousValue = Math.floor((double)diagnoses.size() / 2.0) / (double)diagnoses.size();

            logger.debug(adjustedCautiousParameter + " in [" + minCautiousValue + "," + maxCautiousValue + "] ?");

            if (adjustedCautiousParameter < minCautiousValue)
                adjustedCautiousParameter = minCautiousValue;
            if (adjustedCautiousParameter > maxCautiousValue)
                adjustedCautiousParameter = maxCautiousValue;

            cautiousParameter = new Double(adjustedCautiousParameter);
            logger.debug("NEW cautiousParameter: " + cautiousParameter);

        } else {
            cautiousParameter = preferenceCautiousParameter;
            previousCautiousParameter = null;
        }
        return cautiousParameter;
    }

    private Double calculateEliminationRate() {
        /*
        Set<Diagnosis<OWLLogicalAxiom>> previousDiagnosesMinusDiagnoses = new HashSet<>(previousDiagnoses);
        previousDiagnosesMinusDiagnoses.removeAll(diagnoses);
        return ((double)(previousDiagnosesMinusDiagnoses.size())) / (double)previousDiagnoses.size();
        */

        Double eliminationRate = null;

        if (this.previousAnswer.negative.size() >= 1)
            // this calculates the lower bound of the actual elimination rate
            eliminationRate = (double)this.previousAnswer.query.qPartition.dx.size() / (double)previousDiagnoses.size();
        else if (this.previousAnswer.positive.size() == this.previousAnswer.query.formulas.size())
            // this calculates the lower bound of the actual elimination rate
            eliminationRate = (double)this.previousAnswer.query.qPartition.dnx.size() / (double)previousDiagnoses.size();
        else
            // an approximation of the elimination rate (saves the costs to expensive reasoner calls)
            eliminationRate = ( (double)this.previousAnswer.query.qPartition.dnx.size() / (double)previousDiagnoses.size() ) * this.previousAnswer.positive.size() / this.previousAnswer.query.formulas.size();

        return eliminationRate;
    }

    public void doGetAlternativeQuery() {
        /* TODO
        if (qc != null && qc.hasNext()) {
            querySearchStatus = QuerySearchStatus.ASKING_QUERY;
            notifyListeners();
            actualQuery = qc.next();
        } else {
            errorStatus = ErrorStatus.NO_QUERY;
            resetQuery();
            notifyListeners();
        }
        */

        JOptionPane.showMessageDialog(null, "The function is not implemented yet", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addToQueryHistory(Set<OWLLogicalAxiom> ax, TestcaseType type) {
        LinkedHashSet<OWLLogicalAxiom> axioms = new LinkedHashSet<>(ax);
        queryHistory.add(axioms);
        queryHistoryType.put(axioms,type);
    }

    public void doRemoveQueryHistoryTestcase(Set<OWLLogicalAxiom> testcase, TestcaseType type) {
        doRemoveTestcase(testcase,type);
        queryHistory.remove(testcase);
        queryHistoryType.remove(testcase);
        notifyListeners();
    }

    public void updateProbab(Map<ManchesterOWLSyntax, BigDecimal> map) {
        // TODO
        /*
        CostsEstimator<OWLLogicalAxiom> estimator = getSearchCreator().getSearch().getCostsEstimator();
        ((OWLAxiomKeywordCostsEstimator)estimator).updateKeywordProb(map);
        */
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OntologyDiagnosisSearcher{");
        sb.append("engine=").append(diagnosisEngineFactory.getDiagnosisEngine());
        sb.append("ontology=").append(diagnosisEngineFactory.getOntology());
        sb.append("reasonerManager=").append(diagnosisEngineFactory.getReasonerManager());
        sb.append('}');
        return sb.toString();
    }


}
