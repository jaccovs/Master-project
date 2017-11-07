package org.exquisite.protege.explanation;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 */
public class WorkbenchSettings {

    private JustificationType justificationType = JustificationType.REGULAR;
    
    private int limit = 2;

    private boolean findAll = true;

    public JustificationType getJustificationType() {
        return justificationType;
    }

    public void setJustificationType(JustificationType justificationType) {
        this.justificationType = justificationType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isFindAllExplanations() {
        return findAll;
    }

    public void setFindAllExplanations(boolean findAllExplanations) {
        findAll = findAllExplanations;
    }
}
