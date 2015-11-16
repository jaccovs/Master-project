package org.exquisite.diagnosis.interactivity.partitioning.scoring;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 15.02.12
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public class QSSFactory {

    public static QSS createMinScoreQSS() {
        return new MinScoreQSS();
    }

    public static QSS createSplitInHalfQSS() {
        return new SplitInHalfQSS();
    }

    public static QSS createPenaltyQSS(double maxPenalty) {
        return new PenaltyQSS(maxPenalty);
    }

    public static QSS createStaticRiskQSS(double c) {
        return new StaticRiskQSS(c);
    }

    public static QSS createDynamicRiskQSS(double cMin, double cMax, double c) {
        return new DynamicRiskQSS(cMin, c, cMax);
    }

}
