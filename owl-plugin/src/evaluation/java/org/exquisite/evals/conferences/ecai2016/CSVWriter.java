package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.engines.IDiagnosisEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author wolfi
 */
public class CSVWriter {
    PrintWriter writer;
    IDiagnosisEngine engine;
    int iteration;

    void open() throws FileNotFoundException {
        File f = new File("src/evaluation/resources/evaluations/ecai2016/eval_" + getCurrentTime("yyyyMMddHHmmss") + ".csv");
        writer = new PrintWriter(f);
    }

    void writeConfiguration() {
        writer.println("Configuration:");
        writer.print("Ontologies: ");
        for (String ontology : Configuration.getOntologies()) writer.print(ontology + ", ");
        writer.println();
        writer.print("Diagnosesizes: ");
        for (Integer size : Configuration.getDiagnoseSizes()) writer.print(size + ", ");
        writer.println();
        writer.println("Iterations: " + Configuration.getIterations());
        writer.println("Start of Evalrun: " + getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS"));
        writer.println();
        writer.println();
        writer.flush();
    }

    void writeHeader(Evaluation.Iteration iteration) {
        writer.println(iteration.getHeader());
        writer.flush();
    }

    void writeIteration(Evaluation.Iteration iteration) {
        writer.println(iteration.getLine());
        writer.flush();
    }

    void close() {
        if (writer!=null) {
            writer.println();
            writer.println();
            writer.println("Successfully finished evaluation at " + getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS"));
            writer.flush();
            writer.close();
        }
    }

    String getCurrentTime(String pattern) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(cal.getTime());
    }
}
