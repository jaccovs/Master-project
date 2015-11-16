package org.exquisite.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * For any utilities to manipulate the file system.
 *
 * @author David
 */
public class FileUtilities {

    public static void main(String[] args) {
        String fileContent = "Here,is,some,content...";
        String fileName = ".\\logs\\testFile.txt";

        try {
            FileUtilities.writeToFile(fileContent, fileName, false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void writeToFile(String line, String filePath, boolean append) throws IOException {
        FileWriter fw = new FileWriter(filePath, append);
        PrintWriter pw = new PrintWriter(fw);

        pw.print(line);
        pw.println();
        //Flush the output to the file
        pw.flush();
        //Close the Print Writer
        pw.close();
        //Close the File Writer
        fw.close();
    }
}
