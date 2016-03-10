package org.exquisite.i8n;

import org.exquisite.i8n.de.German;
import org.exquisite.i8n.en.gb.EnglishGB;

import java.util.Locale;

public class Culture {
    private static Locale locale;
    private static CultureInfo currentCulture;

    /**
     * Sets the culture to use for all out going data from server based on current locale.
     *
     * @param newLocale
     */
    public static void setCulture(Locale newLocale) {
        locale = newLocale;
        CultureInfo culture = getCultureForLocale(locale);
        currentCulture = culture;
    }

    /**
     * @return - the current language culture in use.
     */
    public static CultureInfo getCurrentCulture() {
        return currentCulture;
    }

    /**
     * @param locale
     * @return a CultureInfo object associated with the locale.
     */
    public static CultureInfo getCultureForLocale(Locale locale) {
        String language = locale.getLanguage();

        switch (language) {
            case "de":
                return new German();
            case "en":
                return new EnglishGB();
            default:
                System.out.println("WARNING: Locale " + locale
                        .getCountry() + " not currently supported. getCultureForLocale returned null.");
                return null;
        }
    }
}
