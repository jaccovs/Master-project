package org.exquisite.diagnosis.interactivity.partitioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import choco.kernel.model.constraints.Constraint;

/**
 * Kostyas BruteForce class.
 * 
 * @author Schmitz
 *
 */
public class BruteForce implements Partitioning {

	private static Logger logger = LoggerFactory.getLogger(BruteForce.class.getName());

	// private Searchable<Id> theory;

	protected IDiagnosisEngine diagnosisEngine;

	private Set<Diagnosis> hittingSets;

	private int partitionsCount = 0;

	private Scoring scoring = null;

	private ArrayList<Partition> partitions = new ArrayList<Partition>();

	private Partition bestPartition = null;

	private double threshold = 0.01d;

	public BruteForce(IDiagnosisEngine diagnosisEngine, Scoring function) {
		this.diagnosisEngine = diagnosisEngine;
		this.scoring = function;
		this.scoring.setPartitionSearcher(this);
	}

	protected void reset() {
		partitionsCount = 0;
		hittingSets = null;
		partitions = new ArrayList<Partition>();
		bestPartition = null;

	}

	protected String toString(Set<Diagnosis> hittingSets) {
		StringBuilder res = new StringBuilder();
		for (Diagnosis hittingSet : hittingSets) {
			res.append(hittingSet.toString()).append(" ");
		}
		return res.toString();
	}

	protected int numOfHittingSets;

	public int getNumOfHittingSets() {
		return numOfHittingSets;
	}

	public Partition generatePartition(Set<Diagnosis> hittingSets) throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

		numOfHittingSets = hittingSets.size();
		reset();
		Set<Diagnosis> hs = preprocess(hittingSets);

