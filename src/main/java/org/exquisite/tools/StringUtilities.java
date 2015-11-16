package org.exquisite.tools;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.data.ConstraintsFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of String manipulation methods.
 *
 * @author David
 */
public class StringUtilities {
    private static Pattern colRowPattern = Pattern.compile("(?:WS_[0-9]+_)*([A-Z]+)([0-9]+)");

    /**
     * Alphabetically increments a string
     *
     * @param s - the string to increment
     * @return e.g. A -> B, or AA -> AB *always returns a string in upper case*
     */
    public static String next(String s) {
        return nextString(s).toUpperCase();
    }

    /**
     * Alphabetically increments a string
     *
     * @param s - the string to increment
     * @return e.g. A -> B, or AA -> AB etc.
     */
    private static String nextString(String s) {
        s = s.toLowerCase();
        int length = s.length();
        char c = s.charAt(length - 1);

        if (c == 'z')
            return length > 1 ? next(s.substring(0, length - 1)) + 'a' : "aa";

        return s.substring(0, length - 1) + ++c;
    }

    /**
     * Print a tree
     *
     * @param t      - the tree to print
     * @param indent - indent, initial 0
     */
    public static void printTree(CommonTree t, int indent) {
        if (t != null) {
            StringBuffer sb = new StringBuffer(indent);

            if (t.getParent() == null) {
                System.out.println(sb.toString() + t.getText().toString());
            }
            for (int i = 0; i < indent; i++)
                sb = sb.append("  |_ ");
            for (int i = 0; i < t.getChildCount(); i++) {
                System.out.println(sb.toString() + t.getChild(i).toString());
                printTree((CommonTree) t.getChild(i), indent + 1);
            }
        }
    }

    public static List<String> rangeToCells(String range) {
        String[] split = range.split(":");
        String start = split[0];
        String end = split[1];
        List<String> cells = new ArrayList<String>();

        Pattern pattern = Pattern.compile("[A-Z]+|[0-9]+");

        int startRow = Integer.parseInt(StringUtilities.parse(start, pattern).get(1));
        String startCol = StringUtilities.parse(start, pattern).get(0);

        int endRow = Integer.parseInt(StringUtilities.parse(end, pattern).get(1));
        String endCol = StringUtilities.parse(end, pattern).get(0);

        boolean finish = false;
        while (!finish) {
            for (int i = startRow; i <= endRow; i++) {
                start = startCol + i;
                cells.add(start);
                if (start.equals(end)) finish = true;
            }
            startCol = next(startCol);
        }
        return cells;
    }

    /**
     * Returns the row of a cell name starting with 1.
     *
     * @param cell
     * @return
     */
    public static int getCellRow(String cell) {
        List<String> c = StringUtilities.parseGroups(cell, colRowPattern);
        if (c.size() != 2) {
            return 0;
        }
        return Integer.parseInt(c.get(1));
    }

    /**
     * Returns the column of a cell name starting with 1.
     *
     * @param cell
     * @return
     */
    public static int getCellColumn(String cell) {
        List<String> c = StringUtilities.parseGroups(cell, colRowPattern);
        if (c.size() != 2) {
            return 0;
        }

        // Convert column identifier to int
        String col = c.get(0);
        int colNr = 0;
        for (int i = 1; i <= col.length(); i++) {
            char ch = col.charAt(col.length() - i);
            colNr += (ch - 64) * Math.pow(26, i - 1);
        }

        return colNr;
    }

    public static String getCellName(int column, int row) {
        return ConstraintsFactory.WORKSHEET_PREFIX + getColumnName(column) + row;
    }

    public static String getColumnName(int column) {
        String ret = "";
        while (column > 0) {
            int current = column % 26;
            column = column / 26;
            ret += (char) (current + 64);
        }
        return ret;
    }

    /**
     * Breaks a string up into a list of chunks based on regex pattern.
     *
     * @param toParse
     * @param pattern
     * @return
     */
    public static List<String> parse(String toParse, Pattern pattern) {
        List<String> chunks = new ArrayList<String>();
        Matcher matcher = pattern.matcher(toParse);
        while (matcher.find()) {
            chunks.add(matcher.group());
        }
        return chunks;
    }

    public static List<String> parseGroups(String toParse, Pattern pattern) {
        List<String> chunks = new ArrayList<String>();
        Matcher matcher = pattern.matcher(toParse);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                chunks.add(matcher.group(i));
            }
        }
        return chunks;
    }


}
