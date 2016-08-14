package org.exquisite.protege.model;

import org.exquisite.core.query.Answer;
import org.exquisite.core.query.Query;
import org.exquisite.protege.ui.panel.QueryExplanationPanel;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Iterator;

/**
 * A component for explaining inferred axioms in queries.
 *
 */
public class QueryExplanation extends ExplanationService {

    /**
     * Do not access the editorKitHook direct, since it still might be not initialized.
     * Use getEditorKitHook() instead.
     */
    private EditorKitHook editorKitHook = null;

    @Override
    public void initialise() throws Exception {
        // The initialise method is called before initialisation of EditorKitHook, therefore we have to get the hook later
    }

    @Override
    public boolean hasExplanation(OWLAxiom axiom) {
        return isAxiomInferredFromDebugger(getDebugger(), axiom);
    }

    @Override
    public ExplanationResult explain(OWLAxiom axiom) {
        return new QueryExplanationPanel(axiom);
    }

    @Override
    public void dispose() throws Exception {}

    private OntologyDiagnosisSearcher getDebugger() {
        return getEditorKitHook().getActiveOntologyDiagnosisSearcher();
    }

    private EditorKitHook getEditorKitHook() {
        // Since the initialise method is called before initialisation of EditorKitHook we have to get the hook here
        if (editorKitHook == null)
            editorKitHook = (EditorKitHook) getOWLEditorKit().get("org.exquisite.protege.EditorKitHook");
        return editorKitHook;
    }

    /**
     * Check if the axiom has been inferred by the query computation of the debugger.
     * If this is the case, it can be explained.
     *
     * @param debugger The ontology debugger instance.
     * @param axiom The OWL Axiom to check.
     * @return <code>true</code> if axiom has been inferred by query computation, <code>false</code> otherwise.
     */
    public static boolean isAxiomInferredFromDebugger(final OntologyDiagnosisSearcher debugger, final OWLAxiom axiom) {
        if (!(axiom instanceof OWLLogicalAxiom)) // TODO clarify if this is a correct assumption
            return false;

        final OWLLogicalAxiom a = (OWLLogicalAxiom)axiom;

        // has the debugger created a query?
        Query<OWLLogicalAxiom> actualQuery = debugger.getActualQuery();
        if (actualQuery == null)
            return false;

        // the debugger has created queries
        // first search if axiom is part of actual query ...
        boolean isAxiomFromQueries = actualQuery.formulas.contains(a);
        // ... or part of the query history
        for (Iterator<Answer<OWLLogicalAxiom>> answers = debugger.getQueryHistory().iterator();!isAxiomFromQueries && answers.hasNext();) {
            final Answer<OWLLogicalAxiom> answer = answers.next();
            isAxiomFromQueries = answer.positive.contains(a) || answer.negative.contains(a);
        }
        // if axiom is from queries and axiom is NOT part of possibly faulty formulas then
        // it has been inferred by query computation
        return isAxiomFromQueries
                &&
                !debugger.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().getDiagnosisModel().getPossiblyFaultyFormulas().contains(a);
    }
}
