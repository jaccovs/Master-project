package org.exquisite.core.costestimators;


import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.parser.OWLAxiomKeywordCounter;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 08.11.11
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class OWLAxiomKeywordCostsEstimator extends AbstractCostEstimator<OWLLogicalAxiom>
        implements ICostsEstimator<OWLLogicalAxiom> {

    public final static ManchesterOWLSyntax[] keywords = {ManchesterOWLSyntax.SOME,
            ManchesterOWLSyntax.ONLY,
            ManchesterOWLSyntax.MIN,
            ManchesterOWLSyntax.MAX,
            ManchesterOWLSyntax.EXACTLY,
            ManchesterOWLSyntax.AND,
            ManchesterOWLSyntax.OR,
            ManchesterOWLSyntax.NOT,
            ManchesterOWLSyntax.VALUE,
            ManchesterOWLSyntax.INVERSE,
            ManchesterOWLSyntax.SUBCLASS_OF,
            ManchesterOWLSyntax.EQUIVALENT_TO,
            ManchesterOWLSyntax.DISJOINT_CLASSES,
            ManchesterOWLSyntax.DISJOINT_WITH,
            ManchesterOWLSyntax.FUNCTIONAL,
            ManchesterOWLSyntax.INVERSE_OF,
            ManchesterOWLSyntax.SUB_PROPERTY_OF,
            ManchesterOWLSyntax.SAME_AS,
            ManchesterOWLSyntax.DIFFERENT_FROM,
            ManchesterOWLSyntax.RANGE,
            ManchesterOWLSyntax.DOMAIN,
            ManchesterOWLSyntax.TYPE,
            ManchesterOWLSyntax.TRANSITIVE,
            ManchesterOWLSyntax.SYMMETRIC
    };
    private Map<OWLLogicalAxiom, BigDecimal> axiomsProbabilities = null;
    private Map<ManchesterOWLSyntax, BigDecimal> keywordProbabilities;

    public OWLAxiomKeywordCostsEstimator(Set<OWLLogicalAxiom> t) {
        super(t);
        this.keywordProbabilities = createKeywordProbs();
        updateAxiomProbabilities();
    }

    public OWLAxiomKeywordCostsEstimator(DiagnosisModel<OWLLogicalAxiom> model) {
        this(new LinkedHashSet<>(model.getPossiblyFaultyFormulas()));
    }

    public static int getMaxLengthKeyword() {
        int max = 0;
        for (ManchesterOWLSyntax keyword : keywords)
            if (keyword.toString().length() > max)
                max = keyword.toString().length();

        return max;
    }
/*
    public void updateKeywordProb(Map<ManchesterOWLSyntax, BigDecimal> keywordProbabilities) {
        this.keywordProbabilities = keywordProbabilities;
        updateAxiomProbabilities();
    }
*/
    private Map<ManchesterOWLSyntax, BigDecimal> createKeywordProbs() {

        Map<ManchesterOWLSyntax, BigDecimal> map = new HashMap<>();

        for (ManchesterOWLSyntax keyword : keywords)
            map.put(keyword, new BigDecimal("0.01"));
        map.put(ManchesterOWLSyntax.SOME, new BigDecimal("0.05"));
        map.put(ManchesterOWLSyntax.ONLY, new BigDecimal("0.05"));
        map.put(ManchesterOWLSyntax.AND, new BigDecimal("0.001"));
        map.put(ManchesterOWLSyntax.OR, new BigDecimal("0.001"));
        map.put(ManchesterOWLSyntax.NOT, new BigDecimal("0.01"));
        return map;
    }

    @Override
    public BigDecimal getFormulaCosts(OWLLogicalAxiom axiom) {
        if (axiom == null)
            return new BigDecimal(0.5);

        BigDecimal p = axiomsProbabilities.get(axiom);
        if (p == null) {
            p = computeAxiomProbability(axiom);
            axiomsProbabilities.put(axiom, p);
        }
        return p;
    }

    /**
     * Computes the axiom score depending on the keyword probabilities:
     * The product of (1 - keyword_probability) ^ keyword_occurrence (for all occurring keywords)
     *
     * @param axiom The axiom.
     * @return The axiom score.
     */
    private BigDecimal getAxiomScore(OWLLogicalAxiom axiom) {
        BigDecimal result = BigDecimal.ONE;
        OWLAxiomKeywordCounter visitor = new OWLAxiomKeywordCounter();
        axiom.accept(visitor);
        for (ManchesterOWLSyntax keyword : this.keywordProbabilities.keySet()) {
            final int occurrence = visitor.get(keyword);
            if (occurrence > 0) {
                final BigDecimal probability = keywordProbabilities.get(keyword);
                BigDecimal temp = BigDecimal.ONE.subtract(probability);
                temp = temp.pow(occurrence, MathContext.DECIMAL128);
                result = result.multiply(temp);
            }
        }
        return result;
    }

    public void setKeywordProbabilities(Map<ManchesterOWLSyntax, BigDecimal> keywordProbabilities,
                                        Set<Diagnosis<OWLLogicalAxiom>> formulaSets) {
        this.keywordProbabilities = keywordProbabilities;
        updateAxiomProbabilities();
        updateDiagnosisProbabilities(formulaSets);
    }

    public Map<ManchesterOWLSyntax, BigDecimal> getKeywordProbabilities() {
        return keywordProbabilities;
    }

    /**
     * Updates the diagnosis measures measures according to the keywordProbabilities.
     *
     * @param diagnoses The diagnoses.
     */
    public void updateDiagnosisProbabilities(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {

        if (diagnoses == null)
            return;
        if (!diagnoses.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
                BigDecimal measure = getFormulasCosts(diagnosis.getFormulas());
                diagnosis.setMeasure(measure);
                sum = sum.add(measure);
            }
            // now normalize all diagnoses measures to a sum that equals ONE
            BigDecimal normalizedSum = BigDecimal.ZERO;
            int i = 0;
            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
                BigDecimal normalizedMeasure;
                if (i++ == diagnoses.size() - 1) { // this step guarantees that the sum always equals ONE (no rounding errors can happen with division)
                    normalizedMeasure = BigDecimal.ONE.subtract(normalizedSum);
                } else {
                    normalizedMeasure = diagnosis.getMeasure().divide(sum, MathContext.DECIMAL128); // this calculates the normalized measure
                }
                normalizedSum = normalizedSum.add(normalizedMeasure);
                diagnosis.setMeasure(normalizedMeasure);
            }
        }
    }

    private void updateAxiomProbabilities() {
        Map<OWLLogicalAxiom, BigDecimal> axiomsProbs = new HashMap<>();
        Collection<OWLLogicalAxiom> activeFormulas = getPossiblyFaultyFormulas();
        for (OWLLogicalAxiom axiom : activeFormulas) {

            BigDecimal result = computeAxiomProbability(axiom);
            axiomsProbs.put(axiom, result);
        }

        this.axiomsProbabilities = Collections.unmodifiableMap(axiomsProbs);
    }

    private BigDecimal computeAxiomProbability(OWLLogicalAxiom axiom) {
        BigDecimal result = getAxiomScore(axiom);
        result = BigDecimal.ONE.subtract(result);
        // no keyword is known
        if (result.compareTo(BigDecimal.ZERO) == 0)
            result = new BigDecimal("0.000000000000000000000000000000000000000000001");
        return result;
    }
/*
    private int getNumOccurrences(ManchesterOWLSyntax keyword, OWLLogicalAxiom axiom) {
        OWLAxiomKeywordCounter visitor = new OWLAxiomKeywordCounter();
        axiom.accept(visitor);
        int occurrence = visitor.get(keyword);
        return occurrence;
    }
*/
    /**
     * Extracts all logical axioms from an ontology and assigns a formula weight to them.
     *
     * @param ontology An ontology.
     * @return mapping from ontologies formulas to their weights.
     */
    public Map<OWLLogicalAxiom, Float> getFormulaWeights(OWLOntology ontology) {
        Map<OWLLogicalAxiom, Float> formulaWeights = new HashMap<>();
        for (OWLLogicalAxiom formula : ontology.getLogicalAxioms()) {
            BigDecimal formulaCosts = getFormulaCosts(formula);
            formulaWeights.put(formula, formulaCosts.floatValue());
        }
        return formulaWeights;
    }
}
