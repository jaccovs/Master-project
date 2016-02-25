package org.exquisite.diagnosis.sfl;

import org.exquisite.core.partitioning.scoring.BigFunctions;
import org.exquisite.core.partitioning.scoring.Rounding;

import java.math.BigDecimal;

/**
 * Computes the similarity coefficient for the given hit spectra by using the
 * Ochiai coefficient.
 * <p>
 * <pre>
 * coefficient = a11/sqrt((a11+a10)*(a11+a01))
 * a11 = FailedInvolved
 * a10 = PassedInvolved
 * a01 = FailedNotInvolved
 * </pre>
 *
 * @author bhofer
 */
public class Ochiai extends SimilarityCoefficient {

    @Override
    protected BigDecimal calculateCoefficient() {
        // FIXME: This is an ugly Workaround for computing the square root.
//		return div(PAB(), new BigDecimal(Math.sqrt(PA().multiply(PB()).doubleValue())));
        return div(PAB(), BigFunctions.sqrt(PA().multiply(PB()), Rounding.PRECISION));
    }

    @Override
    public String getCoefficientName() {
        return "Ochiai";
    }

}
