package org.exquisite.protege.ui.panel.search;

import com.google.common.collect.ImmutableList;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.search.DebuggerFinderPreferences;
import org.exquisite.protege.model.search.DebuggerSearchManager;
import org.exquisite.protege.model.search.DebuggerSearchResult;
import org.exquisite.protege.ui.panel.axioms.PossiblyFaultyAxiomsPanel;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchRequest;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.ui.search.SearchOptionsChangedListener;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

public class SearchPanel extends JPanel {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(SearchPanel.class.getCanonicalName());

    private static final String WHITE_SPACE_PATTERN = "\\s+";

    private final JTextField searchField;

    private final SearchOptionsPanel searchOptionsPanel;

    private final OWLEditorKit editorKit;

    private final EditorKitHook editorKitHook;

    private String searchString = "";

    private PossiblyFaultyAxiomsPanel axiomsPanel;

    private DebuggerSearchManager searchManager;

    public SearchPanel(PossiblyFaultyAxiomsPanel axiomsPanel, OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        this.axiomsPanel = axiomsPanel;
        this.editorKit = editorKit;
        this.editorKitHook = editorKitHook;

        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        add(box, BorderLayout.NORTH);

        searchField = new AugmentedJTextField("Enter search string");
        box.add(searchField);

        searchOptionsPanel = new SearchOptionsPanel();
        box.add(searchOptionsPanel);

        searchOptionsPanel.addListener(new SearchOptionsChangedListener() {
            public void searchRequestOptionChanged() {
                doSearch();
            }

            public void searchResultsPresentationOptionChanged() {}
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void doSearch() {
        final String s = searchField.getText().trim();
        this.searchString = checkNotNull(s);
        searchOptionsPanel.refresh();

        final java.util.List<OWLLogicalAxiom> axiomsToDisplay = new ArrayList<>();
        if (searchString.trim().isEmpty()) {
            final OWLOntology ontology = editorKit.getModelManager().getActiveOntology();
            final DiagnosisModel<OWLLogicalAxiom> dm = editorKitHook.getActiveOntologyDebugger().getDiagnosisModel();
            axiomsToDisplay.addAll(PossiblyFaultyAxiomsPanel.getAllPossiblyFaultyLogicalAxioms(ontology, dm));
            axiomsPanel.setAxiomsToDisplay(axiomsToDisplay);
            axiomsPanel.updateDisplayedAxioms();
        } else {
            try {
                SearchRequest searchRequest = createSearchRequest();
                logger.debug(searchRequest.toString());

                DebuggerSearchManager searchManager = getSearchManager();
                searchManager.performSearch(searchRequest, searchResults -> {
                    for (SearchResult result : searchResults) {
                        final OWLAxiom axiom = ((DebuggerSearchResult) result).getAxiom();
                        axiomsToDisplay.add((OWLLogicalAxiom) axiom);
                    }
                    Collections.sort(axiomsToDisplay);
                    SwingUtilities.invokeLater(() -> {
                                axiomsPanel.setAxiomsToDisplay(axiomsToDisplay);
                                axiomsPanel.updateDisplayedAxioms();
                            }
                    );
                });
            } catch (PatternSyntaxException psx) {
                logger.info("Invalid regular expression in search pattern: {}", psx.getPattern());
            }
        }

    }

    private SearchRequest createSearchRequest() throws PatternSyntaxException {
        DebuggerFinderPreferences prefs = DebuggerFinderPreferences.getInstance();
        int flags = Pattern.DOTALL | (prefs.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);

        ImmutableList.Builder<Pattern> builder = ImmutableList.builder();

        String preparedSearchString;
        for (String splitSearchString : this.searchString.split(WHITE_SPACE_PATTERN)) {
            if (prefs.isUseRegularExpressions()) {
                preparedSearchString = splitSearchString;
                if (prefs.isIgnoreWhiteSpace()) {
                    preparedSearchString = preparedSearchString.replace(" ", WHITE_SPACE_PATTERN);
                }
            }
            else {
                if (prefs.isIgnoreWhiteSpace()) {
                    StringBuilder sb = new StringBuilder();
                    String[] split = splitSearchString.split(WHITE_SPACE_PATTERN);
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        sb.append(Pattern.quote(s));
                        if (i < split.length - 1) {
                            sb.append(WHITE_SPACE_PATTERN);
                        }
                    }
                    preparedSearchString = sb.toString();
                }
                else {
                    preparedSearchString = Pattern.quote(splitSearchString);
                }
            }
            if (prefs.isWholeWords()) {
                preparedSearchString = "\\b(:?" + preparedSearchString + ")\\b";
            }
            builder.add(Pattern.compile(preparedSearchString, flags));
        }
        return new SearchRequest(builder.build());
    }

    private synchronized DebuggerSearchManager getSearchManager() {
        if (searchManager == null) {
            searchManager = new DebuggerSearchManager();
            searchManager.setup(this.editorKit);
            searchManager.initialise();
        }
        return searchManager;
    }

    public void resetSearchField() {
        this.searchString = "";
        this.searchField.setText(this.searchString);
    }
}
