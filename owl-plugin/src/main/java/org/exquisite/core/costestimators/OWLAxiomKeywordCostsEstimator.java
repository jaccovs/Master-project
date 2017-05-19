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
 * A cost estimator used to calculate the formula costs depending on the difficulty of occurring Manchester Syntax keywords.
 *
 * @author wolfi
 * @author pfleiss
 * @see OWLAxiomKeywordCounter
 */
public class OWLAxiomKeywordCostsEstimator extends AbstractCostEstimator<OWLLogicalAxiom>
        implements ICostsEstimator<OWLLogicalAxiom> {

    public final static ManchesterOWLSyntax[] keywords = {
            ManchesterOWLSyntax.TYPE,
            ManchesterOWLSyntax.SAME_AS,
            ManchesterOWLSyntax.SAME_INDIVIDUAL,
            ManchesterOWLSyntax.DIFFERENT_FROM,
            ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS,
            ManchesterOWLSyntax.SUBCLASS_OF,
            ManchesterOWLSyntax.DISJOINT_WITH,
            ManchesterOWLSyntax.DISJOINT_CLASSES,
            ManchesterOWLSyntax.DISJOINT_PROPERTIES,
            ManchesterOWLSyntax.DISJOINT_UNION_OF,
            ManchesterOWLSyntax.EQUIVALENT_TO,
            ManchesterOWLSyntax.EQUIVALENT_CLASSES,
            ManchesterOWLSyntax.EQUIVALENT_PROPERTIES,
            ManchesterOWLSyntax.AND,
            ManchesterOWLSyntax.OR,
            ManchesterOWLSyntax.NOT,
            ManchesterOWLSyntax.SOME,
            ManchesterOWLSyntax.ONLY,
            ManchesterOWLSyntax.MIN,
            ManchesterOWLSyntax.MAX,
            ManchesterOWLSyntax.SELF,
            ManchesterOWLSyntax.EXACTLY,
            ManchesterOWLSyntax.VALUE,
            ManchesterOWLSyntax.INVERSE,
            ManchesterOWLSyntax.INVERSE_OF,
            // ManchesterOWLSyntax.ONE_OF_DELIMETER, DEACTIVATED AS THERE IS NO KEYWORD FOR OWLObjectOneOf and OWLDataOneOf in Manchester Syntax
            ManchesterOWLSyntax.THAT,
            ManchesterOWLSyntax.HAS_KEY,
            ManchesterOWLSyntax.DOMAIN,
            ManchesterOWLSyntax.RANGE,
            ManchesterOWLSyntax.FUNCTIONAL,
            ManchesterOWLSyntax.INVERSE_FUNCTIONAL,
            ManchesterOWLSyntax.REFLEXIVE,
            ManchesterOWLSyntax.IRREFLEXIVE,
            ManchesterOWLSyntax.SYMMETRIC,
            ManchesterOWLSyntax.ASYMMETRIC,
            ManchesterOWLSyntax.TRANSITIVE,
            ManchesterOWLSyntax.SUB_PROPERTY_OF,
            // ManchesterOWLSyntax.SUB_PROPERTY_CHAIN, DEACTIVATED. IF ACTIVATED THE TEST HAS TO BE ADAPTED
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
        for (ManchesterOWLSyntax keyword : visitor) {
            final int occurrence = visitor.getOccurrences(keyword);
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
        this.axiomsProbabilities = new HashMap<>();
        Collection<OWLLogicalAxiom> activeFormulas = getPossiblyFaultyFormulas();
        for (OWLLogicalAxiom axiom : activeFormulas) {

            BigDecimal result = computeAxiomProbability(axiom);
            this.axiomsProbabilities.put(axiom, result);
        }
    }

    private BigDecimal computeAxiomProbability(OWLLogicalAxiom axiom) {
        BigDecimal result = getAxiomScore(axiom);
        result = BigDecimal.ONE.subtract(result);
        // no keyword is known
        if (result.compareTo(BigDecimal.ZERO) == 0)
            result = new BigDecimal("0.000000000000000000000000000000000000000000001");
        return result;
    }

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
