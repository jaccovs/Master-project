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

    public static enum TestCaseType {POSITIVE_TC, NEGATIVE_TC, ENTAILED_TC, NON_ENTAILED_TC}

    public static enum ErrorStatus {NO_CONFLICT_EXCEPTION, SOLVER_EXCEPTION, INCONSISTENT_THEORY_EXCEPTION,
        NO_QUERY, ONLY_ONE_DIAG, NO_ERROR, UNKNOWN_RM, UNKNOWN_SORTCRITERION}

    public static enum QuerySearchStatus { IDLE, SEARCH_DIAG, GENERATING_QUERY, MINIMZE_QUERY, ASKING_QUERY }

    public static enum SearchStatus { IDLE, RUNNING }

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;

    private DiagnosisEngineFactory diagnosisEngineFactory;

    private OWLReasonerManager reasonerMan;

    private OWLOntology ontology;

    private SearchStatus searchStatus = SearchStatus.IDLE;

    private ErrorStatus errorStatus = NO_ERROR;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<>();

    public Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
    }

    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new HashSet<>();

    private Query<OWLLogicalAxiom> actualQuery;

    private Set<OWLLogicalAxiom> axiomsMarkedEntailed = new LinkedHashSet<>();

    private Set<OWLLogicalAxiom> axiomsMarkedNonEntailed = new LinkedHashSet<>();

    private List<Set<OWLLogicalAxiom>> queryHistory = new LinkedList<>();

    private Map<Set<OWLLogicalAxiom>,TestCaseType> queryHistoryType = new LinkedHashMap<>();


    public OntologyDiagnosisSearcher(OWLEditorKit editorKit) {
        reasonerMan = editorKit.getModelManager().getOWLReasonerManager();
        ontology = editorKit.getModelManager().getActiveOntology();


        diagnosisEngineFactory = new DiagnosisEngineFactory(ontology, reasonerMan);
    }

    public DiagnosisEngineFactory getDiagnosisEngineFactory() {
        return diagnosisEngineFactory;
    }

    protected List<OWLLogicalAxiom> extract(List<AxiomListItem> selectedValuesList) {
        List<OWLLogicalAxiom> axioms = selectedValuesList.stream().map(AxiomListItem::getAxiom).collect(Collectors.toList());
        return axioms;
    }

    public void removeBackgroundAxioms(List<AxiomListItem> selectedValues) {
        logger.debug("moving " + selectedValues + " from background to possiblyFaultyFormulas");
        List<OWLLogicalAxiom> axioms = extract(selectedValues);
        diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getCorrectFormulas().removeAll(axioms);
        diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getPossiblyFaultyFormulas().addAll(axioms);
        notifyListeners();
    }

    public void addBackgroundAxioms(List<AxiomListItem> selectedValues) {
        logger.debug("moving " + selectedValues + " from possiblyFaultyFormulas to background");
        List<OWLLogicalAxiom> axioms = extract(selectedValues);
        diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getPossiblyFaultyFormulas().removeAll(axioms);
        diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getCorrectFormulas().addAll(axioms);
        notifyListeners();
    }

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void notifyListeners() {
        for (ChangeListener listener : changeListeners)
            listener.stateChanged(new ChangeEvent(this));
    }

    public QuerySearchStatus getQuerySearchStatus() {
        return querySearchStatus;
    }

    public void doRemoveTestcase(Set<OWLLogicalAxiom> testcase, TestCaseType type) {
    }

    public void doAddTestcase(Set<OWLLogicalAxiom> testcase, TestCaseType type, ErrorHandler errorHandler) {
        addNewTestcase(testcase,type);
        if(!getErrorStatus().equals(NO_ERROR))
            errorHandler.errorHappend(getErrorStatus());
        notifyListeners();
    }

    protected void addNewTestcase(Set<OWLLogicalAxiom> axioms, TestCaseType type) {

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


    public void doCalculateDiagnosis(ErrorHandler errorHandler) {
        int n = diagnosisEngineFactory.getSearchConfiguration().numOfLeadingDiags;

        /*if (diagnosisEngineFactory.getSearchConfiguration().calcAllDiags)
            n = -1;
        */
        //new SearchThread(diagnosisEngineFactory.getDiagnosisEngine(), n, errorHandler).execute();

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
            notifyListeners();
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

    public void doCommitQuery() {
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

    protected void addToQueryHistory(Set<OWLLogicalAxiom> ax, TestCaseType type) {
        LinkedHashSet<OWLLogicalAxiom> axioms = new LinkedHashSet<OWLLogicalAxiom>(ax);
        queryHistory.add(axioms);
        queryHistoryType.put(axioms,type);
    }

    protected void resetQuery() {
        axiomsMarkedEntailed.clear();
        axiomsMarkedNonEntailed.clear();
        actualQuery=null;
        querySearchStatus = QuerySearchStatus.IDLE;
        qc.reset();
    }

    public void doCommitAndGetNewQuery(ErrorHandler errorHandler) {
        doCommitQuery();
        doCalculateDiagnosis(errorHandler);
        doGetQuery(errorHandler);
    }

    IQueryComputation<OWLLogicalAxiom> qc = null;

    public void doGetQuery(ErrorHandler errorHandler) {

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

            setDiagnosesMeasures(diagnoses);
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


        //new QueryGenerationThread(getSearchCreator(), errorHandler).execute();
    }

    protected void setDiagnosesMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        int i = 1;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {

            BigDecimal measure = new BigDecimal(i++);
            if (measure != null) {
                diagnosis.setMeasure(measure);
                System.out.println("set measure " + measure + " for diagnosis " + diagnosis);
            }
        }
    }

    public void doGetAlternativeQuery() {
        /*
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

    protected ErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public void updateProbab(Map<ManchesterOWLSyntax, BigDecimal> map) {
        // TODO
        /*
        CostsEstimator<OWLLogicalAxiom> estimator = getSearchCreator().getSearch().getCostsEstimator();
        ((OWLAxiomKeywordCostsEstimator)estimator).updateKeywordProb(map);
        */
    }
/*
    public class SearchThread extends SwingWorker<Object,Object> implements ChangeListener {

        private IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine;

        private int number;

        private ErrorHandler errorHandler;

        public SearchThread(IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine, int number, ErrorHandler errorHandler) {
            this.number = number;
            this.errorHandler = errorHandler;
            this.diagnosisEngine = diagnosisEngine;
        }

        @Override
        public Object doInBackground() {
            //diagnosisEngine.addSearchListener(this);
            searchStatus = SearchStatus.RUNNING;
            publish(new Object());
            try {
                diagnosisEngine.setMaxNumberOfDiagnoses(number);
                logger.debug("diagnosisEngine: " + diagnosisEngine);
                logger.debug("solver: " + diagnosisEngine.getSolver());
                logger.debug("diagnosisModel: " + diagnosisEngine.getSolver().getDiagnosisModel());
                diagnoses = diagnosisEngine.calculateDiagnoses();
                logger.debug("diagnoses: " + diagnoses);
                errorStatus = NO_ERROR;
            } catch (DiagnosisException e) {
                errorStatus = SOLVER_EXCEPTION;
            } finally {
                diagnosisEngine.resetEngine();
            }
            searchStatus = SearchStatus.IDLE;
            if (!errorStatus.equals(NO_ERROR))
                errorHandler.errorHappend(errorStatus);

            publish(new Object());
            //diagnosisEngine.removeSearchListener(this);

            return null;
        }

        @Override
        protected void process(List<Object> chunks) {
            notifyListeners();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            publish(new Object());
        }

    }
*/
}
