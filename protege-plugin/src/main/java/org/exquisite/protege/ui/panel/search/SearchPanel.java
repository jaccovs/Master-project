package org.exquisite.protege.ui.panel.search;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.search.SearchOptionsChangedListener;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    private final JTextField searchField;

    private final SearchOptionsPanel searchOptionsPanel;

    private final OWLEditorKit editorKit;

    public SearchPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        add(box, BorderLayout.NORTH);

        searchField = new AugmentedJTextField("Enter search string");
        box.add(searchField);

        searchOptionsPanel = new SearchOptionsPanel(editorKit);
        box.add(searchOptionsPanel);

        searchOptionsPanel.addListener(new SearchOptionsChangedListener() {
            public void searchRequestOptionChanged() {
                doSearch();
            }

            public void searchResultsPresentationOptionChanged() {
                updateSearchResultsPresentation();
            }
        });
    }

    private void doSearch() {

    }

    private void updateSearchResultsPresentation() {

    }
}
