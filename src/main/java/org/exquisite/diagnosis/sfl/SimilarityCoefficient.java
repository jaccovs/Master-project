package org.exquisite.diagnosis.sfl;

import org.exquisite.diagnosis.interactivity.partitioning.scoring.Rounding;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;


/**
 * Abstract class for similarity coefficient computation.
 *
 * @author bhofer
 */
public abstract class SimilarityCoefficient {

    /*
    protected static double log(double term) {
        if (term != 0) {
            return Math.log(term);
        } else
            return 0.0;
    }

    protected static double log2(double term) {
        if (term != 0) {
            return Math.log(term) / Math.log(2);
        } else
            return 0.0;
    }
*/
    protected BigInteger a11, a10, a01, a00;
    protected BigInteger total;

    protected static BigDecimal div(BigDecimal term1, BigDecimal term2) {
        if (term2.compareTo(BigDecimal.ZERO) != 0)
            return term1.divide(term2, Rounding.PRECISION, Rounding.ROUNDING_MODE);
        else
            return BigDecimal.ZERO;
    }

    protected static BigDecimal div(BigDecimal term1, BigInteger term2) {
        BigDecimal t2 = new BigDecimal(term2);
        return div(term1, t2);
    }

    protected static BigDecimal div(BigInteger term1, BigInteger term2) {
        BigDecimal t1 = new BigDecimal(term1);
        BigDecimal t2 = new BigDecimal(term2);
        return div(t1, t2);
    }

    /**
     * Creates a new object of the state of the art coefficient ({@link Ochiai})
     * and returns it.
     *
     * @return Reference to a new Object of the state of the art coefficient (
     * {@link Ochiai})
     */
    public static SimilarityCoefficient getDefaultSimilarityCoefficient() {
        return new Ochiai();
    }

    protected abstract BigDecimal calculateCoefficient();

    /**
     * Returns the name of the created coeffient object, e.g. "Ochiai"
     *
     * @return Name of the used coefficient, e.g. "Ochiai"
     */
    public abstract String getCoefficientName();

    /**
     * Computes the similarity coefficient for the given hit spectra information
     *
     * @param a11 Number of failed and involved test cases
     * @param a10 Number of passed and involved test cases
     * @param a01 Number of failed and not involved test cases
     * @param a00 Number of passed and not involved test cases
     * @return Computed similarity coefficient
     */
    public BigDecimal getSimilarityCoefficient(BigInteger a11, BigInteger a10, BigInteger a01, BigInteger a00) {
        this.a11 = a11;
        this.a10 = a10;
        this.a01 = a01;
        this.a00 = a00;
        this.total = a11.add(a10).add(a01).add(a00);

        return calculateCoefficient();
    }

    protected BigDecimal PA() {
        return div(a11.add(a10), total);
    }

    protected BigDecimal PA_given_B() {
        return div(PAB(), PB());
    }

    protected BigDecimal PA_given_NB() {
        return div(PANB(), new BigDecimal("1.0").subtract(PB()));
    }

    protected BigDecimal PAB() {
        return div(a11, total);
    }

    protected BigDecimal PANB() {
        return div(a10, total);
    }

    protected BigDecimal PB() {
        return div(a01.add(a11), total);
    }

    protected BigDecimal PB_given_A() {
        return div(PAB(), PA());
    }

    protected BigDecimal PB_given_NA() {
        return div(PNAB(), new BigDecimal("1.0").subtract(PA()));
    }

    protected BigDecimal PNAB() {
        return div(a01, total);
    }

    protected BigDecimal PNANB() {
        return div(a00, total);
    }

    public static class CoefficientComparator implements
            Comparator<SimilarityCoefficient> {
        @Override
        public int compare(SimilarityCoefficient o1, SimilarityCoefficient o2) {
            return o1.getCoefficientName().compareTo(o2.getCoefficientName());
        }

    }
}
