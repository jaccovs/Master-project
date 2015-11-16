package org.exquisite.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A class to compare two lists
 *
 * @author dietmar
 */
public class ListComparator<T> implements Comparator<List<T>> {
    @Override
    public int compare(List<T> list1, List<T> list2) {
        // Make sure that these are lists

        if (list1 != null && list2 != null && list1.size() != list2.size()) {
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
