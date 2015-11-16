package org.exquisite.datamodel;

public interface IExquisiteValueBound {

    /**
     * @return the lower
     */
    double getLower();

    /**
     * @param lower the lower to set
     */
    void setLower(double lower);

    /**
     * @return the upper
     */
    double getUpper();

    /**
     * @param upper the upper to set
     */
    void setUpper(double upper);

    /**
     * @return the step
     */
    double getStep();

    /**
     * @param step the step to set
     */
    void setStep(double step);

}