/**
 *
 */
package org.exquisite.datamodel;

import java.lang.reflect.Field;

/**
 * @author Arash
 */
public class ExquisiteValueBound implements IExquisiteValueBound {
    private double Lower;
    private double Upper;
    private double Step;

    /**
     *
     */
    public ExquisiteValueBound() {
        Lower = 0;
        Upper = 0;
        Step = 0;
    }

    /**
     * @param lower
     * @param upper
     * @param step
     */
    public ExquisiteValueBound(double lower, double upper, double step) {
        super();
        Lower = lower;
        Upper = upper;
        Step = step;
    }

    /* (non-Javadoc)
     * @see org.exquisite.datamodel.IExquisiteValueBound#getLower()
     */
    @Override
    public double getLower() {
        return Lower;
    }

    /* (non-Javadoc)
     * @see org.exquisite.datamodel.IExquisiteValueBound#setLower(double)
     */
    @Override
    public void setLower(double lower) {
        Lower = lower;
    }

    /* (non-Javadoc)
     * @see org.exquisite.datamodel.IExquisiteValueBound#getUpper()
     */
    @Override
    public double getUpper() {
        return Upper;
    }

    /* (non-Javadoc)
     * @see org.exquisite.datamodel.IExquisiteValueBound#setUpper(double)
     */
    @Override
    public void setUpper(double upper) {
        Upper = upper;
    }

    /* (non-Javadoc)
     * @see org.exquisite.datamodel.IExquisiteValueBound#getStep()
     */
    @Override
    public double getStep() {
        return Step;
    }

    /* (non-Javadoc)
     * @see org.exquisite.datamodel.IExquisiteValueBound#setStep(double)
     */
    @Override
    public void setStep(double step) {
        Step = step;
    }

    /**
     * for debugging...
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" {");
        //result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            //result.append(newLine);
        }
        result.append("}");
        result.append(newLine);
        return result.toString();
    }
}
