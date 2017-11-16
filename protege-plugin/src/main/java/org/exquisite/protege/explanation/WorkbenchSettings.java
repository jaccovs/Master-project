package org.exquisite.protege.explanation;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi, code changes by @author wolfi
 */
public class WorkbenchSettings {

    private JustificationType justificationType = JustificationType.REGULAR;
    
    private int limit = 2;

    private boolean findAll = true;

    JustificationType getJustificationType() {
        return justificationType;
    }

    void setJustificationType(JustificationType justificationType) {
        this.justificationType = justificationType;
    }

    int getLimit() {
        return limit;
    }

    void setLimit(int limit) {
        this.limit = limit;
    }

    boolean isFindAllExplanations() {
        return findAll;
    }

    void setFindAllExplanations(boolean findAllExplanations) {
        findAll = findAllExplanations;
    }
}
