package org.exquisite.datamodel.serialisation;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("ExquisiteDiagnosisResult")
public class ExquisiteDiagnosisResult implements Comparable {

    @XStreamAlias("CalculationTime")
    public String calculationTime;

    @XStreamAlias("Candidates")
    @XStreamConverter(value = ListStringXStreamConverter.class, strings = {"d4p1:string"})
    public List<String> candidates = new ArrayList<String>();

    @XStreamAlias("Rank")
    public String rank;

    public void sortCandidates() {
        Collections.sort(this.candidates);
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }
}