package org.exquisite.protege.model.search;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.ui.panel.axioms.PossiblyFaultyAxiomsPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.model.search.importer.LogicalAxiomRenderingSearchMetadataImporter;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AxiomSubjectProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wolfi
 */
public class DebuggerSearchMetadataImporter implements SearchMetadataImporter {

    @Override
    public SearchMetadataDB getSearchMetadata(OWLEditorKit editorKit, Set<SearchCategory> categories) {
        DebuggerSearchMetadataImportContext context = new DebuggerSearchMetadataImportContext(editorKit);
        SearchMetadataDB db = new SearchMetadataDB();
        getAxiomBasedSearchMetadata(categories, context, db);
        return db;
    }

    private void getAxiomBasedSearchMetadata(Set<SearchCategory> categories, DebuggerSearchMetadataImportContext context, SearchMetadataDB db) {
        for (AxiomType<?> axiomType : AxiomType.AXIOM_TYPES) {
            getSearchMetadataForAxiomsOfType(axiomType, categories, context, db);
        }
    }

    private void getSearchMetadataForAxiomsOfType(AxiomType<?> axiomType, Set<SearchCategory> categories, DebuggerSearchMetadataImportContext context, SearchMetadataDB db) {
        for (AxiomBasedSearchMetadataImporter importer : getAxiomBasedSearchMetadataImporters(categories, axiomType)) {


            final OWLOntology activeOntology = context.getEditorKit().getModelManager().getActiveOntology();
            final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = context.getEditorKitHook().getActiveOntologyDebugger().getDiagnosisModel();
            final List possiblyFaultyLogicalAxioms = PossiblyFaultyAxiomsPanel.getPossiblyFaultyLogicalAxioms(activeOntology, diagnosisModel);
            for (OWLAxiom ax : activeOntology.getAxioms(axiomType)) {
                if (possiblyFaultyLogicalAxioms.contains(ax)) {
                    OWLObject subject = new AxiomSubjectProvider().getSubject(ax);
                    if (subject instanceof OWLEntity) {
                        OWLEntity entSubject = (OWLEntity) subject;
                        String rendering = context.getRendering(entSubject);
                        importer.generateSearchMetadataFor(ax, entSubject, rendering, context, db);
                    }
                }
            }

        }
    }

    private List<AxiomBasedSearchMetadataImporter> getAxiomBasedSearchMetadataImporters(Set<SearchCategory> categories, AxiomType<?> axiomType) {
        List<AxiomBasedSearchMetadataImporter> axiomBasedSearchMetadataImporters = new ArrayList<>();
        axiomBasedSearchMetadataImporters.add(new LogicalAxiomRenderingSearchMetadataImporter());

        List<AxiomBasedSearchMetadataImporter> result = new ArrayList<>();
        for (AxiomBasedSearchMetadataImporter importer : axiomBasedSearchMetadataImporters) {
            if (importer.isImporterFor(axiomType, categories)) {
                result.add(importer);
            }
        }
        return result;
    }

}
