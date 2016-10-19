package org.exquisite.protege.model.preferences;

import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A central input validation class to check the preference values of the debugger.
 */
public class InputValidator {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InputValidator.class.getCanonicalName());

    /**
     * Parses and validates the diagnosis engine type preference.
     *
     * @param engineType the string value.
     * @return The engine type instance if known or the default otherwise.
     */
    static DebuggerConfiguration.DiagnosisEngineType parseEngineType(String engineType) {
        return validateEngineType(engineType);
    }

    /**
     * Validates the pretended engine type. If unknown the default engine type is returned.
     *
     * @param anEngineType an engine type. Instances of type <code>String</code> and <code>DiagnosisEngineType</code> are expected.
     * @return a valid engine type or the default if invalid.
     * @see org.exquisite.protege.model.preferences.DebuggerConfiguration.DiagnosisEngineType
     */
    public static DebuggerConfiguration.DiagnosisEngineType validateEngineType(Object anEngineType) {
        if (anEngineType instanceof String) {
            final String sEngineType = (String)anEngineType;
            for (DebuggerConfiguration.DiagnosisEngineType validEngineType : DebuggerConfiguration.DiagnosisEngineType.values())
                if (sEngineType.trim().equalsIgnoreCase(validEngineType.toString()))
                    return validEngineType;
        } else if (anEngineType instanceof DebuggerConfiguration.DiagnosisEngineType) {
            final DebuggerConfiguration.DiagnosisEngineType engineType = (DebuggerConfiguration.DiagnosisEngineType)anEngineType;
            for (DebuggerConfiguration.DiagnosisEngineType validEngineType : DebuggerConfiguration.DiagnosisEngineType.values())
                if (engineType.equals(validEngineType))
                    return engineType;
        }
        logger.warn("Unknown DiagnosisEngine Type {}. Applying default: {}.", anEngineType, DefaultPreferences.getDefaultDiagnosisEngineType());
        return DefaultPreferences.getDefaultDiagnosisEngineType();
    }

    /**
     * Parses and validates the requirements measurement preference.
     *
     * @param rm the string value.
     * @return The RM (requirements measurement) if known or the default otherwise.
     */
    static DebuggerConfiguration.RM parseRM(String rm) {
        return validateRM(rm);
    }

    /**
     * Validates the pretended requirements measurement preference. If unknown the default requirements measurement is
     * applied.
     * @param anRM an instance of either <code>String</code> or <code>DebuggerConfiguration.RM</code> pretending to be
     *             a requirements measurement.
     * @return a valid requirements measurement or the default otherwise.
     * @see DebuggerConfiguration.RM
     */
    public static DebuggerConfiguration.RM validateRM(Object anRM) {
        if (anRM instanceof String) {
            final String sRM = (String)anRM;
            for (DebuggerConfiguration.RM validRM : DebuggerConfiguration.RM.values())
                if (sRM.trim().equalsIgnoreCase(validRM.toString()))
                    return validRM;
        } else if (anRM instanceof DebuggerConfiguration.RM) {
            final DebuggerConfiguration.RM rm = (DebuggerConfiguration.RM)anRM;
            for (DebuggerConfiguration.RM validRM : DebuggerConfiguration.RM.values())
                if (rm.equals(validRM))
                    return rm;
        }
        logger.warn("Unknown Requirements Measurement {}. Applying default: {}", anRM, DefaultPreferences.getDefaultRM());
        return DefaultPreferences.getDefaultRM();
    }


    /**
     * Parses and validates the sort criterion preference.
     *
     * @param sortcriterion the string value.
     * @return The sort criterion if known or the default otherwise.
     */
    static DebuggerConfiguration.SortCriterion parseSortCriterion(String sortcriterion) {
        for (DebuggerConfiguration.SortCriterion type : DebuggerConfiguration.SortCriterion.values())
            if (type.toString().equals(sortcriterion))
                return type;
        logger.warn("Unknown Sortcriterion " + sortcriterion + ". Applying default value.");
        return DefaultPreferences.getDefaultSortCriterion();
    }

    /**
     * Validates the pretended sort criterion preference. If unknown the default sort criterion is applied.
     * @param aSortCriterion an instance of either <code>String</code> or <code>DebuggerConfiguration.SortCriterion</code>
     *                       pretending to be a sort criterion.
     * @return a valid sort criterion or the default otherwise.
     * @see DebuggerConfiguration.SortCriterion
     */
    public static DebuggerConfiguration.SortCriterion validateSortCriterion(Object aSortCriterion) {
        if (aSortCriterion instanceof String) {
            final String sSortCriterion = (String)aSortCriterion;
            for (DebuggerConfiguration.SortCriterion validSortCrit : DebuggerConfiguration.SortCriterion.values())
                if (sSortCriterion.trim().equalsIgnoreCase(validSortCrit.toString()))
                    return validSortCrit;
        } else if (aSortCriterion instanceof DebuggerConfiguration.SortCriterion) {
            final DebuggerConfiguration.SortCriterion sortCriterion = (DebuggerConfiguration.SortCriterion) aSortCriterion;
            for (DebuggerConfiguration.SortCriterion validSortCrit : DebuggerConfiguration.SortCriterion.values()) {
                if (sortCriterion.equals(validSortCrit))
                    return sortCriterion;
            }
        }
        logger.warn("Unknown Sortcriterion {}. Applying default: {}", aSortCriterion, DefaultPreferences.getDefaultSortCriterion());
        return DefaultPreferences.getDefaultSortCriterion();
    }

    /**
     * Parses and validates the cost estimator property.
     *
     * @param costEstimator the string value.
     * @return The cost estimator if known or the default otherwise.
     */
    static DebuggerConfiguration.CostEstimator parseCostEstimator(String costEstimator) {
        for (DebuggerConfiguration.CostEstimator type : DebuggerConfiguration.CostEstimator.values())
            if (type.toString().equals(costEstimator))
                return type;
        logger.warn("Unknown Cost Estimator " + costEstimator + ". Applying default value.");
        return DefaultPreferences.getDefaultCostEstimator();
    }

    /**
     * Validates the pretended cost estimator preference. If unknown the default cost estimator is applied.
     * @param aCostEstimator an instance of either <code>String</code> or <code>DebuggerConfiguration.CostEstimator</code>
     *                       pretending to be a cost estimator.
     * @return a valid cost estimator  or the default otherwise.
     * @see DebuggerConfiguration.CostEstimator
     */
    public static DebuggerConfiguration.CostEstimator validateCostEstimator(Object aCostEstimator) {
        if (aCostEstimator instanceof String) {
            final String sCostEstimator = (String)aCostEstimator;
            for (DebuggerConfiguration.CostEstimator validCostEstimator : DebuggerConfiguration.CostEstimator.values())
                if (sCostEstimator.trim().equalsIgnoreCase(validCostEstimator.toString()))
                    return validCostEstimator;
        } else if (aCostEstimator instanceof DebuggerConfiguration.CostEstimator) {
            final DebuggerConfiguration.CostEstimator costEstimator = (DebuggerConfiguration.CostEstimator) aCostEstimator;
            for (DebuggerConfiguration.CostEstimator validCostEstimator : DebuggerConfiguration.CostEstimator.values())
                if (costEstimator.equals(validCostEstimator))
                    return costEstimator;
        }
        logger.warn("Unknown Cost Estimator {}. Applying default: {}.", aCostEstimator, DefaultPreferences.getDefaultCostEstimator());
        return DefaultPreferences.getDefaultCostEstimator();
    }

    /**
     * Parses and validates an integer preference.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param min Minimal possible value.
     * @param max Maximal possible value.
     * @param defaultValue Default value.
     * @return Value of property or a corrected value.
     */
    static Integer parseInt(Properties properties, String key, Integer min, Integer max, Integer defaultValue) {
        return Integer.parseInt(validateInt(properties.get(key), min, max, defaultValue));
    }

    public static String validateInt(Object aValue, Integer min, Integer max, Integer defaultValue) {
        if (aValue == null) return defaultValue.toString();

        Integer value;
        try {
            if (aValue instanceof String) {
                value = Integer.parseInt((String) aValue);
            } else {
                value = (Integer) aValue;
            }

            if (value < min) {
                logger.warn("The preference value {} is out of bounds! Applying minimal possible value {}.", value, min);
                value = min;
            }
            if (value > max) {
                logger.warn("The preference value {} is out of bounds! Applying maximal possible value {}.", value, max);
                value = max;
            }
        } catch (NumberFormatException nfe) {
            logger.warn("The preference value {} is not a valid number! Applying default value {}.", aValue, defaultValue);
            value = defaultValue;
        }
        return value.toString();
    }

    /**
     * Helper method to parse boolean property with fall back to default value.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param defaultValue Default value.
     * @return Value of property.
     */
    static Boolean parseBoolean(Properties properties, String key, Boolean defaultValue) {
        return validateBoolean(properties.get(key), defaultValue);
    }

    public static Boolean validateBoolean(Object aValue, Boolean defaultValue) {
        if (aValue == null) return defaultValue;
        return Boolean.parseBoolean(aValue.toString());
    }

    /**
     * Helper method to parse double property with fall back to default value.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param defaultValue Default value.
     * @return Value of property.
     */
    static Double parseDouble(Properties properties, String key, Double defaultValue) {
        return parseDouble(properties, key, Double.MIN_VALUE, Double.MAX_VALUE, defaultValue);
    }

    /**
     * Helper method to parse double property with fall back to default value.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param min The minimal possible value.
     * @param max The maximal possible value.
     * @param defaultValue Default value.
     * @return Value of property.
     */
    static Double parseDouble(Properties properties, String key, Double min, Double max, Double defaultValue) {
        return Double.parseDouble(validateDouble(properties.get(key), min, max, defaultValue));
    }

    public static String validateDouble(Object aValue, Double min, Double max, Double defaultValue) {
        if (aValue == null) return defaultValue.toString();

        Double value;
        try {
            if (aValue instanceof String) {
                value = Double.parseDouble((String) aValue);
            } else {
                value = (Double) aValue;
            }

            if (value < min) {
                logger.warn("The preference value {} is out of bounds! Applying minimal possible value {}.", value, min);
                value = min;
            }
            if (value > max) {
                logger.warn("The preference value {} is out of bounds! Applying maximal possible value {}.", value, max);
                value = max;
            }
        } catch (NumberFormatException nfe) {
            logger.warn("The preference value {} is not a valid number! Applying default value {}.", aValue, defaultValue);
            value = defaultValue;
        }
        return value.toString();
    }
}
