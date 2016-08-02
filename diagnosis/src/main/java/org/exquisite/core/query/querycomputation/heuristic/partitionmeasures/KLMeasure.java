package org.exquisite.core.query.querycomputation.heuristic.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

/**
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author patrick
 * @author wolfi
 */
public class KLMeasure<F> implements IQPartitionRequirementsMeasure<F> {

    /**
     * A threshold value.
     */
    private BigDecimal tm;

    /**
     *
     * @param tm A threshold value.
     */
    public KLMeasure(BigDecimal tm) {
        this.tm = tm;
    }

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        Tuple tupleFromP = getMaxSetAndItsProb(p);
        Tuple tupleFromPBest = getMaxSetAndItsProb(pBest);
        if (tupleFromP.prob.compareTo(tupleFromPBest.prob) < 0) // prob of p < prob of pBest
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        final BigDecimal sigma = calculateSigma(pBest);
        Tuple tupleFromPBest = getMaxSetAndItsProb(pBest);
        final int numOfLeadingDiagnoses = pBest.dx.size() + pBest.dnx.size() + pBest.dz.size();
        final int dMax = tupleFromPBest.set.size();
        final BigDecimal pDMax = tupleFromPBest.prob;

        Double r1 = (double)dMax / (double)numOfLeadingDiagnoses * (Math.log(1d/pDMax.doubleValue()) / Math.log(2d));
        Double r2 = ((double)(numOfLeadingDiagnoses - dMax) / (double) numOfLeadingDiagnoses) * (Math.log(1d / (1d-pDMax.doubleValue())) / Math.log(2d));
        Double optKL = (double)(numOfLeadingDiagnoses - 1) / (double)numOfLeadingDiagnoses * (Math.log(1d/sigma.doubleValue()) / Math.log(2d))
                +
                (1d / (double) numOfLeadingDiagnoses) * (Math.log(1d / (1d-sigma.doubleValue())) / Math.log(2d));

        Double value = Math.abs(r1 + r2 - optKL);

        boolean result = new BigDecimal(Double.toString(value)).compareTo(tm) <= 0;
        return result;
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        return false;
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        final int numOfLeadingDiagnoses = p.dx.size() + p.dnx.size() + p.dz.size();
        BigDecimal result;
        if (p.dx.size() < ((double) numOfLeadingDiagnoses / 2d))
            result = new BigDecimal(Double.toString( ((double)p.dx.size()) / ((double)numOfLeadingDiagnoses * p.probDx.doubleValue()) ));
        else
            result = new BigDecimal(Double.toString( ((double)p.dnx.size()) / ((double)numOfLeadingDiagnoses * p.probDnx.doubleValue()) ));
        return result;
    }

    @Override
    public BigDecimal getScore(Query<F> query) {
        return BigDecimal.ZERO;
    } // TODO maybe think about a valid score?

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        throw new RuntimeException();
    }

    /**
     * Get the probability of the greater sized set in the qPartition. Equal sized sets are preferred that have the
     * smaller probability value.
     *
     * @param qPartition A qPartition.
     * @return The probability value of the greater sized set in the qPartition.
     * Equal sized sets are preferred that have the smaller probability value.
     */
    private Tuple getMaxSetAndItsProb(QPartition<F> qPartition) {
        if (qPartition.dx.size() > qPartition.dnx.size())
            return new Tuple(qPartition.probDx, qPartition.dx); //return qPartition.probDx;
        else if (qPartition.dx.size() < qPartition.dnx.size())
            return new Tuple(qPartition.probDnx, qPartition.dnx); //return qPartition.probDnx;
        else // dx.size() == dnx.size()
            if (qPartition.probDx.compareTo(qPartition.probDnx) <= 0)
                return new Tuple(qPartition.probDx, qPartition.dx); //return qPartition.probDx; // (probDx <= probDnx)
            else
                return new Tuple(qPartition.probDnx, qPartition.dnx); // return qPartition.probDnx; // (probDx > probDnx)
    }

    private BigDecimal calculateSigma(QPartition<F> pBest) {
        BigDecimal maxMeasure = BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;

        for (Diagnosis<F> d : pBest.dx) {
            sum = sum.add(d.getMeasure());
            if (d.getMeasure().compareTo(maxMeasure) > 0)
                maxMeasure = d.getMeasure();
        }

        for (Diagnosis<F> d : pBest.dnx) {
            sum = sum.add(d.getMeasure());
            if (d.getMeasure().compareTo(maxMeasure) > 0)
                maxMeasure = d.getMeasure();
        }

        final BigDecimal normalizedMaxMeasure = maxMeasure.divide(sum, MathContext.DECIMAL128);
        return normalizedMaxMeasure;
        //return BigDecimal.ONE.subtract(normalizedMaxMeasure);
    }

    class Tuple {
        BigDecimal prob;
        Set<Diagnosis<F>> set;

        public Tuple(BigDecimal prob, Set<Diagnosis<F>> set) {
            this.prob = prob;
            this.set = set;
        }
    }
}
