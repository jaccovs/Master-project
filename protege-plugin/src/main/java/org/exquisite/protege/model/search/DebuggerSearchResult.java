package org.exquisite.protege.model.search;

import com.google.common.collect.ImmutableList;
import org.protege.editor.owl.model.search.SearchMetadata;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultMatch;
import org.semanticweb.owlapi.model.OWLAxiom;

public class DebuggerSearchResult extends SearchResult {

    private OWLAxiom axiom;

    DebuggerSearchResult(SearchMetadata searchMetadata, ImmutableList<SearchResultMatch> matches) {
        super(searchMetadata, matches);
        this.axiom = ((DebuggerSearchMetadata)searchMetadata).getAxiom();
    }

    public OWLAxiom getAxiom() {
        return axiom;
    }
}
