package org.exquisite.diagnosis.engines;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.datamodel.ExquisiteEnums.ExampleConstraintValueTypes;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.sfl.ObservationMatrix;
import org.exquisite.diagnosis.sfl.Ochiai;
import org.exquisite.diagnosis.sfl.SimilarityCoefficient;

import choco.kernel.model.constraints.Constraint;


public class SpectrumBasedDiagnosisEngine implements IDiagnosisEngine{
	
	/**
	 * Object that contains tests.diagnosis model, appXML and graph object instances for this session.
	 */
	private ExquisiteSession sessionData = null;
	
	private List<Diagnosis> diagnoses = null;
	
	protected long finishedTime = 0;
	
	private SimilarityCoefficient sc = null;
	
	private BigDecimal threshold = null;
	
	
	public BigDecimal getThreshold() {
		return threshold;
	}


	public void setThreshold(BigDecimal threshold) {
		this.threshold = threshold;
	}


	public SimilarityCoefficient getSc() {
		return sc;
	}


	public void setSc(SimilarityCoefficient sc) {
		this.sc = sc;
	}


	public SpectrumBasedDiagnosisEngine(ExquisiteSession sessionData, BigDecimal threshold) {
		setSessionData(sessionData);
		sc = SimilarityCoefficient.getDefaultSimilarityCoefficient();
		this.threshold = threshold;
	}
	
	public SpectrumBasedDiagnosisEngine(ExquisiteSession sessionData, BigDecimal threshold, SimilarityCoefficient sc) {
		setSessionData(sessionData);
		this.sc = sc;
		this.threshold = threshold;
	}
	
	public SpectrumBasedDiagnosisEngine(ExquisiteSession sessionData) {
		setSessionData(sessionData);
		sc = SimilarityCoefficient.getDefaultSimilarityCoefficient();
		this.threshold = new BigDecimal(sessionData.config.probabilityThreshold);
	}
	

	@Override
	public void resetEngine() {
		this.diagnoses=null;
	}

	@Override
	public ExquisiteSession getSessionData() {
		return this.sessionData;
	}

	
	@Override
	public void setSessionData(ExquisiteSession sessionData) {
		this.sessionData=sessionData;
		
	}

	@Override
	public DiagnosisModel getModel() {
		return sessionData.diagnosisModel;
	}

	@Override
	public List<Diagnosis> calculateDiagnoses() throws DiagnosisException {
		if(diagnoses!=null){
			return diagnoses;
		}
		
		diagnoses = new ArrayList<Diagnosis>();
		DiagnosisModel dm = sessionData.diagnosisModel;
		ExquisiteGraph<String> graph = dm.graph;
				
		List<Set<String>> negativeCones = new ArrayList<Set<String>>();
		List<Set<String>> positiveCones = new ArrayList<Set<String>>();
		
		for(Example example : dm.getPositiveExamples()){
			negativeCones.addAll(getCones(example.getConstraintsGrouped(ExampleConstraintValueTypes.ExpectedValue), graph));
			positiveCones.addAll(getCones(example.getConstraintsGrouped(ExampleConstraintValueTypes.CorrectValue), graph));			
		}
		 
		Set<String> range = graph.getVertex();
		Iterator<String> iterator = range.iterator();
		while(iterator.hasNext()){
			try {
				String cell = iterator.next();
				Set<String> predecessor = graph.getAllParents(cell);
				if(predecessor==null || predecessor.size()==0)
					iterator.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ObservationMatrix om = new ObservationMatrix(positiveCones, negativeCones, range);
		
		// Debugging output
		System.out.println(om.toString());
		
		SimilarityCoefficient sc = new Ochiai();
		
		Map<String, BigDecimal> coefficients = om.getCoefficientValues(sc);
		List<String> ranking = getRanking(coefficients, ObservationMatrix.asSortedList(coefficients.values()));
		for(String cell: ranking){
			if(coefficients.get(cell).compareTo(threshold)>0){
				// Debugging output
				System.out.println(cell+": "+coefficients.get(cell).doubleValue());
				List<Constraint> constraints = new ArrayList<Constraint>();
				constraints.add(dm.getConstraintByName(cell));
				diagnoses.add(new Diagnosis(constraints, dm, coefficients.get(cell)));
			}
		}
		
		
		finishedTime = System.nanoTime();	
		
		return diagnoses;
	}
	
	private List<String> getRanking(Map<String, BigDecimal> coefficients, List<BigDecimal> sorted){
		List<String> ranking = new ArrayList<String>();
		Map<BigDecimal,Set<String>> grouping = getCellsGroupedByCoefficients(coefficients);
		for(BigDecimal coefficient: sorted){
			Set<String> tie = grouping.get(coefficient);
			for(String cell:tie){
				ranking.add(0, cell);
			}
		}
		return ranking;
	}
	
	private Map<BigDecimal,Set<String>> getCellsGroupedByCoefficients(Map<String, BigDecimal> coefficients){
		 Map<BigDecimal,Set<String>> grouping = new HashMap<BigDecimal,Set<String>>();
		 for(String cell: coefficients.keySet()){
			 Set<String> tie = null;
			 BigDecimal coeff = coefficients.get(cell);
			 if(grouping.containsKey(coeff))
				 tie = grouping.get(coeff);
			 else
				 tie = new HashSet<String>();
			 tie.add(cell);
			 grouping.put(coeff, tie);
		 }
		 return grouping;
	}
	
	private List<Set<String>> getCones(Map<String, Constraint> cells, ExquisiteGraph<String> graph){
		List<Set<String>> list = new ArrayList<Set<String>>();
		if(cells==null)
			return list;
		for(String key:cells.keySet()){
			list.add(getCone(key,graph));
		}
		return list;
	}
	
	
	private Set<String> getCone(String cell, ExquisiteGraph<String> graph){
		Set<String> cone = new HashSet<String>();
		cone.add(cell);
		try {
			for(String parent : graph.getAllParents(cell)){
				if(!cone.contains(parent)){
					cone.addAll(getCone(parent,graph));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cone;
	}

	@Override
	public int getSolverCalls() {
		// There a no solver calls for SFL!
		return 0;
	}

	@Override
	public void incrementSolverCalls() {
		// There are no solver calls for SFL! 
		
	}

	@Override
	public long getSolverTime() {
		// There are no solver calls for SFL!
		return 0;
	}

	@Override
	public void incrementSolverTime(long time) {
		// There are no solver calls for SFL!	
	}

	@Override
	public int getCspSolvedCount() {
		return 0;
	}

	@Override
	public void incrementCSPSolutionCount() {		
	}

	@Override
	public int getPropagationCount() {
		return 0;
	}

	@Override
	public void incrementPropagationCount() {		
	}

	@Override
	public int getTPCalls() {
		return 0;
	}

	@Override
	public void incrementQXPCalls() {
	}

	@Override
	public int getSearchesForConflicts() {
		return 0;
	}

	@Override
	public void incrementSearchesForConflicts() {
		
	}

	@Override
	public int getMXPConflicts() {
		return 0;
	}

	@Override
	public void incrementMXPConflicts(int conflicts) {
	}

	@Override
	public int getMXPSplittingTechniqueConflicts() {
		return 0;
	}

	@Override
	public void incrementMXPSplittingTechniqueConflicts(int conflicts) {
	}

	@Override
	public long getFinishedTime() {
		return finishedTime;
	}

}
