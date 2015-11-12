package org.exquisite.diagnosis.interactivity.partitioning.costestimators;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.BigFunctions;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Rounding;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.ontologies.AxiomConstraint;
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import choco.kernel.model.constraints.Constraint;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 08.11.11
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class OWLAxiomKeywordCostsEstimator extends AbstractCostEstimator
        implements CostsEstimator {

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


    public static int getMaxLengthKeyword() {
        int max = 0;
        for (ManchesterOWLSyntax keyword : keywords)
            if (keyword.toString().length() > max)
                max = keyword.toString().length();

        return max;
    }

    public OWLAxiomKeywordCostsEstimator(Set<Constraint> t) {
        super(t);
        this.keywordProbabilities = createKeywordProbs();
        updateAxiomProbabilities();
    }

    public OWLAxiomKeywordCostsEstimator(DiagnosisModel model) {
        this(new LinkedHashSet<Constraint>(model.getPossiblyFaultyStatements()));
    }

    public void updateKeywordProb(Map<ManchesterOWLSyntax, BigDecimal> keywordProbabilities) {
        this.keywordProbabilities = keywordProbabilities;
        updateAxiomProbabilities();
    }

    private Map<ManchesterOWLSyntax, BigDecimal> createKeywordProbs() {

        Map<ManchesterOWLSyntax, BigDecimal> map = new HashMap<ManchesterOWLSyntax, BigDecimal>();

        for (ManchesterOWLSyntax keyword : keywords)
            map.put(keyword, new BigDecimal("0.01"));
        map.put(ManchesterOWLSyntax.SOME, new BigDecimal("0.05"));
        map.put(ManchesterOWLSyntax.ONLY, new BigDecimal("0.05"));
        map.put(ManchesterOWLSyntax.AND, new BigDecimal("0.001"));
        map.put(ManchesterOWLSyntax.OR, new BigDecimal("0.001"));
        map.put(ManchesterOWLSyntax.NOT, new BigDecimal("0.01"));
        return map;
    }

    /*
    public BigDecimal getFormulaSetCosts(Set<OWLLogicalAxiom> formulas) {
        BigDecimal probability = BigDecimal.ONE;
        if (formulas != null)
            for (OWLLogicalAxiom axiom : formulas) {
                probability = probability.multiply(getFormulaCosts(axiom));
            }
        Collection<OWLLogicalAxiom> activeFormulas = new ArrayList<OWLLogicalAxiom>(searchable.getKnowledgeBase().getFaultyFormulas());
        activeFormulas.removeAll(formulas);
        for (OWLLogicalAxiom axiom : activeFormulas) {
            probability = probability.multiply(BigDecimal.ONE.subtract(getFormulaCosts(axiom)));
        }
        return probability;
    }
    */

    public BigDecimal getFormulaCosts(Constraint constraint) {

        //NEU bei null wird 1 zurückgegeben
        if(constraint==null)
            return new BigDecimal(0.5);

        BigDecimal p = axiomsProbabilities.get(constraint);
        if (p != null)
            return p;

        OWLAxiom axiom = ((AxiomConstraint)constraint).getAxiom();
        
        ManchesterOWLSyntaxOWLObjectRendererImpl impl = new ManchesterOWLSyntaxOWLObjectRendererImpl();
        String renderedAxiom = impl.render(axiom); // String renderedAxiom = modelManager.getRendering(axiom);
        BigDecimal result = BigDecimal.ONE;

        for (ManchesterOWLSyntax keyword : this.keywordProbabilities.keySet()) {
            int occurrence = getNumOccurrences(keyword, renderedAxiom);
            BigDecimal probability = getProbability(keyword);

            BigDecimal temp = BigDecimal.ONE.subtract(probability);
            temp = BigFunctions.intPower(temp, occurrence, temp.scale());
            result = result.multiply(temp);
        }
        result = BigDecimal.ONE.subtract(result);
        // no keyword is known
        if (result.compareTo(new BigDecimal("0.0")) == 0)
            result = new BigDecimal("0.000000000000000000000000000000000000000000001");

        return result;
    }

    private Map<Constraint, BigDecimal> axiomsProbabilities = null;
    private Map<ManchesterOWLSyntax, BigDecimal> keywordProbabilities;

    public void setKeywordProbabilities(Map<ManchesterOWLSyntax, BigDecimal> keywordProbabilities,
                                        Set<Diagnosis> formulaSets) {


        this.keywordProbabilities = keywordProbabilities;
        updateAxiomProbabilities();
        updateDiagnosisProbabilities(formulaSets);

    }

    public Map<ManchesterOWLSyntax, BigDecimal> getKeywordProbabilities() {
        return keywordProbabilities;
    }

    private void updateDiagnosisProbabilities(Set<Diagnosis> formulaSets) {

        if (formulaSets == null)
            return;
        if (!formulaSets.isEmpty()) {
            for (Diagnosis formulaSet : formulaSets) {
                BigDecimal probability = getFormulaSetCosts(formulaSet.getElements());

                formulaSet.setMeasure(probability);
                //axiomSet.setUserAssignedProbability(probability);
            }
            BigDecimal sum = BigDecimal.ZERO;

            for (Diagnosis formulaSet : formulaSets) {
                sum = sum.add(formulaSet.getMeasure());
            }
            for (Diagnosis formulaSet : formulaSets) {
                formulaSet.setMeasure(formulaSet.getMeasure().divide(sum, Rounding.PRECISION, Rounding.ROUNDING_MODE));
            }
        }
    }

    private void updateAxiomProbabilities() {
        Map<Constraint, BigDecimal> axiomsProbs = new HashMap<Constraint, BigDecimal>();
        ManchesterOWLSyntaxOWLObjectRendererImpl impl = new ManchesterOWLSyntaxOWLObjectRendererImpl();
        Collection<Constraint> activeFormulas = getFaultyFormulas();
        BigDecimal sum = BigDecimal.ZERO;
        for (Constraint constraint : activeFormulas) {
        	OWLAxiom axiom = ((AxiomConstraint)constraint).getAxiom();
            String renderedAxiom = impl.render(axiom); // String renderedAxiom = modelManager.getRendering(axiom);
            BigDecimal result = BigDecimal.ONE;

            for (ManchesterOWLSyntax keyword : this.keywordProbabilities.keySet()) {
                int occurrence = getNumOccurrences(keyword, renderedAxiom);
                BigDecimal probability = getProbability(keyword);

                BigDecimal t = BigDecimal.ONE.subtract(probability);
                t = BigFunctions.intPower(t, occurrence, t.scale());
                result = result.multiply(t);
            }
            result = BigDecimal.ONE.subtract(result);
            // no keyword is known
            if (result.compareTo(new BigDecimal("0.0")) == 0)
                result = new BigDecimal("0.000000000000000000000000000000000000000000001");
            axiomsProbs.put(constraint, result);
            sum = sum.add(BigDecimal.ONE.subtract(result));
        }
        /*if (normalize_axioms) {
            for (Id axiom : axiomsProbs.keySet())
                axiomsProbs.put(axiom, axiomsProbs.get(axiom) / sum);
        }*/


        this.axiomsProbabilities = Collections.unmodifiableMap(axiomsProbs);
    }

    private BigDecimal getProbability(ManchesterOWLSyntax keyword) {
        return keywordProbabilities.get(keyword);
    }

    private int getNumOccurrences(ManchesterOWLSyntax keyword, String str) {
        int cnt = 0;
        int last = 0;

        if (keyword == null) {
            System.out.println();
        }
        last = str.indexOf(keyword.toString());
        while (last > -1) {
            cnt++;
            last = str.indexOf(keyword.toString(), last + 1);
        }

        return cnt;

    }
}
