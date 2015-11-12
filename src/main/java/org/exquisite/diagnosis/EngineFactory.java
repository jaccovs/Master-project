package org.exquisite.diagnosis;

import java.util.Dictionary;
import java.util.Hashtable;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.FullParallelHSDagBuilder;
import org.exquisite.diagnosis.engines.HSDagBuilder;
import org.exquisite.diagnosis.engines.HeuristicDiagnosisEngine;
import org.exquisite.diagnosis.engines.HybridEngine;
import org.exquisite.diagnosis.engines.ParallelHSDagBuilder;
import org.exquisite.diagnosis.engines.ParallelRandomDFSEngine;
import org.exquisite.diagnosis.engines.SpectrumBasedDiagnosisEngine;
import org.exquisite.diagnosis.invquickxplain.InvQXDiagnosisEngine;
import org.exquisite.diagnosis.invquickxplain.MXPandInvQXP;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;

import choco.kernel.model.variables.integer.IntegerExpressionVariable;
//import evaluations.dxc.synthetic.minizinc.MZDiagnosisEngine;
//import evaluations.dxc.synthetic.minizinc.MZModelGenerator;

/**
 * Just a helper class to build HSDagBuilder (or any other class that implements
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
	// ExquisiteSession sessionData) throws UnsupportedEngineException{
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
	 * @param engineType the type of the engine
	 * @param xmlFilePath the file to use
	 * @param threadPoolSize the threadpool size (in case of a parallel engine)
	 * @return a fresh engine
	 */
	public static IDiagnosisEngine makeEngineFromXMLFile(EngineType engineType,
			String xmlFilePath, int threadPoolSize) {
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
		return makeEngineFromAppXML(engineType, appXML, threadPoolSize);
	}
	
	public static IDiagnosisEngine makeEngineFromAppXML(EngineType engineType, ExquisiteAppXML appXML, int threadPoolSize) {
		ExquisiteSession sessionData = new ExquisiteSession(appXML);
		
		// Prepare all the internal things to load the xml file
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		VariablesFactory varFactory = new VariablesFactory(variablesMap);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
		modelLoader.loadDiagnosisModelFromXML();
		
		// Use a default strategy
		sessionData.config.searchStrategy = SearchStrategies.Default;
		
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
	public static IDiagnosisEngine makeEngine(EngineType engineType,
			ExquisiteSession sessionData, int threadPoolSize) {
		switch (engineType) {
		case HSDagStandardQX:
			return makeDAGEngineStandardQx(sessionData);
		case ParaHSDagStandardQX:
			return makeParaDagEngineStandardQx(sessionData, threadPoolSize);
		case FullParaHSDagStandardQX:
			return makeFullParaDagEngineStandardQx(sessionData, threadPoolSize);
		case HeuristicSearch:
			return new HeuristicDiagnosisEngine(sessionData,threadPoolSize);
		case Hybrid:
			return new HybridEngine(sessionData,threadPoolSize);
        case MiniZinc:
            return makeMZDiagnosisEngine(sessionData, threadPoolSize);
        case PRDFS:
        	return new ParallelRandomDFSEngine(sessionData, threadPoolSize);
		case InverseQuickXplain:
			return new InvQXDiagnosisEngine(sessionData);
		case MXPandInvQXP:
			return new MXPandInvQXP(sessionData);
		case SFL:
			return new SpectrumBasedDiagnosisEngine(sessionData);
		default:
			System.err.println("Unrecognized engine: " + engineType);
			return null;
		}
	}


	private static IDiagnosisEngine makeMZDiagnosisEngine(ExquisiteSession sessionData, int threadPoolSize) {
		/*
        MZModelGenerator mz = new MZModelGenerator(sessionData);
        MZDiagnosisEngine mzDiagnosisEngine = new MZDiagnosisEngine(mz.getModel(), mz.getAbnormalsCount(), mz.getSearchType(), threadPoolSize);
        mzDiagnosisEngine.setSessionData(sessionData);
        return mzDiagnosisEngine;
        */
        throw new RuntimeException("Removed dependency on the expriments");
    }

    /**
	 * Returns an instance of a heuristic depth-first search algorithm
	 * 	 * @return
	 */
	public static IDiagnosisEngine makeHeuristicSearchEngine(
			ExquisiteSession sessionData, int threads) {
		IDiagnosisEngine engine = new HeuristicDiagnosisEngine(sessionData, threads);
		return engine;
	}
	
	/**
	 * Returns an instance of HSDagBuilder using the standard QuickXPlain
	 * implementation.
	 * 
	 * @return
	 */
	public static IDiagnosisEngine makeDAGEngineStandardQx(
			ExquisiteSession sessionData) {
		IDiagnosisEngine engine = new HSDagBuilder(sessionData);
		return engine;
	}

	/**
	 * Returns an instance of ParallelHSDagBuilder using standard QX
	 * implementation.
	 * 
	 * @return
	 */
	public static IDiagnosisEngine makeParaDagEngineStandardQx(
			ExquisiteSession sessionData, int threadPoolSize) {
		IDiagnosisEngine engine = new ParallelHSDagBuilder(sessionData, threadPoolSize);
		return engine;
	}
	
	/**
	 * Returns an instance of the FUllParallelHSDagBuilder using standard QX
	 * implementation.
	 * 
	 * @return
	 */
	public static IDiagnosisEngine makeFullParaDagEngineStandardQx(
			ExquisiteSession sessionData, int threadPoolSize) {
		IDiagnosisEngine engine = new FullParallelHSDagBuilder(sessionData, threadPoolSize);
		return engine;
	}

}
