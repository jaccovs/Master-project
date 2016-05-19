package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.math.MathContext;

/**
* Created with IntelliJ IDEA.
* User: pfleiss
* Date: 05.09.12
* Time: 16:37
* To change this template use File | Settings | File Templates.
*/
public class AxiomListHeader {

    private Diagnosis<OWLLogicalAxiom> diagnosis;

    private String headerPref;

    private boolean isIncludeMeasure;

    public AxiomListHeader(Diagnosis<OWLLogicalAxiom> diagnosis, String headerPref, boolean isIncludeMeasure) {
        this.diagnosis = diagnosis;
        this.headerPref = headerPref;
        this.isIncludeMeasure = isIncludeMeasure;

    }

    public Diagnosis<OWLLogicalAxiom> getDiagnosis() {
        return diagnosis;
    }


    public String toString() {
        String roundedMeas = getDiagnosis().getMeasure().round(new MathContext(6)).toEngineeringString();
        String r = headerPref + "(Size: " + getDiagnosis().getFormulas().size();
        if (isIncludeMeasure)
            r += ", Measure: " + roundedMeas;
        return r + ")";

    }

}
