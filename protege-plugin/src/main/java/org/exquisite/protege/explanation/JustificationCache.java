package org.exquisite.protege.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: modified put method by @author wolfi, reformatting by @author wolfi
 */
public class JustificationCache {

    private Map<OWLAxiom, Set<Explanation<OWLAxiom>>> cache = new HashMap<>();

    public boolean contains(OWLAxiom entailment) {
        return cache.containsKey(entailment);
    }

    public Set<Explanation<OWLAxiom>> get(OWLAxiom entailment) {
        Set<Explanation<OWLAxiom>> explanations = cache.get(entailment);
        if (explanations == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(explanations);
    }

    public void put(Explanation<OWLAxiom> explanation) {
        Set<Explanation<OWLAxiom>> expls = cache.computeIfAbsent(explanation.getEntailment(), k -> new HashSet<>());
        expls.add(explanation);
    }

    public void put(Set<Explanation<OWLAxiom>> explanations) {
        for (Explanation<OWLAxiom> expl : explanations) {
            put(expl);
        }
    }

    public void clear() {
        cache.clear();
    }

    public void clear(OWLAxiom entailment) {
        cache.remove(entailment);
    }


}
