package org.exquisite.diagnosis.engines.common;

import org.exquisite.core.engines.tree.Node;

/**
 * Is used as an additional output in NodeUtilites.checkAndAddNode().
 * A NodeContainer is passed to the function and the contained Node is created or set.
 *
 * @author Thomas
 */

public class NodeContainer<T> {
    /**
     * The node to be created or set
     */
    public Node<T> node;
}
