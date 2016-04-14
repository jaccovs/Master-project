package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.engines.IDiagnosisEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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
        writer.print("Ontologies; ");
        for (String ontology : Configuration.getOntologies()) writer.print(ontology + ", ");
        writer.println();
        writer.print("Diagnosesizes; ");
        for (Integer size : Configuration.getDiagnoseSizes()) writer.print(size + ", ");
        writer.println();
        writer.println("Iterations; " + Configuration.getIterations());
        writer.println("Start of Evalrun; " + getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS"));
        writer.println();

        writer.println("Systeminformation:");
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            writer.println("Current host name ; " + ip.getHostName());
            writer.println("Current IP address ; " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        String nameOS= System.getProperty("os.name");
        writer.println("Operating system Name ; " + nameOS);
        String osType= System.getProperty("os.arch");
        writer.println("Operating system type ; " + osType);
        String osVersion= System.getProperty("os.version");
        writer.println("Operating system version ; " + osVersion);

        writer.println("Processor-ID ; " + System.getenv("PROCESSOR_IDENTIFIER"));
        writer.println("Processor-Architecture ; " + System.getenv("PROCESSOR_ARCHITECTURE"));
        writer.println("Number of processors ; " + System.getenv("NUMBER_OF_PROCESSORS"));
        /* Total number of processors or cores available to the JVM */
        writer.println("Available processors (cores) ; " +
                Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        writer.println("Free memory (bytes) ; " +
                Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        writer.println("Maximum memory (bytes) ; " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        writer.println("Total memory (bytes) ; " +
                Runtime.getRuntime().totalMemory());


        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            writer.print("Current MAC address ; ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            writer.println(sb.toString());
        } catch (SocketException e) {
            e.printStackTrace();
        }

        writer.println();
        writer.println();
        writer.flush();
    }

    void writeHeader(String header) {
        writer.println(header);
        writer.flush();
    }

    void writeIteration(Iteration iteration) {
        writer.println(iteration.getLine());
        writer.flush();
    }

    void writeStatistics(Statistics statistics) {
        writer.println(statistics.getStatistics());
        writer.flush();
    }

    void close(Exception e) {
        if (writer!=null) {
            writer.println();
            writer.println();
            if (e == null)
                writer.println("Successfully finished evaluation at " + getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS"));
            else
                writer.println("Evaluation has been interrupted because of an exception at " + getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS") + "\nException cause: " + e.getMessage());

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
