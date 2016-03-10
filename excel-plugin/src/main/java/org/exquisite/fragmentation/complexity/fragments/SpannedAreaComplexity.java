package org.exquisite.fragmentation.complexity.fragments;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;
import org.exquisite.tools.StringUtilities;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class SpannedAreaComplexity extends AbstractFragmentComplexity {

    public SpannedAreaComplexity(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees) {
        super(xml, formulaTrees);
    }

    @Override
    public float getComplexity(Fragment fragment) {
        List<String> cells = new ArrayList<String>(fragment.getInterims());
        cells.addAll(fragment.getOutputs());

        return getSpannedArea(cells);
    }

    private float getSpannedArea(List<String> cells) {
        if (cells.size() == 0) {
            return 0;
        }

        String cell = cells.get(0);

        int colMin = StringUtilities.getCellColumn(cell);
        int colMax = colMin;
        int rowMin = StringUtilities.getCellRow(cell);
        int rowMax = rowMin;

        for (int i = 1; i < cells.size(); i++) {
            cell = cells.get(i);
            int col = StringUtilities.getCellColumn(cell);
            int row = StringUtilities.getCellRow(cell);

            if (col < colMin) {
                colMin = col;
            } else if (col > colMax) {
                colMax = col;
            }

            if (row < rowMin) {
                rowMin = row;
            } else if (row > rowMax) {
                rowMax = row;
            }
        }

        return (colMax + 1 - colMin) * (rowMax + 1 - rowMin);
    }

}