		// find the best partition
		Set<Diagnosis> desc = new LinkedHashSet<Diagnosis>(new TreeSet<Diagnosis>(hs).descendingSet());
		Partition partition = findPartition(desc, new LinkedHashSet<Diagnosis>());
		if (logger.isDebugEnabled())
			logger.debug("Searched through " + getPartitionsCount() + " partitionsCount");
		if (getScoring() != null) {
			partition = getScoring().runPostprocessor(getPartitions(), partition);
		}
		restoreEntailments(hittingSets);
		return partition;
	}

	public Partition nextPartition(Partition lastPartition) throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
		getPartitions().remove(lastPartition);
		Partition partition = getPartitions().get(0);
		if (!partition.isVerified)
			verifyPartition(partition);
		if (getScoring() != null)
			partition = getScoring().runPostprocessor(getPartitions(), partition);
		lastPartition = partition;
		return partition;
	}

	protected Set<Diagnosis> preprocess(Set<Diagnosis> hittingSets) { // throws SolverException {
//		ensureCapacity((int) Math.pow(2, hittingSets.size()));
		if (getScoringFunction() == null)
			throw new IllegalStateException("Scoring function is not set!");
		// save the original hitting sets

		setHittingSets(Collections.unmodifiableSet(hittingSets));
		// preprocessing
		Set<Diagnosis> hs = new LinkedHashSet<Diagnosis>(hittingSets);
		removeCommonEntailments(hs);
		for (Iterator<Diagnosis> hsi = hs.iterator(); hsi.hasNext();)
			if (hsi.next().getEntailments().isEmpty())
				hsi.remove();
		getScoringFunction().normalize(hs);

		return new TreeSet<Diagnosis>(hs);
	}

	protected void restoreEntailments(Set<Diagnosis> hittingSets) {
		for (Diagnosis hs : hittingSets)
			hs.restoreEntailments();
	}

	protected void removeCommonEntailments(Set<Diagnosis> hittingSets) { // throws SolverException {
		Set<Constraint> ent = getCommonEntailments(hittingSets);
		if (!ent.isEmpty())
			for (Diagnosis hs : hittingSets) {
				Set<Constraint> hse = new LinkedHashSet<Constraint>(hs.getEntailments());
				hse.removeAll(ent);
				hs.setEntailments(hse);
			}
	}

	protected void ensureCapacity(int cap) {
		this.partitions.ensureCapacity(cap);
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public boolean verifyPartition(Partition partition) throws DiagnosisException {
		Set<Constraint> ent = getCommonEntailments(partition.dx);
		partition.partition = Collections.unmodifiableSet(ent);
		if (logger.isDebugEnabled())
			logger.debug("Common entailments: " + partition.partition);
		if (ent == null || ent.isEmpty())
			return false;
		// partition the rest of diagnoses
		for (Diagnosis hs : getHittingSets()) {
			if (!partition.dx.contains(hs)) {
				if (hs.getEntailments().containsAll(ent))
					partition.dx.add(hs);
				else if (!diagnosisConsistent(hs, ent))
					partition.dnx.add(hs);
				else if (diagnosisEntails(hs, ent))
					partition.dx.add(hs);
				else
					partition.dz.add(hs);
			}
		}
		return true;
	}
	
	protected boolean diagnosisConsistent(Diagnosis diagnosis, Set<Constraint> entailments) throws DiagnosisException {
		QuickXPlain qx = new QuickXPlain(diagnosisEngine.getSessionData(), diagnosisEngine);
		
		List<Constraint> constraints = new ArrayList<Constraint>();
		DiagnosisModel model = diagnosisEngine.getModel();
		
		constraints.addAll(model.getPossiblyFaultyStatements());
		constraints.removeAll(diagnosis.getElements());
		
		constraints.addAll(model.getCorrectStatements());
		
		constraints.addAll(entailments);
		
		// TODO: We only use first testcase at the moment
		if (diagnosisEngine.getModel().getPositiveExamples().size() > 0) {
			constraints.addAll(diagnosisEngine.getModel().getPositiveExamples().get(0).constraints);
		}
		
		return qx.isConsistent(constraints);
	}
	
	protected boolean diagnosisEntails(Diagnosis diagnosis, Set<Constraint> entailments) throws DiagnosisException {
		QuickXPlain qx = new QuickXPlain(diagnosisEngine.getSessionData(), diagnosisEngine);
		
		List<Constraint> constraints = new ArrayList<Constraint>();
		DiagnosisModel model = diagnosisEngine.getModel();
		
		constraints.addAll(model.getPossiblyFaultyStatements());
		constraints.removeAll(diagnosis.getElements());
		
		constraints.addAll(model.getCorrectStatements());
		
		// TODO: We only use first testcase at the moment
		if (diagnosisEngine.getModel().getPositiveExamples().size() > 0) {
			constraints.addAll(diagnosisEngine.getModel().getPositiveExamples().get(0).constraints);
		}
		
		return qx.isEntailed(constraints, entailments);
	}

	public static Set<Constraint> getCommonEntailments(Set<Diagnosis> dx) { // throws SolverException {
		Set<Constraint> intersection = null;
		for (Diagnosis hs : dx) {
			if (intersection == null)
				intersection = new LinkedHashSet<Constraint>(hs.getEntailments());
			else
				intersection.retainAll(hs.getEntailments());
			if (intersection.isEmpty())
				return intersection;
		}
		return intersection;
	}

	protected Partition findPartition(Set<Diagnosis> hittingSets, Set<Diagnosis> head) throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

		// if (this.bestPartition != null && this.bestPartition.score < this.threshold)
		// return this.bestPartition;
		if ((hittingSets == null || hittingSets.isEmpty())) {
			if (head.isEmpty())
				return new Partition(diagnosisEngine.getModel());
			else {
				incPartitionsCount();
				Partition part = new Partition(diagnosisEngine.getModel());
				for (Diagnosis el : head)
					part.dx.add(el); // getOriginalHittingSet(el));
				if (logger.isDebugEnabled())
					logger.debug("Creating a partition with dx: " + head);
				if (verifyPartition(part)) {
					if (logger.isDebugEnabled())
						logger.debug("Created partition: \n dx:" + part.dx + "\n dnx:" + part.dnx + "\n dz:" + part.dz);
					if (getPartitions() != null && !getPartitions().contains(part)) {
						getPartitions().add(part);
					}
					return part;
				}
				return null;
			}
		}

		Set<Diagnosis> tail = new LinkedHashSet<Diagnosis>(hittingSets);
		Iterator<Diagnosis> ti = tail.iterator();
		Diagnosis hs = ti.next();
		ti.remove();

		if (logger.isDebugEnabled())
			logger.debug("Partitions: " + partitionsCount + " head: " + head.size() + " hsets:" + hittingSets.size());
		Partition part = findPartition(tail, head);

		head.add(hs);
		Partition partHead = null;

		// if (this.bestPartition == null || (getScoringFunction().getPartitionScore(part) <= this.bestPartition.score))
		partHead = findPartition(tail, head);

		head.remove(hs);

		Partition best = partHead;
		if (getScoringFunction().getScore(part).compareTo(getScoringFunction().getScore(partHead)) < 0) {
			best = part;
		}
		if (this.bestPartition == null || (best != null && this.bestPartition.score.compareTo(best.score) > 0))
			this.bestPartition = best;
		return best;
	}

	protected void incPartitionsCount() {
		this.partitionsCount++;
	}

	// protected Diagnosis getOriginalHittingSet(Diagnosis el) {
	// for (Diagnosis elem : getHittingSets()) {
	// if (el.compareTo(elem) == 0)
	// return elem;
	// }
	// return null;
	// }

	// public Searchable<Id> getTheory() {
	// return theory;
	// }

	public Scoring getScoringFunction() {
		return this.scoring;
	}

	public Set<Diagnosis> getHittingSets() {
		return hittingSets;
	}

	protected void setHittingSets(Set<Diagnosis> hittingSets) {
		this.hittingSets = hittingSets;
	}

	public int getPartitionsCount() {
		return partitionsCount;
	}

	public Scoring getScoring() {
		return scoring;
	}

	public List<Partition> getPartitions() {
		return partitions;
	}

}
