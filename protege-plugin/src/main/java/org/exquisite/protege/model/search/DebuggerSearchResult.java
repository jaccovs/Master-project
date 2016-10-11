package org.exquisite.protege.model.search;

import com.google.common.collect.ImmutableList;
import org.protege.editor.owl.model.search.SearchMetadata;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultMatch;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author wolfi
 */
public class DebuggerSearchResult extends SearchResult {

    private OWLAxiom axiom;

    public DebuggerSearchResult(SearchMetadata searchMetadata, ImmutableList<SearchResultMatch> matches) {
        super(searchMetadata, matches);
        this.axiom = ((DebuggerSearchMetadata)searchMetadata).getAxiom();
    }

    public OWLAxiom getAxiom() {
        return axiom;
    }
}
