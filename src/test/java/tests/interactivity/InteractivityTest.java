package tests.interactivity;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.core.engines.InteractiveDiagnosisEngine;
import org.exquisite.core.engines.query.QueryComputation;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.engines.query.CKK;
import org.exquisite.core.engines.query.scoring.QSSFactory;
import org.exquisite.core.engines.query.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.exquisite.core.measurements.MeasurementManager.*;

/**
 * Test class to test the user interactivity tests.diagnosis engine with spreadsheets
 *
 * @author Schmitz
 */
public class InteractivityTest {

    private static final Logger log = Logger.getLogger(InteractivityTest.class.getSimpleName());

    static String inputFileDirectory = "experiments/spreadsheetsindividual/";

    // static String scenario = "formula_query_example";
    // static String scenario = "Hospital_Payment_Calculation_TS";
    static String scenario = "salesforecast_TC_2Faults";

    static String correctExtension = "_correct";
    static String fileEnding = ".xml";

    // new StdScenario("new/benchmark-5faults.xml", searchDepth, maxDiags),
    // new StdScenario("new/Hospital_Payment_Calculation.xml", searchDepth, maxDiags),
    // new StdScenario("new/xxen.xml", searchDepth, maxDiags),
    // new StdScenario("SemUnitEx2_2fault.xml", searchDepth, maxDiags),
    // new StdScenario("VDEPPreserve_1fault.xml", searchDepth, maxDiags),

    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));

        InteractivityTest test = new InteractivityTest();
        test.runInteractivityTest();
    }

    public void runInteractivityTest() {
        log.info("Starting interacitivity test");

        ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = true;

        String fullInputFilename = inputFileDirectory + scenario + fileEnding;
        String fullCorrectFilename = inputFileDirectory + scenario + correctExtension + fileEnding;

        EngineType engineType = EngineType.HSDagStandardQX;
        // EngineType engineType = EngineType.HeuristicSearch;

        int threadPoolSize = 4;

        IDiagnosisEngine<Constraint> innerEngine = EngineFactory
                .makeEngineFromXMLFile(engineType, fullInputFilename, threadPoolSize);

        Collections.shuffle(innerEngine.getDiagnosisModel().getPossiblyFaultyStatements());

        int diagnosesPerQuery = 9;

        Scoring scoring = QSSFactory.createMinScoreQSS();

        QueryComputation<Constraint> queryComputation = new CKK<>(innerEngine, scoring);

        Diagnosis<Constraint> correctDiagnosis = getCorrectDiagnosis(innerEngine, fullCorrectFilename);

        InteractiveDiagnosisEngine<Constraint> engine = new InteractiveDiagnosisEngine<>(innerEngine.getDiagnosisModel(),
                innerEngine, diagnosesPerQuery,
                queryComputation, correctDiagnosis);

        try {
            List<Diagnosis<Constraint>> diagnoses = engine.calculateDiagnoses();
            log(correctDiagnosis, engine, diagnoses);
        } catch (DiagnosisException e) {
            e.printStackTrace();
        }

        log.info("Finished interactivity test");
    }

    protected static <T> void  log(Diagnosis<T> correctDiagnosis,
    InteractiveDiagnosisEngine<T>
            engine,
                     List<Diagnosis<T>> diagnoses) {
        log.info("Found " + diagnoses.size() + " tests.diagnosis:  " + Utilities
                .printSortedDiagnoses(diagnoses, ' '));
        assert correctDiagnosis != null;
        log.info("Real tests.diagnosis was: " + correctDiagnosis.toString());
        log.info("Needed " + getCounter(COUNTER_INTERACTIVE_DIAGNOSES).value() + " tests.diagnosis runs and " +
               getCounter(COUNTER_INTERACTIVE_NQUERIES).value() + " user interactions with a " +
                "total of " +
                getCounter(COUNTER_INTERACTIVE_QSTMT) + " queried statements.");
        long total = getTimer(TIMER_INTERACTIVE_SESSION).total();
        log.info(String.format("Diagnoses calculation took %.2f s.", total / 1000f));
        log.info(String.format("User interaction calculation took %.2f s.",
                (total-getTimer(TIMER_INTERACTIVE_DIAGNOSES).total()) / 1000f));
    }

    /**
     * Returns the correct tests.diagnosis based on the differences between the mutated and the correct spreadsheet xmls. Diagnosis might not be minimal.
     *
     * @param engine
     * @param correctFilename
     * @return
     */
    private Diagnosis<Constraint> getCorrectDiagnosis(IDiagnosisEngine<Constraint> engine, String correctFilename) {
        ExquisiteAppXML mutatedXML = ((ExcelExquisiteSession<Constraint>)engine.getDiagnosisModel()).appXML;
        ExquisiteAppXML correctXML = ExquisiteAppXML.parseToAppXML(correctFilename);

        Diagnosis<Constraint> diag = new Diagnosis<>(new ArrayList<>(), engine.getDiagnosisModel());

        for (Constraint c : engine.getDiagnosisModel().getPossiblyFaultyStatements()) {
            String cell = engine.getDiagnosisModel().getConstraintName(c);
            if (!mutatedXML.getFormulas().get(cell).equals(correctXML.getFormulas().get(cell))) {
                diag.getElements().add(c);
            }
        }

        return diag;
    }
}
