package org.exquisite.core.engines.tree;

import org.exquisite.core.costestimators.CostsEstimator;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * Created by kostya on 01.12.2015.
 */
public class TestNode {

    /**
     * Tests the ordering and comparator used in the HSTreeEngine
     */
    @Test
    public void testOrdering() {
        ArrayList<Node<Integer>> genNodes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Node<Integer> node = new Node<>(Collections.emptySet());
            genNodes.add(node);
        }
        Collections.shuffle(genNodes);

        TreeSet<Node<Integer>> nodes = new TreeSet<>(HSTreeEngine.getNodeComparator());
        nodes.addAll(genNodes);

        int i = 9;
        for (Node<Integer> node : nodes) {
            assertEquals(i, node.generationOrder);
            i--;
        }
    }

    @Test
    public void testOrderingWithCosts() {
        ArrayList<Node<Integer>> genNodes = new ArrayList<>();
        Node<Integer> root = new Node<>(Collections.emptySet());
        for (int i = 0; i < 10; i++) {
            Node<Integer> node = new Node<>(root, 10 - i, new CostsEstimator<Integer>() {
                @Override
                public BigDecimal getFormulasCosts(Collection<Integer> formulas) {
                    return new BigDecimal(formulas.stream().mapToInt(Integer::valueOf).sum());
                }

                @Override
                public BigDecimal getFormulaCosts(Integer formula) {
                    return new BigDecimal(formula);
                }
            });
            genNodes.add(node);
        }
        Collections.shuffle(genNodes);

        TreeSet<Node<Integer>> nodes = new TreeSet<>(HSTreeEngine.getNodeComparator());
        nodes.addAll(genNodes);

        int i = 10;
        for (Node<Integer> node : nodes) {
            assertEquals(i--, node.generationOrder);
        }
    }


}
