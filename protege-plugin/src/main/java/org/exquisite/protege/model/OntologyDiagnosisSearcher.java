package org.exquisite.protege.model;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.RiskOptimizationMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.SplitInHalfMeasure;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinMaxFormulaWeights;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinQueryCardinality;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinSumFormulaWeights;
import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.exquisite.protege.model.configuration.SearchConfiguration;
import org.exquisite.protege.model.error.ErrorHandler;
import org.exquisite.protege.ui.list.AxiomListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
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

    public enum TestCaseType {CONSISTENT_TC, INCONSISTENT_TC, ENTAILED_TC, NON_ENTAILED_TC}

    public enum ErrorStatus {NO_CONFLICT_EXCEPTION, SOLVER_EXCEPTION, INCONSISTENT_THEORY_EXCEPTION,
        NO_QUERY, ONLY_ONE_DIAG, NO_ERROR, UNKNOWN_RM, UNKNOWN_SORTCRITERION}

    public enum QuerySearchStatus { IDLE, SEARCH_DIAG, GENERATING_QUERY, MINIMZE_QUERY, ASKING_QUERY }

    private enum SearchStatus { IDLE, RUNNING }

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;

    private DiagnosisEngineFactory diagnosisEngineFactory;

    private ErrorStatus errorStatus = NO_ERROR;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<>();

    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new HashSet<>();

    private Query<OWLLogicalAxiom> actualQuery;

    private Set<OWLLogicalAxiom> axiomsMarkedEntailed = new LinkedHashSet<>();

    private Set<OWLLogicalAxiom> axiomsMarkedNonEntailed = new LinkedHashSet<>();

    private List<Set<OWLLogicalAxiom>> queryHistory = new LinkedList<>();

    private Map<Set<OWLLogicalAxiom>,TestCaseType> queryHistoryType = new LinkedHashMap<>();

    private IQueryComputation<OWLLogicalAxiom> qc = null;

    public OntologyDiagnosisSearcher(OWLEditorKit editorKit) {
        OWLReasonerManager reasonerMan = editorKit.getModelManager().getOWLReasonerManager();
        OWLOntology ontology = editorKit.getModelManager().getActiveOntology();
        diagnosisEngineFactory = new DiagnosisEngineFactory(ontology, reasonerMan);
    }

    public DiagnosisEngineFactory getDiagnosisEngineFactory() {
        return diagnosisEngineFactory;
    }

    public void removeBackgroundAxioms(List<AxiomListItem> selectedValues) {
        logger.debug("moving " + selectedValues + " from background to possiblyFaultyFormulas");
        List<OWLLogicalAxiom> axioms = extract(selectedValues);
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel();
        diagnosisModel.getCorrectFormulas().removeAll(axioms);
        diagnosisModel.getPossiblyFaultyFormulas().addAll(axioms);
        notifyListeners();
    }

    public void addBackgroundAxioms(List<AxiomListItem> selectedValues) {
        logger.debug("moving " + selectedValues + " from possiblyFaultyFormulas to background");
        List<OWLLogicalAxiom> axioms = extract(selectedValues);
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel();
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(axioms);
        diagnosisModel.getCorrectFormulas().addAll(axioms);
        notifyListeners();
    }

    private List<OWLLogicalAxiom> extract(List<AxiomListItem> selectedValuesList) {
        return selectedValuesList.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
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

    public QuerySearchStatus getQuerySearchStatus() {
        return querySearchStatus;
    }

    public void doRemoveTestcase(Set<OWLLogicalAxiom> testcases, TestCaseType type) {
        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();

        switch (type) {
            case CONSISTENT_TC:
                diagnosisModel.getConsistentExamples().removeAll(testcases);
                break;
            case INCONSISTENT_TC:
                diagnosisModel.getInconsistentExamples().removeAll(testcases);
                break;
            case ENTAILED_TC:
                diagnosisModel.getEntailedExamples().removeAll(testcases);
                break;
            case NON_ENTAILED_TC:
                diagnosisModel.getNotEntailedExamples().removeAll(testcases);
                break;
        }

        notifyListeners();
    }

    public void doAddTestcase(Set<OWLLogicalAxiom> testcase, TestCaseType type, ErrorHandler errorHandler) {
        addNewTestcase(testcase,type);
        if(!getErrorStatus().equals(NO_ERROR))
            errorHandler.errorHappend(getErrorStatus());
        notifyListeners();
    }

    private void addNewTestcase(Set<OWLLogicalAxiom> axioms, TestCaseType type) {

        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().getDiagnosisModel();

        switch(type) {
            case ENTAILED_TC:
                diagnosisModel.getEntailedExamples().addAll(axioms);
                break;
            case NON_ENTAILED_TC:
                diagnosisModel.getNotEntailedExamples().addAll(axioms);
                break;
        }
    }

    public void doUpdateTestcase(Set<OWLLogicalAxiom> testcase, Set<OWLLogicalAxiom> testcase1, TestCaseType type, ErrorHandler errorHandler) {
    }

    public boolean isTestcasesEmpty() {
        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();
        return diagnosisModel.getEntailedExamples().isEmpty() && diagnosisModel.getNotEntailedExamples().isEmpty();
    }

    public void doCalculateDiagnosis(ErrorHandler errorHandler) {
        int n = diagnosisEngineFactory.getSearchConfiguration().numOfLeadingDiags;

        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();

        logger.debug("diagnoses before resetEngine() " + diagnoses);
        diagnosisEngine.resetEngine();
        logger.debug("diagnoses after resetEngine() " + diagnoses);

        diagnosisEngine.setMaxNumberOfDiagnoses(n);
        try {
            logger.debug("maxNumberOfDiagnoses: " + n);
            logger.debug("diagnosisEngine: " + diagnosisEngine);
            logger.debug("solver: " + diagnosisEngine.getSolver());
            logger.debug("diagnosisModel: " + diagnosisEngine.getSolver().getDiagnosisModel());
            logger.debug("start searching maximal " + n + " diagnoses ...");
            diagnoses = diagnosisEngine.calculateDiagnoses();
            logger.debug("found these " + diagnoses.size() + " diagnoses: " + diagnoses);
            setDiagnosesMeasures(diagnoses);
            notifyListeners();

            if (diagnoses.size() == 0)
                JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);

        } catch (DiagnosisException e) {
            errorHandler.errorHappend(SOLVER_EXCEPTION);
        }

    }

    public void updateConfig(SearchConfiguration newConfiguration) {
        getDiagnosisEngineFactory().updateConfig(newConfiguration);
    }

    public Query<OWLLogicalAxiom> getActualQuery() {
        return actualQuery;
    }

    public boolean isMarkedEntailed(OWLLogicalAxiom axiom) {
        return axiomsMarkedEntailed.contains(axiom);
    }

    public boolean isMarkedNonEntailed(OWLLogicalAxiom axiom) {
        return axiomsMarkedNonEntailed.contains(axiom);
    }

    public void doAddAxiomsMarkedEntailed(OWLLogicalAxiom axiom) {
        axiomsMarkedEntailed.add(axiom);
        notifyListeners();
    }

    public void doAddAxiomsMarkedNonEntailed(OWLLogicalAxiom axiom) {
        axiomsMarkedNonEntailed.add(axiom);
        notifyListeners();
    }

    public void doRemoveAxiomsMarkedEntailed(OWLLogicalAxiom axiom) {
        axiomsMarkedEntailed.remove(axiom);
        notifyListeners();
    }

    public void doRemoveAxiomsMarkedNonEntailed(OWLLogicalAxiom axiom) {
        axiomsMarkedNonEntailed.remove(axiom);
        notifyListeners();
    }

    public void reasonerChanged() {
        getDiagnosisEngineFactory().reasonerChanged();
        doReset();
    }

    public void doReset() {
        resetQuery();
        resetQueryHistory();
        resetDiagnoses();
        notifyListeners();
        logger.debug("searcher: do reset");
    }

    public void doFullReset() {
        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();
        diagnosisModel.getNotEntailedExamples().clear();
        diagnosisModel.getEntailedExamples().clear();
        doReset();
    }

    private void doCommitQuery() {
        if (!axiomsMarkedEntailed.isEmpty()) {
            doAddTestcase(new LinkedHashSet<>(axiomsMarkedEntailed),
                    TestCaseType.ENTAILED_TC, new ErrorHandler());
            addToQueryHistory(axiomsMarkedEntailed,TestCaseType.ENTAILED_TC);
        }
        if (!axiomsMarkedNonEntailed.isEmpty()) {
            doAddTestcase(new LinkedHashSet<>(axiomsMarkedNonEntailed),
                    TestCaseType.NON_ENTAILED_TC, new ErrorHandler());
            addToQueryHistory(axiomsMarkedNonEntailed,TestCaseType.NON_ENTAILED_TC);
        }
        resetQuery();
        notifyListeners();
    }

    private void resetQuery() {
        axiomsMarkedEntailed.clear();
        axiomsMarkedNonEntailed.clear();
        actualQuery=null;
        querySearchStatus = QuerySearchStatus.IDLE;
        if (qc!=null) qc.reset();
    }

    private void resetQueryHistory() {
        queryHistory.clear();
        queryHistoryType.clear();
    }

    private void resetDiagnoses() {
        diagnoses.clear();

        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
        diagnosisEngine.resetEngine();
    }

    public void doCommitAndGetNewQuery(ErrorHandler errorHandler) {
        doCommitQuery();
        doCalculateDiagnosis(errorHandler);

        switch (diagnoses.size()) {
            case 0:
                JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "The diagnosis corresponding to your preferences (test cases) is found!", "Diagnosis found!", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                doGetQuery(errorHandler);
                break;
        }
    }

    /**
     * Calculates a query according to the diagnoses.
     * If no diagnoses have been computed yet, let us compute them first.
     *
     * @param errorHandler An error handler.
     */
    public void doCalculateDiagnosesAndGetQuery(ErrorHandler errorHandler) {
        if (diagnoses.size() == 0)
            doCalculateDiagnosis(errorHandler);

        switch (diagnoses.size()) {
            case 0:
                JOptionPane.showMessageDialog(null, "Your ontology is OK! Nothing to debug.", "Consistent ontology!", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "The diagnosis corresponding to your preferences (test cases) is found!", "Diagnosis found!", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                doGetQuery(errorHandler);
                break;
        }
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
                        new BigDecimal(String.valueOf(preference.cautiousParameter))));
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
                actualQuery = qc.next();
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

    private void setDiagnosesMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        int i = 1;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            BigDecimal measure = new BigDecimal(i++);
            diagnosis.setMeasure(measure);
            logger.debug("set measure " + measure + " for diagnosis " + diagnosis);
        }
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

    private void addToQueryHistory(Set<OWLLogicalAxiom> ax, TestCaseType type) {
        LinkedHashSet<OWLLogicalAxiom> axioms = new LinkedHashSet<OWLLogicalAxiom>(ax);
        queryHistory.add(axioms);
        queryHistoryType.put(axioms,type);
    }

    public List<Set<OWLLogicalAxiom>> getQueryHistory() {
        return queryHistory;
    }

    public Map<Set<OWLLogicalAxiom>, TestCaseType> getQueryHistoryType() {
        return queryHistoryType;
    }

    public void doRemoveQueryHistoryTestcase(Set<OWLLogicalAxiom> testcase, TestCaseType type) {
        doRemoveTestcase(testcase,type);
        queryHistory.remove(testcase);
        queryHistoryType.remove(testcase);
        notifyListeners();
    }

    private ErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public void updateProbab(Map<ManchesterOWLSyntax, BigDecimal> map) {
        // TODO
        /*
        CostsEstimator<OWLLogicalAxiom> estimator = getSearchCreator().getSearch().getCostsEstimator();
        ((OWLAxiomKeywordCostsEstimator)estimator).updateKeywordProb(map);
        */
    }

    public Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
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
