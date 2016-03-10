package org.exquisite.diagnosis.quickxplain.mergexplain;

import java.util.ArrayList;
import java.util.List;

/**
 * Result class to return the two sets ConflictFreeStatements and Conflicts
 *
 * @author Thomas
 */
public class MergeXplainResult<T> {
    public List<T> ConflictFreeStatements = new ArrayList<T>();
    public List<List<T>> Conflicts = new ArrayList<List<T>>();
}
