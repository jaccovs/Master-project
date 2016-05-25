package org.exquisite.protege.model;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.ErrorStatus.NO_ERROR;
import static org.exquisite.protege.model.OntologyDiagnosisSearcher.ErrorStatus.SOLVER_EXCEPTION;

/**
 * Created by wolfi on 17.03.2016.
 */
public class OntologyDiagnosisSearcher {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(OntologyDiagnosisSearcher.class.getName());

    public static enum TestCaseType {POSITIVE_TC, NEGATIVE_TC, ENTAILED_TC, NON_ENTAILED_TC}

    public static enum ErrorStatus {NO_CONFLICT_EXCEPTION, SOLVER_EXCEPTION, INCONSISTENT_THEORY_EXCEPTION,
        NO_QUERY, ONLY_ONE_DIAG, NO_ERROR}

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
    }

    public void doUpdateTestcase(Set<OWLLogicalAxiom> testcase, Set<OWLLogicalAxiom> testcase1, TestCaseType type, ErrorHandler errorHandler) {
    }


    public void doCalculateDiagnosis(ErrorHandler errorHandler) {
        int n = diagnosisEngineFactory.getSearchConfiguration().numOfLeadingDiags;
        if (diagnosisEngineFactory.getSearchConfiguration().calcAllDiags)
            n = -1;
        //new SearchThread(diagnosisEngineFactory.getDiagnosisEngine(), n, errorHandler).execute();

        final IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = diagnosisEngineFactory.getDiagnosisEngine();
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
        } finally {
            diagnosisEngine.resetEngine();
        }

    }

    public void updateConfig(SearchConfiguration newConfiguration) {
        getDiagnosisEngineFactory().updateConfig(newConfiguration);
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
