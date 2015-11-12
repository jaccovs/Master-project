package org.exquisite.datamodel;

public interface IExquisiteValueBound {

	/**
	 * @return the lower
	 */
	public abstract double getLower();

	/**
	 * @param lower the lower to set
	 */
	public abstract void setLower(double lower);

	/**
	 * @return the upper
	 */
	public abstract double getUpper();

	/**
	 * @param upper the upper to set
	 */
	public abstract void setUpper(double upper);

	/**
	 * @return the step
	 */
	public abstract double getStep();

	/**
	 * @param step the step to set
	 */
	public abstract void setStep(double step);

}