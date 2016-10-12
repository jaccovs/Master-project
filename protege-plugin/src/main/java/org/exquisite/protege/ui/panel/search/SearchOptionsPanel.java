package org.exquisite.protege.ui.panel.search;

import org.exquisite.protege.model.search.DebuggerSearchPreferences;
import org.protege.editor.owl.ui.search.SearchOptionsChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * A panel containing search options.
 *
 */
class SearchOptionsPanel extends JPanel {

    private final JCheckBox caseSensitive;

    private final JCheckBox wholeWordsCheckbox;

    private final JCheckBox ignoreWhiteSpaceCheckbox;

    private final JCheckBox useRegexCheckBox;

    private final java.util.List<SearchOptionsChangedListener> listeners = new ArrayList<>();


    SearchOptionsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JPanel searchOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        add(searchOptionsPanel, BorderLayout.NORTH);

        caseSensitive = new JCheckBox(new AbstractAction("Case sensitive") {
            public void actionPerformed(ActionEvent e) {
                DebuggerSearchPreferences.getInstance().setCaseSensitive(caseSensitive.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(caseSensitive);

        wholeWordsCheckbox = new JCheckBox(new AbstractAction("Whole words") {
            public void actionPerformed(ActionEvent e) {
                DebuggerSearchPreferences.getInstance().setWholeWords(wholeWordsCheckbox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(wholeWordsCheckbox);

        ignoreWhiteSpaceCheckbox = new JCheckBox(new AbstractAction("Ignore white space") {
            public void actionPerformed(ActionEvent e) {
                DebuggerSearchPreferences.getInstance().setIgnoreWhiteSpace(ignoreWhiteSpaceCheckbox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(ignoreWhiteSpaceCheckbox);


        useRegexCheckBox = new JCheckBox(new AbstractAction("Regular expression") {
            public void actionPerformed(ActionEvent e) {
                DebuggerSearchPreferences.getInstance().setUseRegularExpressions(useRegexCheckBox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(useRegexCheckBox);

    }

    void addListener(SearchOptionsChangedListener listener) {
        listeners.add(listener);
    }

    void refresh() {
        DebuggerSearchPreferences prefs = DebuggerSearchPreferences.getInstance();
        wholeWordsCheckbox.setSelected(prefs.isWholeWords());
        caseSensitive.setSelected(prefs.isCaseSensitive());
        useRegexCheckBox.setSelected(prefs.isUseRegularExpressions());
        ignoreWhiteSpaceCheckbox.setSelected(prefs.isIgnoreWhiteSpace());
    }

    private void fireSearchRequestOptionChanged() {
        new ArrayList<>(listeners).forEach(SearchOptionsChangedListener::searchRequestOptionChanged);
    }
}
