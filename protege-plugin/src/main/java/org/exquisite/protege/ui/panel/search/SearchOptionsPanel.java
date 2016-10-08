package org.exquisite.protege.ui.panel.search;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.ui.search.SearchOptionsChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * A panel containing search options.
 *
 */
public class SearchOptionsPanel extends JPanel {

    private final JCheckBox caseSensitive;

    private final JCheckBox wholeWordsCheckbox;

    private final JCheckBox ignoreWhiteSpaceCheckbox;

    private final JCheckBox useRegexCheckBox;

    private final java.util.List<SearchOptionsChangedListener> listeners = new ArrayList<>();

    private final OWLEditorKit editorKit;

    public SearchOptionsPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JPanel searchOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        add(searchOptionsPanel, BorderLayout.NORTH);

        caseSensitive = new JCheckBox(new AbstractAction("Case sensitive") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setCaseSensitive(caseSensitive.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(caseSensitive);

        wholeWordsCheckbox = new JCheckBox(new AbstractAction("Whole words") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setWholeWords(wholeWordsCheckbox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(wholeWordsCheckbox);

        ignoreWhiteSpaceCheckbox = new JCheckBox(new AbstractAction("Ignore white space") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setIgnoreWhiteSpace(ignoreWhiteSpaceCheckbox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(ignoreWhiteSpaceCheckbox);


        useRegexCheckBox = new JCheckBox(new AbstractAction("Regular expression") {
            public void actionPerformed(ActionEvent e) {
                OWLEntityFinderPreferences.getInstance().setUseRegularExpressions(useRegexCheckBox.isSelected());
                fireSearchRequestOptionChanged();
            }
        });
        searchOptionsPanel.add(useRegexCheckBox);

    }

    public void addListener(SearchOptionsChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SearchOptionsChangedListener listener) {
        listeners.remove(listener);
    }

    public void refresh() {
        OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();

        caseSensitive.setSelected(prefs.isCaseSensitive());
        useRegexCheckBox.setSelected(prefs.isUseRegularExpressions());
        wholeWordsCheckbox.setSelected(prefs.isWholeWords());
        ignoreWhiteSpaceCheckbox.setSelected(prefs.isIgnoreWhiteSpace());

    }

    private void fireSearchRequestOptionChanged() {
        for (SearchOptionsChangedListener listener : new ArrayList<>(listeners)) {
            listener.searchRequestOptionChanged();
        }
    }
}
