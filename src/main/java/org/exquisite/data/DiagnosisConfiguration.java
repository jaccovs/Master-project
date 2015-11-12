package org.exquisite.data;

import java.util.Locale;

import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteEnums.ExquisiteLocaleFlag;
import org.exquisite.datamodel.ExquisiteUserSettings;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;

/**
 * A class for storing configuration data for various aspects of Exquisites operation.
 */
public class DiagnosisConfiguration 
{
	/**
	 * Toggles debug messages in ServerProtocol class on/off
	 */
	public boolean showServerDebugMessages = false;
	
	public EngineType diagnosisEngine;
	
	/**
	 * Defines how far down to search for a tests.diagnosis. - setting to -1 makes search run continuously.
	 */
	public int searchDepth = -1;
	
	/**
	 * Maximum number of diagnoses to return before exiting. - setting to -1 makes system record all diagnoses.
	 */
	public int maxDiagnoses = -1;
	
	public double probabilityThreshold = 0d;
	
	/**
	 * Changing the locale changes the culture used in output e.g. for csv delimiters etc.
	 * Currently supports German and English.
	 * 
	 * At some point this could be made dynamic based on culture info sent from client.
	 * Since all the development machines are German then it has just been parameterized here for now.
	 */
	public Locale defaultOutputLocale = Locale.GERMAN;
	
	/**
	 * @param overrides config field values with values from user settings.
	 */
	public void updateFromUserSettings(ExquisiteUserSettings userSettings){
		
		this.diagnosisEngine = userSettings.getDiagnosisEngine();
		this.maxDiagnoses = userSettings.getMaxDiagnoses();
		this.searchDepth = userSettings.getSearchDepth();		
		this.probabilityThreshold = userSettings.getProbabilityThreshold();
		this.defaultOutputLocale = mapUserLocale(userSettings.getLocaleFlag());
		this.showServerDebugMessages = userSettings.getShowServerDebugMessages();
	}
	
	/**
	 * Reset config with default values
	 */
	public void resetToDefaults(){
		this.diagnosisEngine = EngineType.HSDagStandardQX;
		this.maxDiagnoses = -1;
		this.searchDepth = -1;
		this.probabilityThreshold = 0d;
		this.defaultOutputLocale = Locale.GERMAN;
		this.showServerDebugMessages = false;
	}
	
	/**
	 * Updates the locale to use internally based on the flag value recieved from ExquisiteAppXML.
	 * @param flag
	 * @return Locale to use internally.
	 */
	private Locale mapUserLocale(ExquisiteLocaleFlag flag){
		switch(flag){
			case German:
				return Locale.GERMAN;
			case EnglishGB:
				return Locale.ENGLISH;
		}
		return null;
	}
	
	/**
	 * Time out in ms
	 */
	public int timeOut = 10000;
	
	/**
	 * Default search strategy to use
	 */
	public SearchStrategies searchStrategy = null;	
	
	/**
	 * Time a search strategy has to win to be set as default search strategy
	 */
	public int successTime = 3;
}
