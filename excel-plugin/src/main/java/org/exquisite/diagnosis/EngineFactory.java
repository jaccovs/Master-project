package org.exquisite.diagnosis;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.engines.HSDagEngine;
import org.exquisite.core.engines.SpectrumBasedDiagnosisEngine;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.engines.*;
import org.exquisite.diagnosis.invquickxplain.InvQXDiagnosisEngine;
import org.exquisite.diagnosis.invquickxplain.MXPandInvQXP;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;

import java.util.Dictionary;
import java.util.Hashtable;
//import evaluations.dxc.synthetic.minizinc.MZDiagnosisEngine;
//import evaluations.dxc.synthetic.minizinc.MZModelGenerator;

/**
 * Just a helper class to build HSDagEngine (or any other class that implements
 * IDiagnosisEngine) instances in different configurations.
 *
 * @author David
 */
public class EngineFactory {

    // /**
    // * Another way of instantiating a tests.diagnosis engine instance.
    // * @param config - a config enum describing the desired tests.diagnosis engine
    // configuration.
    // * @return - an instance of a class that implements IDiagnosisEngine.
    // * @throws UnsupportedEngineException - throws this exception if no engine
    // * could be found that matches the configuration description.
    // */
    // public static IDiagnosisEngine makeEngine(EngineType config,
    // ExcelExquisiteSession sessionData) throws UnsupportedEngineException{
    // switch (config){
    // case HSDagStandardQX:
    // return makeDAGEngineStandardQx(sessionData);
    // case ParaHSDagStandardQX:
    // final int DefaultThreadCount = 4;
    // return makeParaDagEngineStandardQx(sessionData, DefaultThreadCount);
    // default:
    // throw new UnsupportedEngineException();
    // }
    // }

    /**
     * Creates an engine from an Exquisite XML file
     *
     * @param engineType     the type of the engine
     * @param xmlFilePath    the file to use
     * @param threadPoolSize the threadpool size (in case of a parallel engine)
     * @return a fresh engine
     */
    public static IDiagnosisEngine<Constraint> makeEngineFromXMLFile(EngineType engineType,
                                                                     String xmlFilePath, int threadPoolSize) {
        ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
        return makeEngineFromAppXML(engineType, appXML, threadPoolSize);
    }

    public static IDiagnosisEngine<Constraint> makeEngineFromAppXML(EngineType engineType, ExquisiteAppXML appXML,
                                                                    int threadPoolSize) {
        ExcelExquisiteSession<Constraint> sessionData = new ExcelExquisiteSession<>(appXML);

        // Prepare all the internal things to load the xml file
        ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
        Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
        VariablesFactory varFactory = new VariablesFactory(variablesMap);
        DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
        modelLoader.loadDiagnosisModelFromXML();

        // Use a default strategy
        sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;

        return makeEngine(engineType, sessionData, threadPoolSize);
    }

    /**
     * Provides an option to specify the size of the thread pool to be available
     * to a threaded tests.diagnosis engine. Note, not all engine implementations use
     * parallelism.
     *
     * @param engineType
     * @param sessionData
     * @param threadPoolSize
     * @return
     */
    public static <T> IDiagnosisEngine<T> makeEngine(EngineType engineType,
                                                     ExcelExquisiteSession<T> sessionData, int threadPoolSize) {
        switch (engineType) {
            case HSDagStandardQX:
                return makeDAGEngineStandardQx(sessionData);
            case ParaHSDagStandardQX:
                return makeParaDagEngineStandardQx(sessionData, threadPoolSize);
            case FullParaHSDagStandardQX:
                return makeFullParaDagEngineStandardQx(sessionData, threadPoolSize);
            case HeuristicSearch:
                return new HeuristicDiagnosisEngine<>(sessionData, threadPoolSize);
            case Hybrid:
                return new HybridEngine<>(sessionData, threadPoolSize);
            case MiniZinc:
                return makeMZDiagnosisEngine(sessionData, threadPoolSize);
            case PRDFS:
                return new ParallelRandomDFSEngine<T>(sessionData, threadPoolSize);
            case InverseQuickXplain:
                return new InvQXDiagnosisEngine<>(sessionData);
            case MXPandInvQXP:
                return new MXPandInvQXP<>(sessionData);
            case SFL:
                return new SpectrumBasedDiagnosisEngine<>(sessionData);
            default:
                System.err.println("Unrecognized engine: " + engineType);
                return null;
        }
    }


    private static <T> IDiagnosisEngine<T> makeMZDiagnosisEngine(ExcelExquisiteSession sessionData, int threadPoolSize) {
        /*
        MZModelGenerator mz = new MZModelGenerator(sessionData);
        MZDiagnosisEngine mzDiagnosisEngine = new MZDiagnosisEngine(mz.getDiagnosisModel(), mz.getAbnormalsCount(), mz.getSearchType(), threadPoolSize);
        mzDiagnosisEngine.setDiagnosisModel(sessionData);
        return mzDiagnosisEngine;
        */
        throw new RuntimeException("Removed dependency on the expriments");
    }

    /**
     * Returns an instance of a heuristic depth-first search algorithm
     * * @return
     */
    public static <T> IDiagnosisEngine<T> makeHeuristicSearchEngine(
            ExcelExquisiteSession sessionData, int threads) {
        IDiagnosisEngine<T> engine = new HeuristicDiagnosisEngine<>(sessionData, threads);
        return engine;
    }

    /**
     * Returns an instance of HSDagEngine using the standard ConstraintsQuickXPlain
     * implementation.
     *
     * @return
     */
    public static <T> IDiagnosisEngine<T> makeDAGEngineStandardQx(
            ExcelExquisiteSession sessionData) {
        IDiagnosisEngine<T> engine = new HSDagEngine<>(sessionData);
        return engine;
    }

    /**
     * Returns an instance of ParallelHSDagEngine using standard QX
     * implementation.
     *
     * @return
     */
    public static <T> IDiagnosisEngine<T> makeParaDagEngineStandardQx(
            ExcelExquisiteSession sessionData, int threadPoolSize) {
        IDiagnosisEngine<T> engine = new ParallelHSDagEngine<>(sessionData, threadPoolSize);
        return engine;
    }

    /**
     * Returns an instance of the FUllParallelHSDagBuilder using standard QX
     * implementation.
     *
     * @return
     */
    public static <T> IDiagnosisEngine<T> makeFullParaDagEngineStandardQx(
            ExcelExquisiteSession sessionData, int threadPoolSize) {
        IDiagnosisEngine<T> engine = new FullParallelHSDagEngine<>(sessionData, threadPoolSize);
        return engine;
    }

}
