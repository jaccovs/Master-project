package tests.diagnosis;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.core.engines.tree.Node;
import org.exquisite.diagnosis.engines.common.ConstraintComparator;

import java.util.*;

/**
 * Reflects the unpruned DAG structure from the Greiner paper - used for testing revised pruning and node reuse methods.
 * @author David
 *
 */
public class MockNodeData {

    public Hashtable<String, Constraint> constraints = new Hashtable<String, Constraint>();
    public List<Node<Constraint>> graph = new ArrayList<>();
    public List<List<Constraint>> knownConflicts = new ArrayList<List<Constraint>>();
    public Hashtable<List<Constraint>, List<Node<Constraint>>> conflictNodeLookup = new Hashtable<>();
    public Node<Constraint> rootNode;

    /**
     * @return a graph the same as used in Greiner's paper.
	 */
	public static MockNodeData greinerExample(){
		MockNodeData instance = new MockNodeData();
		instance.makeGreinerExample();
		return instance;
	}
	
	/**
	 * @return a small graph with a pair of nodes whose paths contain identical labels.
	 */
	public static MockNodeData duplicatePathsExample(){
		MockNodeData instance = new MockNodeData();
		instance.makeDuplicatePathExample();
		return instance;
	}
	
	public static MockNodeData nodeReuseExample(){
		MockNodeData instance = new MockNodeData();
		instance.makeReuseExample();
		return instance;
	}
	
	private void makeReuseExample(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 1);
		Constraint b = Choco.eq(testVar, 2);
		Constraint c = Choco.eq(testVar, 3);
		Constraint d = Choco.eq(testVar, 4);
		
		constraints.put("a", a);
		constraints.put("b", b);
		constraints.put("c", c);
		constraints.put("d", d);
		
		Constraint[] rootArray = {a,b};
        Node<Constraint> root = makeRoot(rootArray);

        Constraint[] n1Conflicts = {b, c};
        Node<Constraint> n1 = makeNode("n1", n1Conflicts, root, a);

        Constraint[] n2Conflicts = {a, c};
        Node<Constraint> n2 = makeNode("n2", n2Conflicts, root, b);

        Constraint[] n3Conflicts = {};
        Node<Constraint> n3 = makeNode("n3", n3Conflicts, n1, b);

        Constraint[] n4Conflicts = {b, d};
        Node<Constraint> n4 = makeNode("n4", n4Conflicts, n1, c);
    }

    /**
	 * Emulates unpruned graph example from pg.85 of 
	 * http://cs.ru.nl/~peterl/teaching/KeR/Theorist/greibers-correctiontoreiter.pdf
	 */
	private void makeGreinerExample(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 1);
		Constraint b = Choco.eq(testVar, 2);
		Constraint c = Choco.eq(testVar, 3);
		Constraint d = Choco.eq(testVar, 4);
		
		constraints.put("a", a);
		constraints.put("b", b);
		constraints.put("c", c);
		constraints.put("d", d);
						
		Constraint[] rootArray = {a,b};
        Node<Constraint> root = makeRoot(rootArray);

        Constraint[] n1Conflicts = {b, c};
        Node<Constraint> n1 = makeNode("n1", n1Conflicts, root, a);

        Constraint[] n2Conflicts = {a, c};
        Node<Constraint> n2 = makeNode("n2", n2Conflicts, root, b);

        Constraint[] n3Conflicts = {};
        Node<Constraint> n3 = makeNode("n3", n3Conflicts, n1, b);
        n2.addChild(n3, a);

        Constraint[] n4Conflicts = {b, d};
        Node<Constraint> n4 = makeNode("n4", n4Conflicts, n1, c);

        Constraint[] n5Conflicts = {};
        Node<Constraint> n5 = makeNode("n5", n5Conflicts, n2, c);

        Constraint[] n6Conflicts = {};
        Node<Constraint> n6 = makeNode("n6", n6Conflicts, n4, b);

        Constraint[] n7Conflicts = {b};
        Node<Constraint> n7 = makeNode("n7", n7Conflicts, n4, d);
    }

    /**
	 * For testing parallel tree pre-check for duplicate node paths.
	 */
	private void makeDuplicatePathExample(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 1);
		Constraint b = Choco.eq(testVar, 2);
		Constraint c = Choco.eq(testVar, 3);
		Constraint d = Choco.eq(testVar, 4);
		Constraint e = Choco.eq(testVar, 4);
		Constraint f = Choco.eq(testVar, 4);
		
		constraints.put("a", a);
		constraints.put("b", b);
		constraints.put("c", c);
		constraints.put("d", d);
		constraints.put("e", e);
		constraints.put("f", f);
						
		//level 0
		Constraint[] rootArray = {a,b,c};
        Node<Constraint> root = makeRoot(rootArray);

        //level 1
        Constraint[] n1Conflicts = {b, e};
        Node<Constraint> n1 = makeNode("n1", n1Conflicts, root, a);

        Constraint[] n2Conflicts = {a, f};
        Node<Constraint> n2 = makeNode("n2", n2Conflicts, root, b);

        Constraint[] n3Conflicts = {d, e};
        Node<Constraint> n3 = makeNode("n3", n3Conflicts, root, c);
    }

    /**
	 * Makes a Node and configures that node as a root node.
	 * @param conflict the nodeLabel set for this root node
	 * @return Node set as root.
	 */
    private Node<Constraint> makeRoot(Constraint[] conflict) {
        Node<Constraint> root = new Node<Constraint>(new ArrayList<Constraint>(Arrays.asList(conflict)));
        root.nodeName = "root";
        graph.add(root);
		knownConflicts.add(root.nodeLabel);
        List<Node<Constraint>> nodes = new ArrayList<Node<Constraint>>();
        nodes.add(root);
        conflictNodeLookup.put(root.nodeLabel, nodes);
		rootNode = root;
		return root;
	}
	
	/**
	 * Makes a (non-root) Node
	 * @param name    A name for the node (can be useful for printing, debugging).
	 * @param conflict    The nodeLabel set for this particular node.
	 * @param parent    The parent of this node.
	 * @param edge    The path label from the parent that points to this node.
	 * @return A Node that is the child of another node.
	 */
    private Node<Constraint> makeNode(String name, Constraint[] conflict, Node<Constraint> parent,
									  Constraint edge) {
        Node<Constraint> node = new Node<Constraint>(parent, edge);
        node.nodeLabel = new ArrayList<Constraint>(Arrays.asList(conflict));
        //System.out.println("Making node " + name + " with nodeLabel size of: " + node.nodeLabel.size());
		node.nodeName = name;
		Set<Constraint> set = new TreeSet<Constraint>(new ConstraintComparator());
		set.addAll(parent.pathLabels);
		node.pathLabels.addAll(set);
		node.pathLabels.add(edge);
		graph.add(node);
		if (!node.nodeLabel.isEmpty()){
			knownConflicts.add(node.nodeLabel);

            List<Node<Constraint>> nodes = conflictNodeLookup.get(node.nodeLabel);
            if (nodes == null) {
                nodes = new ArrayList<Node<Constraint>>();
            }
            nodes.add(node);
			conflictNodeLookup.put(node.nodeLabel, nodes);
		}
		return node;
	}
}
