package org.exquisite.diagnosis.ranking.smell;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


/**
 * 
 * @author Philipp-Malte Lingnau
 * 
 */


public class ConfigReader {
	Properties props;
	
	public static final String configFilePath = "./smells/Configuration/config.properties";
	
	/**
	 * Config reader loads the config file
	 */
	public ConfigReader() {
		props = new Properties();
		FileInputStream fileInputStream;
		try {
			
			String path = configFilePath;
			fileInputStream = new FileInputStream(path);


			props.load(fileInputStream);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the parameter from config and returns the value as int
	 * @param configurationString
	 * @return Returns the value of the given configurationString as integer
	 */
	public int getIntConfigValue(String configurationString) {
		return (Integer.parseInt(props.getProperty(configurationString)));
	}

	/**
	 * Gets the parameter from config and returns the value as float
	 * @param configurationString
	 * @return Returns the value of the given configurationString as float
	 */
	public float getFloatConfigValue(String configurationString) {
		return (Float.parseFloat(props.getProperty(configurationString)));
	}
	
	/**
	 * Gets the parameter from config and returns the value as string
	 * @param configurationString
	 * @return Returns the value of the given configurationString as string
	 */
	public String getStringConfigValue(String configurationString) {
		return props.getProperty(configurationString);
	}
}