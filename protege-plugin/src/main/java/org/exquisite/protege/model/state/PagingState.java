package org.exquisite.protege.model.state;

/**
 * Container that saves the information about a view's paging state for one OntologyDebugger instance.
 */
public class PagingState {

    public int currPageNum = 1;

    public int lastPageNum = Integer.MAX_VALUE;

    public void start() {
        currPageNum = 1;
    }

    public void prev() {
        if (--currPageNum <= 0)
            currPageNum = 1;
    }

    public void next() {
        if (++currPageNum > lastPageNum)
            currPageNum = lastPageNum;
    }

    public void end() {
        currPageNum = lastPageNum;
    }
}
