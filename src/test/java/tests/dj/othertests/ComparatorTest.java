package tests.dj.othertests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.exquisite.diagnosis.engines.common.SharedCollection;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Some small test with comparators
 * @author dietmar
 * @param <T>
 *
 */
public class ComparatorTest<T> {

	
	static List<List<Constraint>> allConflicts = new ArrayList<List<Constraint>>();
	
	/**
	 * Test comparators
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			new ComparatorTest<List<Constraint>>().run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finished");
		
	}
	
	/**
	 * Doing the real work
	 * @throws Exception
	 */
	public void run ()  throws Exception {
		IntegerVariable v1 = Choco.makeIntVar("v1", 1,3);
		Constraint c1 = Choco.gt(v1, 1);
		Constraint c2 = Choco.gt(v1, 2);
		Constraint c3 = Choco.gt(v1, 3);
		
		List<Constraint> clist1 = new ArrayList<Constraint>();
		List<Constraint> clist2 = new ArrayList<Constraint>();
		
		clist1.add(c1);
		clist1.add(c2);
		clist1.add(c3);
		
		clist2.add(c1);
		clist2.add(c3);

		SharedCollection<List<Constraint>> knownConflicts = new SharedCollection<List<Constraint>>();
		
		List<Constraint> known1 = knownConflicts.addItemListNoDups(clist1);
		List<Constraint> known2 = knownConflicts.addItemListNoDups(clist1);
		List<Constraint> known3 = knownConflicts.addItemListNoDups(clist2);
		
		System.out.println(known1);
		System.out.println(known2);
		System.out.println(known3);
		
		System.out.println("Size: " + knownConflicts.getCollection().size());
		
		if (true) {
			return;
		}
		
		
		ListComparator lcomp = new ListComparator();
		
		int result = lcomp.compare((List<T>) clist1, (List<T>) clist1);
		
		System.out.println("Result: " + result);
		
		allConflicts.add(clist1);
		
		addList(allConflicts, clist2);
		System.out.println("List size: " + allConflicts.size());
		
		
	}
	
	/**
	 * Add without duplicates
	 * @param list
	 * @return
	 */
	static boolean addList(List<List<Constraint>> globalList, List<Constraint> list) {

		if (!globalList.contains(list)) {
			ComparatorTest.allConflicts.add(list);
			return true;
		}
		else {
			return false;
		}
	}
	
	
	class ListComparator implements Comparator<List<T>> {

		@Override
		public int compare(List<T> list1, List<T> list2) {
			// Make sure that these are lists

			if (list1!=null && list2!=null && list1.size() != list2.size()) {
				return -1;
			}
			List<T> difference = new ArrayList<T>(list1);
			difference.removeAll(list2);
			if (difference.size() == 0) {
				return 0;
			}
			
			return -1;
		}
		
	}

}
