package org.exquisite.protege.model;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.DiagnosisRuntimeException;
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
import org.exquisite.protege.model.error.AbstractErrorHandler;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.list.AxiomListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
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

    /**
     * When calling doStopSession() use this flag to inform the user, why the debugging session has stopped.
     */
    public enum SessionStopReason { PREFERENCES_CHANGED, INVOKED_BY_USER, CONSISTENT_ONTOLOGY,
        ERROR_OCCURRED, DEBUGGER_RESET, REASONER_CHANGED, ONTOLOGY_RELOADED, ONTOLOGY_CHANGED, SESSION_RESTARTED };

    private DebuggingSession debuggingSession;

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;

    private DiagnosisEngineFactory diagnosisEngineFactory;

    private ErrorStatus errorStatus = NO_ERROR;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new HashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> previousDiagnoses = null;

    private Answer<OWLLogicalAxiom> answer = new Answer<>();

    private Answer<OWLLogicalAxiom> previousAnswer = new Answer<>();

    private List<Answer<OWLLogicalAxiom>> queryHistory = new LinkedList<>();

    private IQueryComputation<OWLLogicalAxiom> qc = null;

    private final OWLModelManager modelManager;

    private final OWLReasonerManager reasonerManager;

    private Double cautiousParameter, previousCautiousParameter;

    private TestcasesModel testcases;

    /**
     * Singleton instance of an listener to ontology changes. Registered in the EditorKitHook.
     */
    private OntologyChangeListener ontologyChangeListener;

    public OntologyDiagnosisSearcher(OWLEditorKit editorKit) {
        modelManager = editorKit.getModelManager();
        reasonerManager = modelManager.getOWLReasonerManager();
        OWLOntology ontology = modelManager.getActiveOntology();
        diagnosisEngineFactory = new DiagnosisEngineFactory(this, ontology, reasonerManager);
        debuggingSession = new DebuggingSession();
        this.testcases = new TestcasesModel(this);
    }

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

    public List<Answer<OWLLogicalAxiom>> getQueryHistory() {
        return queryHistory;
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

    public boolean isSessionStopped() {
        return debuggingSession.getState() == DebuggingSession.State.STOPPED;
    }

    public TestcasesModel getTestcases() {
        return this.testcases;
    }

    void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    private void notifyListeners() {
        for (ChangeListener listener : changeListeners)
            listener.stateChanged(new ChangeEvent(this));
    }

    OntologyChangeListener getOntologyChangeListener() {
        if (this.ontologyChangeListener == null)
            this.ontologyChangeListener = new OntologyChangeListener(this);
        return this.ontologyChangeListener;
    }

    /**
     * Starts a new debugging session. This step initiates the search for a diagnosis and the presentation of a
     * query.
     *
     * @param errorHandler An error handler.
     */
    public void doStartDebugging(QueryErrorHandler errorHandler) {
        if (!isSessionRunning()) {
            if (reasonerManager.getReasonerStatus() == ReasonerStatus.NO_REASONER_FACTORY_CHOSEN) {
                JOptionPane.showMessageDialog(null, "No Reasoner set. Select a reasoner from the Reasoner menu.", "No Reasoner set", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            diagnosisEngineFactory.reset();                 // create new engine
            debuggingSession.startSession();                // start session
            doCalculateDiagnosesAndGetQuery(errorHandler);  // calculate diagnoses and compute query

            switch (diagnoses.size()) {
                case 0:
                    JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);
                    doStopDebugging(SessionStopReason.CONSISTENT_ONTOLOGY);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "The faulty axioms corresponding to your preferences (test cases) are found!", "Faulty Axioms found!", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    notifyListeners();
                    break;
            }
        }
    }

    /**
     * Stops the current running debugging session and restarts with another one.
     *
     * @param errorHandler An error handler.
     */
    public void doRestartDebugging(QueryErrorHandler errorHandler) {
        doStopDebugging(SessionStopReason.SESSION_RESTARTED);
        doStartDebugging(errorHandler);
        notifyListeners();
    }

    /**
     * Stop diagnosis session -> reset engine, diagnoses, conflicts, queries and history.
     */
    public void doStopDebugging(SessionStopReason reason) {
        if (isSessionRunning()) {
            diagnosisEngineFactory.dispose();
            //diagnosisEngineFactory.reset();                           // reset engine
            resetDiagnoses();                                           // reset diagnoses, conflicts
            resetQuery();                                               // reset queries
            resetQueryHistory();                                        // reset history
            testcases.reset();
            debuggingSession.stopSession();                             // stop session
            this.cautiousParameter = null;
            this.previousCautiousParameter = null;
            notifyListeners();

            // notify the user why session has stopped
            String msg = null;
            switch (reason) {
                case ERROR_OCCURRED:
                    msg = "An error occured!";
                    break;
                case ONTOLOGY_RELOADED:
                    msg = "The ontology has been reloaded.";
                    break;
                case ONTOLOGY_CHANGED:
                    msg = "The ontology has been modified!";
                    break;
                case PREFERENCES_CHANGED:
                    msg = "Preferences have been modified!";
                    break;
                case REASONER_CHANGED:
                    msg = "Reasoner has been changed!";
                    break;
                case INVOKED_BY_USER:     // no message necessary
                case DEBUGGER_RESET:      // no message necessary
                case CONSISTENT_ONTOLOGY: // no message necessary
                case SESSION_RESTARTED:   // no message necessary
                    break;
                default:
                    msg = reason.toString();
                    logger.warn("Unknown SessionStopReason: " + reason);
            }

            if (msg != null)
                JOptionPane.showMessageDialog(null, "Your running debugging session has been stopped!\n" +
                        "\nOntology: " + this.getDiagnosisEngineFactory().getOntology().getOntologyID() +
                        "\nReason: " + msg, "Debugging Session has been stopped!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Reset debugger ->  reset test cases + doStopDebugging()
     */
    public void doResetDebugger() {
        doStopDebugging(SessionStopReason.DEBUGGER_RESET);
    }

    /**
     * Reset the diagnoses engine and doFullReset().
     */
    void doReload() {
        doStopDebugging(SessionStopReason.ONTOLOGY_RELOADED);
    }

    private void resetQuery() {
        this.previousAnswer = this.answer;
        this.answer = new Answer<>();

        this.querySearchStatus = QuerySearchStatus.IDLE;
        if (this.qc!=null) qc.reset();
    }

    private void resetQueryHistory() {
        this.queryHistory.clear();
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
        List<OWLLogicalAxiom> axioms = selectedCorrectAxioms.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
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
        List<OWLLogicalAxiom> axioms = selectedPossiblyFaultyAxioms.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel();
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(axioms);
        diagnosisModel.getCorrectFormulas().addAll(axioms);
        notifyListeners();
    }

    public void doRemoveTestcase(Set<OWLLogicalAxiom> axioms, TestcaseType type) {
        this.testcases.removeTestcase(axioms, type);

        // We also have to synchronize the query history (if the user removed the test case from the acquired test cases)
        if (axioms.size() == 1 && (type == TestcaseType.ACQUIRED_ENTAILED_TC || type == TestcaseType.ACQUIRED_NON_ENTAILED_TC)) {
            // It is possible that the user used the remove button in the acquired test cases view
            // Now search for the answer in the history
            Answer<OWLLogicalAxiom> answer = null;
            Iterator<Answer<OWLLogicalAxiom>> it = this.queryHistory.iterator();
            while (it.hasNext() && answer == null) {
                Answer<OWLLogicalAxiom> anAnswer = it.next();
                switch (type) {
                    case ACQUIRED_ENTAILED_TC:
                        if (anAnswer.positive.removeAll(axioms)) // note that only one axiom is removed
                            answer = anAnswer;
                        break;
                    case ACQUIRED_NON_ENTAILED_TC:
                        if (anAnswer.negative.removeAll(axioms)) // note that only one axiom is removed
                            answer = anAnswer;
                        break;
                    default:
                        throw new DiagnosisRuntimeException("Unexpected test case type used to clean up history: " + type);
                }
            }

            // we found an answer in the history we have to remove from history if the answer is empty
            if (answer != null && answer.positive.isEmpty() && answer.negative.isEmpty()) {
                this.queryHistory.remove(answer);
                logger.debug("Removed from history: " + answer);
            }

        }

        if (isSessionRunning())
            doCalculateDiagnosesAndGetQuery(new QueryErrorHandler());
        notifyListeners();
    }

    public void doAddTestcase(Set<OWLLogicalAxiom> axioms, TestcaseType type, AbstractErrorHandler errorHandler) {
        this.testcases.addTestcase(axioms, type);
        if(!getErrorStatus().equals(NO_ERROR))
            errorHandler.errorHappened(getErrorStatus());
        notifyListeners();
    }

    /**
     * Check if the set of new acquired test cases is empty. This check is called by the ResetDebuggerAction.
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

    /**
     * Commit the response from the expert, calculate the new diagnoses and get the new queries.
     *
     * @param errorHandler An error handler.
     */
    public void doCommitAndGetNewQuery(QueryErrorHandler errorHandler) {
        this.previousDiagnoses = new HashSet<>(this.diagnoses);
        doCommitQuery();
        boolean noErrorOccur = doCalculateDiagnoses(errorHandler);
        if (noErrorOccur) {
            switch (diagnoses.size()) {
                case 0:
                    JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);
                    doStopDebugging(SessionStopReason.CONSISTENT_ONTOLOGY);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "The faulty axioms corresponding to your preferences (test cases) are found!", "Faulty Axioms found!", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    doGetQuery(errorHandler);
                    break;
            }
        } else {
            doStopDebugging(SessionStopReason.ERROR_OCCURRED);
        }
    }

    public void doGetAlternativeQuery() {
        JOptionPane.showMessageDialog(null, "The function is not implemented yet", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doCommitQuery() {
        if (!this.answer.positive.isEmpty()) {
            doAddTestcase(this.answer.positive, TestcaseType.ACQUIRED_ENTAILED_TC, new QueryErrorHandler());
        }
        if (!this.answer.negative.isEmpty()) {
            doAddTestcase(this.answer.negative, TestcaseType.ACQUIRED_NON_ENTAILED_TC, new QueryErrorHandler());
        }

        this.queryHistory.add(this.answer);

        resetQuery();
        notifyListeners();
    }

    /**
     * Calculate the diagnoses.
     *
     * @param errorHandler An error handler
     * @return <code>true</code> if no error occurred, otherwise <code>false</code>.
     */
    private boolean doCalculateDiagnoses(AbstractErrorHandler errorHandler) {
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
            return true;
        } catch (DiagnosisException | ReasonerInternalException e) {
            errorHandler.errorHappened(SOLVER_EXCEPTION, e);
            logger.error("Exception occurred while calculating diagnoses.", e);
            return false;
        }

    }

    /**
     * First calculate diagnoses and compute queries afterwards.
     *
     * @param errorHandler The error handler.
     */
    private void doCalculateDiagnosesAndGetQuery(QueryErrorHandler errorHandler) {
        boolean noErrorOccurred = doCalculateDiagnoses(errorHandler);
        if (noErrorOccurred) {
            if (diagnoses.size() > 1)
                doGetQuery(errorHandler);
        } else {
            doStopDebugging(SessionStopReason.ERROR_OCCURRED);
        }
    }

    /**
     * The main method to calculate a new query.
     * The calling method has to check that the size of diagnoses is at least 2.
     *
     * @param errorHandler An error handler.
     */
    private void doGetQuery(QueryErrorHandler errorHandler) {

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
                errorHandler.errorHappened(ErrorStatus.UNKNOWN_RM);
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
                errorHandler.errorHappened(ErrorStatus.UNKNOWN_SORTCRITERION);
        }

        qc = new HeuristicQueryComputation<>(heuristicConfiguration);

        try {
            qc.initialize(diagnoses);

            if ( qc.hasNext()) {
                this.answer.query = qc.next();
                logger.debug("query configuration: " + qc);
            } else {
                errorHandler.errorHappened(ErrorStatus.NO_QUERY);
                errorStatus = ErrorStatus.NO_QUERY;
                resetQuery();
            }
            querySearchStatus = QuerySearchStatus.ASKING_QUERY;

        } catch (DiagnosisException e) {
            errorHandler.errorHappened(ErrorStatus.SOLVER_EXCEPTION);
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

            cautiousParameter = adjustedCautiousParameter;
            logger.debug("NEW cautiousParameter: " + cautiousParameter);

        } else {
            cautiousParameter = preferenceCautiousParameter;
            previousCautiousParameter = null;
        }
        return cautiousParameter;
    }

    private Double calculateEliminationRate() {
        Double eliminationRate;
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

    public void doRemoveQueryHistoryAnswer(Answer<OWLLogicalAxiom> answer) {
        queryHistory.remove(answer);
        doRemoveTestcase(answer.positive, TestcaseType.ACQUIRED_ENTAILED_TC);
        doRemoveTestcase(answer.negative, TestcaseType.ACQUIRED_NON_ENTAILED_TC);

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
        return "OntologyDiagnosisSearcher{" + "engine=" + diagnosisEngineFactory.getDiagnosisEngine() +
                "ontology=" + diagnosisEngineFactory.getOntology() +
                "reasonerManager=" + diagnosisEngineFactory.getReasonerManager() +
                '}';
    }


}
