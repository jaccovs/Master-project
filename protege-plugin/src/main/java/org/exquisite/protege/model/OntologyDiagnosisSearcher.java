package org.exquisite.protege.model;

import org.exquisite.protege.model.configuration.SearchCreator;
import org.exquisite.protege.model.error.ErrorHandler;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;

/**
 * Created by wolfi on 17.03.2016.
 */
public class OntologyDiagnosisSearcher {

    public static enum TestCaseType {POSITIVE_TC, NEGATIVE_TC, ENTAILED_TC, NON_ENTAILED_TC}

    public static enum ErrorStatus {NO_CONFLICT_EXCEPTION, SOLVER_EXCEPTION, INCONSISTENT_THEORY_EXCEPTION,
        NO_QUERY, ONLY_ONE_DIAG, NO_ERROR}

    public static enum QuerySearchStatus { IDLE, SEARCH_DIAG, GENERATING_QUERY, MINIMZE_QUERY, ASKING_QUERY }

    public static enum SearchStatus { IDLE, RUNNING }

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;

    private SearchCreator creator;


    public OntologyDiagnosisSearcher(OWLEditorKit editorKit) {
        OWLReasonerManager reasonerMan = editorKit.getModelManager().getOWLReasonerManager();
        OWLOntology ontology = editorKit.getModelManager().getActiveOntology();

        creator = new SearchCreator(ontology,reasonerMan);
    }

    public SearchCreator getSearchCreator() {
        return creator;
    }

    public void removeBackgroundAxioms(List selectedValues) {
    }

    public void addBackgroundAxioms(List selectedValues) {

    }

    public void addChangeListener(EditorKitHook editorKitHook) {
    }

    public void removeChangeListener(EditorKitHook editorKitHook) {
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

        int n = creator.getConfig().numOfLeadingDiags;
        if (creator.getConfig().calcAllDiags)
            n = -1;

        System.out.println("Start with calculation of maximal " + n + " diagnoses ... ");
        //new SearchThread(creator.getSearch(), n, errorHandler).execute(); //TODO
    }

}
