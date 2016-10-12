package org.exquisite.protege.model.search;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Preferences used for the search options in debugger's input ontology view.
 */
public class DebuggerSearchPreferences {

    private static final String PREFERENCES_KEY = "org.exquisite.protege.finder";

    private static final String CASE_SENSITIVE_KEY = "CASE_SENSITIVE_KEY";

    private static final String WHOLE_WORDS_KEY = "WHOLE_WORDS_KEY";

    private static final String IGNORE_WHITE_SPACE_KEY = "IGNORE_WHITE_SPACE_KEY";

    private static final String USE_REGULAR_EXPRESSIONS_KEY = "USE_REGULAR_EXPRESSIONS";

    private static final boolean DEFAULT_CASE_SENSITIVE_VALUE = false;

    private static final boolean DEFAULT_WHOLE_WORDS_VALUE = false;

    private static final boolean DEFAULT_GNORE_WHITE_SPACE_VALUE = false;

    private static final boolean DEFAULT_USE_REGULAR_EXPRESSIONS_VALUE = false;

    private static DebuggerSearchPreferences instance;

    private boolean caseSensitive;

    private boolean wholeWords;

    private boolean ignoreWhiteSpace;

    private boolean useRegularExpressions;

    private DebuggerSearchPreferences() {
        load();
    }

    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
    }

    private void load() {
        Preferences preferences = getPreferences();
        caseSensitive = preferences.getBoolean(CASE_SENSITIVE_KEY, DEFAULT_CASE_SENSITIVE_VALUE);
        wholeWords = preferences.getBoolean(WHOLE_WORDS_KEY, DEFAULT_WHOLE_WORDS_VALUE);
        ignoreWhiteSpace = preferences.getBoolean(IGNORE_WHITE_SPACE_KEY, DEFAULT_GNORE_WHITE_SPACE_VALUE);
        useRegularExpressions = preferences.getBoolean(USE_REGULAR_EXPRESSIONS_KEY, DEFAULT_USE_REGULAR_EXPRESSIONS_VALUE);
    }

    public static synchronized DebuggerSearchPreferences getInstance() {
        if (instance == null) {
            instance = new DebuggerSearchPreferences();
        }
        return instance;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        getPreferences().putBoolean(CASE_SENSITIVE_KEY, caseSensitive);
    }

    public boolean isIgnoreWhiteSpace() {
        return ignoreWhiteSpace;
    }

    public void setIgnoreWhiteSpace(boolean ignoreWhiteSpace) {
        this.ignoreWhiteSpace = ignoreWhiteSpace;
        getPreferences().putBoolean(IGNORE_WHITE_SPACE_KEY, ignoreWhiteSpace);
    }

    public boolean isWholeWords() {
        return wholeWords;
    }

    public void setWholeWords(boolean wholeWords) {
        this.wholeWords = wholeWords;
        getPreferences().putBoolean(WHOLE_WORDS_KEY, wholeWords);
    }

    public boolean isUseRegularExpressions() {
        return useRegularExpressions;
    }

    public void setUseRegularExpressions(boolean useRegularExpressions) {
        this.useRegularExpressions = useRegularExpressions;
        getPreferences().putBoolean(USE_REGULAR_EXPRESSIONS_KEY, useRegularExpressions);

    }
}
