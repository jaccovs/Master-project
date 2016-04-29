package org.exquisite.protege.model.configuration;

import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public class SearchCreator {

    //private TreeSearch<FormulaSet<OWLLogicalAxiom>, OWLLogicalAxiom> search;

    private OWLOntology ontology;

    private SearchConfiguration config;

    private OWLReasonerManager reasonerMan;

    public SearchCreator(OWLOntology ontology, OWLReasonerManager reasonerMan) {
        this.ontology = ontology;
        this.reasonerMan = reasonerMan;
        readConfiguration();
    }

    public SearchConfiguration getConfig() {
        return config;
    }

    private void readConfiguration() {
        config = ConfigFileManager.readConfiguration();

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SearchCreator{");
        sb.append("config=").append(config);
        sb.append(", ontology=").append(ontology);
        sb.append(", reasonerMan=").append(reasonerMan);
        sb.append('}');
        return sb.toString();
    }
}
