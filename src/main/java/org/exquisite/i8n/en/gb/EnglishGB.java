package org.exquisite.i8n.en.gb;

import org.exquisite.i8n.CultureInfo;
/**
 * CultureInfo for English (GB).
 * @author David
 * @see org.exquisite.i8n.CultureInfo
 */
public class EnglishGB extends CultureInfo {
	
	/**
	 * Selected field values are overwritten with values appropriate for English (GB) language culture.
	 */
	public EnglishGB(){
		this.csvDelimiter = ",";
		this.paramDelimiter = this.csvDelimiter;
		this.decimalPoint = ".";
		this.sum = "SUM";
		this.iff = "IF";
		this.and = "AND";
		this.or = "OR";
		this.not = "NOT";
	}
}
