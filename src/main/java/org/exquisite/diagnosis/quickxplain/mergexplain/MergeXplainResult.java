package org.exquisite.diagnosis.quickxplain.mergexplain;

import java.util.ArrayList;
import java.util.List;

import choco.kernel.model.constraints.Constraint;

/**
 * Result class to return the two sets ConflictFreeStatements and Conflicts
 * @author Thomas
 *
 */
public class MergeXplainResult {
	public List<Constraint> ConflictFreeStatements = new ArrayList<Constraint>();
	public List<List<Constraint>> Conflicts = new ArrayList<List<Constraint>>();
}
