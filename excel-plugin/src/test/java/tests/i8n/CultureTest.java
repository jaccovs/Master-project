package tests.i8n;

import java.util.Locale;

import org.exquisite.i8n.Culture;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.i8n.de.German;
import org.exquisite.i8n.en.gb.EnglishGB;

/**
 * For testing the CultureInfo classes.
 * @author David
 *
 */
public class CultureTest {

	public static void main(String[] args)
	{
		System.out.println("Start Culture Tests.");
		new CultureTest().run();
		System.out.println("End of Culture Tests.");
	}
	
	public void run()
	{
		setCultureTest();
		
		String toTest = "this.is.to.test";
		String[] splits = toTest.split("\\.");
		System.out.println("splits length = " + splits.length);
		String[] newSplits = toTest.split(",");
		System.out.println("newSplits length = " + newSplits.length);
	}
	
	private void setCultureTest()
	{
		Culture.setCulture(Locale.GERMAN);
		CultureInfo culture = Culture.getCurrentCulture();
		boolean isGerman = (culture instanceof German);
				
		Culture.setCulture(Locale.ENGLISH);
		culture = Culture.getCurrentCulture();
		boolean isEnglishGB = (culture instanceof EnglishGB);
		
		Culture.setCulture(Locale.CHINESE);
		culture = Culture.getCurrentCulture();
		boolean isDefault = (culture instanceof German); //assuming default is set to German.
			
		System.out.println("All setCulture tests passed? " + (isGerman && isEnglishGB && isDefault));		
	}	
}
