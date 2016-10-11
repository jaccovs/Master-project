package org.exquisite.protege.model.search;

import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Set;

/**
 * @author wolfi
 */
public class DebuggerLogicalAxiomRenderingSearchMetadataImporter extends AxiomBasedSearchMetadataImporter {

    @Override
    public boolean isImporterFor(AxiomType<?> axiomType, Set<SearchCategory> categories) {
        return axiomType.isLogical() && categories.contains(SearchCategory.LOGICAL_AXIOM);
    }

    @Override
    public void generateSearchMetadataFor(final OWLAxiom axiom, OWLEntity axiomSubject, String axiomSubjectRendering, final SearchMetadataImportContext context, SearchMetadataDB db) {
        StyledString rendering = context.getStyledStringRendering(axiom);
        String groupDescription = axiom.getAxiomType().getName();
        DebuggerSearchMetadata md = new DebuggerSearchMetadata(SearchCategory.LOGICAL_AXIOM, groupDescription, axiomSubject, axiomSubjectRendering, rendering.getString(), axiom) {
            @Override
            public StyledString getStyledSearchSearchString() {
                return context.getStyledStringRendering(axiom);
            }
        };
        db.addResult(md);
    }
}
