package org.exquisite.core.utils;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.stream.Collectors;

/**
 * @author wolfi
 */
public class OWLUtils {

    static public String getString(OWLLogicalAxiom axiom){
        String st = axiom.toString();
        st = st.replaceAll("<http+://[a-zA-Z_0-9\\./-]+#", "");
        return st.replaceAll(">","");
    }

    static public String getString(Diagnosis<OWLLogicalAxiom> diagnosis){
        return diagnosis.getFormulas().stream().map(OWLUtils::getString).collect(Collectors.joining(", "));
    }

    static public String getString(Query<OWLLogicalAxiom> query) {
        return query.formulas.stream().map(OWLUtils::getString).collect(Collectors.joining(", ")) + ", Score:" + query.score;
    }

    static public String getManchesterSyntaxString(OWLObject owlObject) {
        return new ManchesterOWLSyntaxOWLObjectRendererImpl().render(owlObject).replaceAll("(\r|\n|\r\n)+", "");
    }

}
