package org.exquisite.diagnosis.engines.common;

import org.exquisite.diagnosis.models.DAGNode;

/**
 * Is used as an additional output in NodeUtilites.checkAndAddNode().
 * A NodeContainer is passed to the function and the contained DAGNode is created or set.
 * @author Thomas
 *
 */

public class NodeContainer {
	/**
	 * The node to be created or set
	 */
	public DAGNode node;
}
