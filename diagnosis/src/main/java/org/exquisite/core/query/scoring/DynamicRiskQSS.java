package org.exquisite.core.query.scoring;

/**
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 */
public class DynamicRiskQSS<F> extends StaticRiskQSS<F> {

    double cMin;
    double cMax;


    public DynamicRiskQSS(double cMin, double c, double cMax) {
        super(c);
        this.cMin = cMin;
        this.cMax = cMax;
    }

    /* IMPORTANT: numOfLeadingDiags in preprocessC() MUST BE EQUAL TO nomOfLeadingDiags in getCAdjust()*/

    protected void preprocessC() {
        double maxPossibleCMax;
        if ((maxPossibleCMax = (double) this
                .getMaxPossibleNumOfDiagsToEliminate() / numOfLeadingDiags) < cMax) {
            cMax = maxPossibleCMax;
        }
        if (cMin < 0d) cMin = 0d;
        if (cMin > cMax) cMin = cMax = (cMin + cMax) / 2d;
        if (c < cMin) c = cMin;
        if (c > cMax) c = cMax;
    }

    protected double getCAdjust() {
        double interval = cMax - cMin;
        double epsilon = 0.01d;
        double adjust = ((Math
                .floor((numOfLeadingDiags / 2d) - epsilon) - numOfEliminatedLeadingDiags) / numOfLeadingDiags);
        return adjust * interval * 2d;
    }

    public void updateParameters(boolean answerToLastQuery) {

        //preprocessBeforeUpdate(answerToLastQuery);

        double cAdjust = getCAdjust();
        if (c + cAdjust > cMax) {
            c = cMax;
        } else if (c + cAdjust < cMin) {
            c = cMin;
        } else {
            c += cAdjust;
        }

    }
}
