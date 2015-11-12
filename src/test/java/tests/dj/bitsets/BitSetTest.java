package tests.dj.bitsets;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;


/**
 * Playing with bitests
 * @author dietmar
 *
 */
public class BitSetTest<T> {
	private final Object writeLock = new Object();
	private volatile List<T> collection = Collections.synchronizedList(new ArrayList<T>());

	/**
	 * Main driver
	 * @param args
	 */
	public static void main(String[] args) {
		new BitSetTest<BitSet>().run();
	}

	/**
	 * Start working
	 */
	private void run() {
		
		
		IntegerVariable a = Choco.makeIntVar("a", 1,3);
		IntegerVariable b = Choco.makeIntVar("b", 1,3);
		IntegerVariable c = Choco.makeIntVar("c", 1,3);
		
		Constraint c1 = Choco.eq(a,1);
		Constraint c2 = Choco.eq(a,1);
		Constraint c3 = Choco.eq(a,1);
		Constraint c4 = Choco.eq(a,1);
		Constraint c5 = Choco.eq(a,1);

		
		List<Constraint> allConstraints = new ArrayList<Constraint>();
		allConstraints.add(c1);
		allConstraints.add(c2);
		allConstraints.add(c3);
		allConstraints.add(c4);
		allConstraints.add(c5);
		
		List list1 = new ArrayList<Constraint>();
		List list2 = new ArrayList<Constraint>();
		
		list1.add(c1);  list1.add(c4); list1.add(c3);
		list2.add(c2); list2.add(c3);
		
		BitSet bs1 = QuickXPlain.makeBitSet(allConstraints, list1);
		BitSet bs2 = QuickXPlain.makeBitSet(allConstraints, list2);
				
		List<BitSet> knownBS = new ArrayList<BitSet>();
		knownBS.add(bs2);
		
		boolean result = QuickXPlain.checkSetIsSubsetOfKnown(knownBS, bs1);
		System.out.println("Result: " + result);
		
		result = QuickXPlain.checkIsSupersetOfKnown(knownBS, bs1);
		System.out.println("Result: " + result);

		if (true) {
			return;
		}
		
		BitSetTest<BitSet> bstest = new BitSetTest<BitSet>();
		
		bstest.addBitSetNoDups(bs1);
		bstest.addBitSetNoDups(bs2);
		
		
		
		List<String> allElements = new ArrayList<String>();
		allElements.add("A"); allElements.add("B"); allElements.add("C");
		
		List<String> someElements1 = new ArrayList<String>();
		someElements1.add("B"); someElements1.add("C"); someElements1.add("A");
		
		List<String> someElements2 = new ArrayList<String>();
		someElements2.add("D"); someElements2.add("C"); someElements2.add("B");

		BitSet some1 = QuickXPlain.makeBitSet(allElements, someElements1);
		BitSet some2 = QuickXPlain.makeBitSet(allElements, someElements2);
	
		System.out.println("Created the bitsets.");

		// How do we know that some1 is equal or a subset of some 2
		BitSet some1clone = (BitSet) some1.clone();
		System.out.println("Size: " + some1clone.cardinality());
		some1clone.and(some2);
		
		System.out.println("Size: " + some1clone.cardinality());
		if (some1clone.cardinality() == some1.cardinality()) {
			System.out.println("Subset");
		}
		else {
			System.out.println("No subset");
		}
	}
	
	
	
	/**
	 * This is to add bit sets - they need a special treatment
	 * @param bs
	 */
	public void addBitSetNoDups(T bs) {
		synchronized(writeLock){
			BitSet knownbs;
			BitSet tmpbs;
			BitSet newbs = (BitSet) bs;
			boolean added = false;
			boolean foundDup = false;
			for (Object o : this.collection) {
				knownbs = (BitSet) o;

				tmpbs = (BitSet) newbs.clone();
				tmpbs.and(knownbs);
				
				if (knownbs.cardinality() == tmpbs.cardinality()) {
					foundDup = true;
				}
			}
			if (!foundDup) {
				this.collection.add(bs);
				added = true;
			}
		}
	}
	
	

}
