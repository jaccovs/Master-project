package utils;

import org.exquisite.core.model.Diagnosis;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

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
}
