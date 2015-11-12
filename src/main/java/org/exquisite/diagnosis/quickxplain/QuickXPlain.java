package org.exquisite.diagnosis.quickxplain;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3.Choco2ToChoco3Solver;
import org.exquisite.diagnosis.quickxplain.ontologies.OntologySolver;
import org.exquisite.diagnosis.quickxplain.parallelqx.ConstraintListener;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;

/**
 * A method that implements the QuickExplain algorithm for Choco constraints
 * 
 * @author Dietmar
 * 
 */
public class QuickXPlain {

	public enum SolverType {
		Choco2, Choco3, OWLAPI
	}

	// a termporary switch to test things
	// public static boolean CHOCO3 = false;
	public static SolverType SOLVERTYPE = SolverType.Choco2;

	public static boolean CONTINUE_AFTER_FIRST_CONFLICT = false;
	public static int maxThreadPoolSize = 4;
	public static Object ContinuingSync = new Object();
	private static ExecutorService threadPool = Executors.newFixedThreadPool(maxThreadPoolSize);
	public static int runningThreads = 0;
	public static Object runningThreadsSync = new Object();

	public boolean finished = false;
	public boolean cancelled = false;
	private Thread currentThread = null;

	// A handle to the DAG Builder
	public IDiagnosisEngine diagnosisEngine = null;

	public static int reuseCount = 0;

	// Some artificial wait time in ms to simulate longer solve tasks: Should by
	// -1
	public static int ARTIFICIAL_WAIT_TIME = -1;

	// Only for a test
	public static boolean PRINT_SOLUTION_TIME = false;

	/**
	 * The model used during conflict search.
	 */
	public DiagnosisModel currentDiagnosisModel;

	/**
	 * Session data, contains appXML, graph and complete Diagnosis model.
	 */
	protected ExquisiteSession sessionData;

	// For parallel access..
	public ConstraintListener constraintListener;

	/**
	 * Remember the current example being explored
	 */
	public Example currentExample;

	/**
	 * set currentDiagnosisModel as a copy of SessionData tests.diagnosis model.
	 * 
	 * @param sessionData
	 */
	public QuickXPlain(ExquisiteSession sessionData, IDiagnosisEngine diagnosisEngine) {
		this.diagnosisEngine = diagnosisEngine;
		this.sessionData = sessionData;
		this.currentDiagnosisModel = new DiagnosisModel(this.sessionData.diagnosisModel);
	}

	/**
	 * Sets the tests.diagnosis model.
	 * 
	 * @param model
	 */
	public void setDiagnosisModel(DiagnosisModel model) {
		this.currentDiagnosisModel = model;
	}

	/**
	 * Check consistency of constraints that constitute the test case.
	 * 
	 * @return
	 * @throws DomainSizeException
	 */
	public boolean checkCorrectStatements() throws DomainSizeException {
		List<Constraint> correctStatements = new ArrayList<Constraint>(this.currentDiagnosisModel.getCorrectStatements());
		try {
			return isConsistent(correctStatements);
		} catch (DomainSizeException e) {
			throw e;
		}
	}

	// public static createSolverFromCurrent

	/**
	 * A method that determines if a given set of constraints is consistent
	 * 
	 * @param constraints
	 *            the set of constraints to be checked
	 * @return true, if there is no conflict, false otherwise
	 * @throws DomainSizeException
	 */
	public boolean isConsistent(List<Constraint> constraints) throws DomainSizeException {

		// Simulate some more computation time
		long start = System.nanoTime();
		if (ARTIFICIAL_WAIT_TIME > 0) {
			while ((System.nanoTime() - start) / 1000000 < ARTIFICIAL_WAIT_TIME) {
				// do nothing..
			}
		}

		ISolver solver = createSolver();

		solver.createModel(this, constraints);

		// Call solve()
		try {
			// / Try to reuse things first

			// Otherwise, solve

			// System.out.println("Start solve..");
			boolean isFeasible = false;

			isFeasible = solver.isFeasible(this.diagnosisEngine);

			long time = System.nanoTime() - start;
			if (diagnosisEngine != null) {
				diagnosisEngine.incrementSolverTime(time);
			}
			// if (PRINT_SOLUTION_TIME) {
			// System.out.println("Solve needed " + time + " ms.");
			// }

			if (this.diagnosisEngine != null && isFeasible) {
				this.diagnosisEngine.incrementCSPSolutionCount();
			}
			// System.out.println("QX solution: " + isFeasible);

			return isFeasible;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception here, " + e.getMessage());
			return false;
		}
		// return result;
	}

	public boolean isEntailed(List<Constraint> constraints, Set<Constraint> entailments) {
		// Simulate some more computation time
		long start = System.nanoTime();
		if (ARTIFICIAL_WAIT_TIME > 0) {
			while ((System.nanoTime() - start) / 1000000 < ARTIFICIAL_WAIT_TIME) {
				// do nothing..
			}
		}

		ISolver solver = createSolver();

		solver.createModel(this, constraints);

		// Call solve()
		try {
			// / Try to reuse things first

			// Otherwise, solve

			// System.out.println("Start solve..");
			boolean isFeasible = false;

			isFeasible = solver.isEntailed(this.diagnosisEngine, entailments);

			long time = System.nanoTime() - start;
			if (diagnosisEngine != null) {
				diagnosisEngine.incrementSolverTime(time);
			}
			// if (PRINT_SOLUTION_TIME) {
			// System.out.println("Solve needed " + time + " ms.");
			// }

			if (this.diagnosisEngine != null && isFeasible) {
				this.diagnosisEngine.incrementCSPSolutionCount();
			}
			// System.out.println("QX solution: " + isFeasible);

			return isFeasible;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception here, " + e.getMessage());
			return false;
		}
		// return result;
	}
	
	public Set<Constraint> calculateEntailments(List<Constraint> constraints) {
		ISolver solver = createSolver();

		solver.createModel(this, constraints);
		
		Set<Constraint> entailments = solver.calculateEntailments();
		
		return entailments;
	}

	private ISolver createSolver() {
		ISolver solver = null;
		// -------------------------------------------------------------------
		switch (SOLVERTYPE) {
		case Choco2:
			solver = new Choco2Solver();
			break;
		case Choco3:
			solver = new Choco2ToChoco3Solver();
			break;
		case OWLAPI:
			solver = new OntologySolver();
			break;
		}
		return solver;
	}

	/**
	 * A method that returns a unique code for a set of conflicts
	 * 
	 * @param conflict
	 *            the list of conflicts
	 * @return a code
	 */
	private String createStringRepOfConflict(List<Constraint> conflict) {
		// System.out.println("Creating a conflict code: ");
		StringBuffer result = new StringBuffer();
		List<String> ct = new ArrayList<String>();
		for (Constraint c : conflict) {
			ct.add("" + c.hashCode());
		}
		Collections.sort(ct);
		for (String name : ct) {
			result.append(name);
		}
		return result.toString();
	}

	/**
	 * Checks if the set of correct and potentially faulty statements is consistent
	 * 
	 * @return true if consistent, otherwise return false
	 * @throws DomainSizeException
	 */
	public boolean checkConsistency() throws DomainSizeException {
		List<Constraint> allStatements = new ArrayList<Constraint>(this.currentDiagnosisModel.getCorrectStatements());
		allStatements.addAll(new ArrayList<Constraint>(this.currentDiagnosisModel.getPossiblyFaultyStatements()));

		try {
			return isConsistent(allStatements);
		} catch (DomainSizeException e) {
			throw e;
		}
	}

	/**
	 * The main quickxplain method. It uses the background from the constraint model is called otherwise only checkConsistency is called.
	 * 
	 * @return a conflict or an empty set if no conflict was found.
	 * @throws DomainSizeException
	 * 
	 */
	public List<Constraint> findConflict() throws DomainSizeException {
		List<Constraint> result = new ArrayList<Constraint>();

		try {
			if (checkConsistency() == true) {
				Debug.msg("checkConsistency = true.");
				return result;
			} else {
				Debug.msg("checkConsistency = false.");
				if (this.currentDiagnosisModel.getPossiblyFaultyStatements().size() == 0) {
					Debug.msg("No constraints to be analyzed");
					return result;
				}

				// // DJ Remember the constraints
				// this.allConstraints.clear();
				// this.allConstraints.addAll(this.currentDiagnosisModel.getCorrectStatements());
				// this.allConstraints.addAll(this.currentDiagnosisModel.getPossiblyFaultyStatements());
				//
				// System.out.println("all constraints: " + this.allConstraints.size());

				result = qxplain(this.currentDiagnosisModel.getCorrectStatements(), this.currentDiagnosisModel.getCorrectStatements(),
						this.currentDiagnosisModel.getPossiblyFaultyStatements(), 0);
			}
			Debug.msg("Returning result of find conflict of size: " + result.size());
			return result;
		} catch (DomainSizeException e) {
			throw e;
		}
	}

	/**
	 * A variant of the API which returns multiple conflicts at a time In the default implementation, only one conflict is returned.
	 * 
	 * @return A list of conflicts. Only one in the case of the single-threaded version
	 */
	public List<List<Constraint>> findConflicts() throws DomainSizeException {
		List<List<Constraint>> result = new ArrayList<List<Constraint>>();

		List<Constraint> conflict = findConflict();
		if (conflict.size() > 0) {
			result.add(new ArrayList<Constraint>(conflict));
		}
		// Release this guy
		if (this.constraintListener != null) {
			// System.out.println("Done with QX - release the listener");
			this.constraintListener.release();
		}

		if (diagnosisEngine != null) {
			diagnosisEngine.incrementSearchesForConflicts();
		}

		return result;

	}

	public void findConflictsParallel(final ConflictCheckingResult result, final SharedCollection<List<Constraint>> knownConflicts)
			throws DomainSizeException {

		List<Constraint> conflict = findConflict();
		if (conflict.size() > 0) {
			knownConflicts.addItemListNoDups(conflict);
			result.addConflict(conflict);
		}

		// Release this guy
		if (this.constraintListener != null) {
			// System.out.println("Done with QX - release the listener");
			this.constraintListener.release();
		}

		if (diagnosisEngine != null) {
			diagnosisEngine.incrementSearchesForConflicts();
		}

		synchronized (QuickXPlain.ContinuingSync) {
			finished = true;
			QuickXPlain.ContinuingSync.notifyAll();
		}
	}

	/**
	 * Computes a conflict for a given set of examples
	 * 
	 * @param examples
	 *            the set of examples to check (all at the beginning)
	 * @param constraintsToIgnore
	 *            (any constraints to ignore)
	 * @param createConflicts
	 *            (should conflicts be created or just consistency be tested)
	 * @return a conflict checking result object containing the list of found conflicts or null, if the current thread was interrupted
	 * @throws DomainSizeException
	 */
	public ConflictCheckingResult checkExamples(List<Example> examples, List<Constraint> constraintsToIgnore, boolean createConflicts)
			throws DomainSizeException {

		// System.out.println("Checking examples.");
		// System.out.println("qx examples: " + examples);
		// System.out.println("ignore: " + constraintsToIgnore);
		// System.out.println("create conflicts: " + createConflicts);

		// long start = System.currentTimeMillis();

		if (diagnosisEngine != null) {
			diagnosisEngine.incrementQXPCalls();
		}
		// Debug.msg("    Checking " + examples.size() +
		// " examples ignoring the following " +
		// Utilities.printConstraintList(constraintsToIgnore,
		// this.sessionData.diagnosisModel));
		ConflictCheckingResult result = new ConflictCheckingResult();
		// Remember one detected conflict
		List<Constraint> detectedConflict = new ArrayList<Constraint>();
		// System.out.println(examples.size());
		// go through all the positive examples
		for (Example example : examples) {
			// clean up stuff
			// this.dagBuilder.resetCachedConflicts();
			this.currentExample = example;

			// Check if the thread HSDAG builder is running in has been
			// interrupted, eg if
			// tests.diagnosis calculation has been cancelled by the user.
			if (Thread.currentThread().isInterrupted()) {
				return null;
			}

			// Make a copy of the tests.diagnosis model
			// add the example to the set of correct constraints
			// and remove constraints to ignore and any
			// constraints that are irrelevant (i.e. independent).
			DiagnosisModel copiedModel = new DiagnosisModel(sessionData.diagnosisModel);

			for (Constraint constraint : example.constraints) {
				copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));

				// TS: We dont need this anymore
				// OLDCHOCO3
				// if (CHOCO3) {
				// // copy the formula info
				// FormulaInfo formulaInfo = example.choco3FormulaInfos.get(constraint);
				// if (formulaInfo == null) {
				// System.err.println("FormulaInfo is null");
				// }
				// copiedModel.getFormulaInfoOfConstraints().put(constraint, formulaInfo);
				// }
			}
			copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>(example.irrelevantConstraints.values()));
			copiedModel.removeConstraintsToIgnore(constraintsToIgnore);

			// set qx with the new model copy.
			this.currentDiagnosisModel = copiedModel;

			// do the conflict search for this particular positive example...
			try {
				// Debug.msg("createConflicts = " + createConflsicts);
				if (createConflicts) {

					// if a conflict has already been detected, then only check
					// for consistency.
					boolean alreadyOneConflictFound = detectedConflict.size() > 0;
					// Debug.msg("alreadyOneConflictFound = " +
					// alreadyOneConflictFound);
					if (!alreadyOneConflictFound) {
						// Try to find a conflict through the example
						Debug.msg("qx.findConflict() start call...");

						// long start = System.currentTimeMillis();
						List<List<Constraint>> conflicts = this.findConflicts();
						// List<Constraint> conflict = this.findConflict();
						// long end = System.currentTimeMillis();
						// if (QuickXPlain.PRINT_SOLUTION_TIME
						// && conflict.size() > 0) {
						// System.out.println("Conflict detection time: "
						// + (end - start) + ", size of conflict: "
						// + conflict.size());
						//
						// }

						// Debug.msg("qx.findConflict() end of call...");
						// We got one
						// Debug.msg("conflict.size returned from qx = " +
						// conflict.size());
						if (conflicts.size() > 0) {

							for (List<Constraint> conflict : conflicts) {
								if (conflict.size() > 0) {
									// Remember it
									detectedConflict = conflict;
									Debug.msg("    Found conflicts: " + Utilities.printConstraintList(conflict, copiedModel) + "\n");
									List<Constraint> conflictSet = new ArrayList<Constraint>();
									// System.out.println("Adding a new conflict " + conflict);
									conflictSet.addAll(conflict);
									result.addConflict(conflictSet);
									// Remember the failed example
									if (!result.failedExamples.contains(example)) {
										result.failedExamples.add(example);
									}

								}
							}

						} else { // Debugging only
							// Debug.msg("    No conflict found for example\n");
						}

					} else {
						// Otherwise (we already have one conflict, just check
						// if the example go through
						// Debug.msg("   Checking consistency of other example (we already have a conflict...)");
						boolean consistent = this.checkConsistency();
						if (!consistent) {
							// If we can't find a solution, we mark the example
							// as failed without
							// looking for a further conflict.
							result.failedExamples.add(example);
						}
					}

				} //
				else { // only check the consistency of the example
				// Debug.msg("Checking example (at last level) - Only checking consistency");
					boolean consistent = this.checkConsistency();
					if (!consistent) {
						// If we can't find a solution, we mark the example as
						// failed without
						// looking for a further conflict.
						result.failedExamples.add(example);
					}
				}
			}
			// Catch and re-throw the exception
			catch (DomainSizeException e) {
				System.err.println("Problem with the domain size: " + e.getMessage());
				throw e;
			}
		}
		// System.out.println("done with example check, nb conflicts: " + result.conflicts.size());

		// long end = System.currentTimeMillis();
		// System.out.println("QXP took " + (end - start));
		return result;
	}

	public void checkExamplesParallel(final List<Example> examples, final List<Constraint> constraintsToIgnore, final boolean createConflicts,
			final ConflictCheckingResult result, final SharedCollection<List<Constraint>> knownConflicts) {
		finished = false;
		currentThread = new Thread() {
			public void run() {
				synchronized (runningThreadsSync) {
					runningThreads++;
				}

				try {

					// System.out.println("qx examples: " + examples);
					// System.out.println("ignore: " + constraintsToIgnore);
					// System.out.println("create conflicts: " + createConflicts);

					// long start = System.currentTimeMillis();

					if (diagnosisEngine != null) {
						diagnosisEngine.incrementQXPCalls();
					}
					// Debug.msg("    Checking " + examples.size() +
					// " examples ignoring the following " +
					// Utilities.printConstraintList(constraintsToIgnore,
					// this.sessionData.diagnosisModel));
					// ConflictCheckingResult result = new ConflictCheckingResult();
					// Remember one detected conflict
					List<Constraint> detectedConflict = new ArrayList<Constraint>();
					// System.out.println(examples.size());
					// go through all the positive examples
					for (Example example : examples) {
						// clean up stuff
						// this.dagBuilder.resetCachedConflicts();
						currentExample = example;

						// Check if the thread HSDAG builder is running in has been
						// interrupted, eg if
						// tests.diagnosis calculation has been cancelled by the user.
						if (Thread.currentThread().isInterrupted()) {
							return;
						}

						// Make a copy of the tests.diagnosis model
						// add the example to the set of correct constraints
						// and remove constraints to ignore and any
						// constraints that are irrelevant (i.e. independent).
						DiagnosisModel copiedModel = new DiagnosisModel(sessionData.diagnosisModel);

						for (Constraint constraint : example.constraints) {
							copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));

							// TS: We dont need this anymore
							// OLDCHOCO3
							// if (CHOCO3) {
							// // copy the formula info
							// FormulaInfo formulaInfo = example.choco3FormulaInfos.get(constraint);
							// if (formulaInfo == null) {
							// System.err.println("FormulaInfo is null");
							// }
							// copiedModel.getFormulaInfoOfConstraints().put(constraint, formulaInfo);
							// }
						}
						copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>(example.irrelevantConstraints.values()));
						copiedModel.removeConstraintsToIgnore(constraintsToIgnore);

						// set qx with the new model copy.
						currentDiagnosisModel = copiedModel;

						// do the conflict search for this particular positive example...
						try {
							// Debug.msg("createConflicts = " + createConflsicts);
							if (createConflicts) {

								// if a conflict has already been detected, then only check
								// for consistency.
								boolean alreadyOneConflictFound = result.conflictFound();
								// Debug.msg("alreadyOneConflictFound = " +
								// alreadyOneConflictFound);
								if (!alreadyOneConflictFound) {
									// Try to find a conflict through the example
									Debug.msg("qx.findConflict() start call...");

									// long start = System.currentTimeMillis();
									findConflictsParallel(result, knownConflicts);
									// List<Constraint> conflict = this.findConflict();
									// long end = System.currentTimeMillis();
									// if (QuickXPlain.PRINT_SOLUTION_TIME
									// && conflict.size() > 0) {
									// System.out.println("Conflict detection time: "
									// + (end - start) + ", size of conflict: "
									// + conflict.size());
									//
									// }

									// Debug.msg("qx.findConflict() end of call...");
									// We got one
									// Debug.msg("conflict.size returned from qx = " +
									// conflict.size());
									/*
									 * if (conflicts.size() > 0) {
									 * 
									 * for (List<Constraint> conflict : conflicts) { if (conflict.size() > 0) { // Remember it detectedConflict =
									 * conflict; Debug.msg("    Found conflicts: " + Utilities.printConstraintList(conflict, copiedModel) + "\n");
									 * List<Constraint> conflictSet = new ArrayList<Constraint>(); // System.out.println("Adding a new conflict " +
									 * conflict); conflictSet.addAll(conflict); result.addConflict(conflictSet); // Remember the failed example if
									 * (!result.failedExamples.contains(example)) { result.failedExamples.add(example); }
									 * 
									 * } }
									 * 
									 * 
									 * } else { // Debugging only // Debug.msg("    No conflict found for example\n"); }
									 */

								}/*
								 * else { // Otherwise (we already have one conflict, just check // if the example go through //
								 * Debug.msg("   Checking consistency of other example (we already have a conflict...)"); boolean consistent =
								 * checkConsistency(); if (!consistent) { // If we can't find a solution, we mark the example // as failed without //
								 * looking for a further conflict. result.failedExamples.add(example); } }
								 */

							} //
							else { // only check the consistency of the example
								// Debug.msg("Checking example (at last level) - Only checking consistency");
								boolean consistent = checkConsistency();
								if (!consistent) {
									// If we can't find a solution, we mark the example as
									// failed without
									// looking for a further conflict.
									result.failedExamples.add(example);
								}
							}
						}
						// Catch and re-throw the exception
						catch (DomainSizeException e) {
							System.err.println("Problem with the domain size: " + e.getMessage());
							return;
						}
					}
					// System.out.println("done with example check, nb conflicts: " + result.conflicts.size());

					// long end = System.currentTimeMillis();
					// System.out.println("QXP took " + (end - start));

				} finally {
					synchronized (QuickXPlain.ContinuingSync) {
						finished = true;

						// If no conflict was found, parent thread has to be awoken
						if (!createConflicts || !result.conflictFound()) {
							// Debug.syncMsg("MXP: No result found. Returning.");
							synchronized (QuickXPlain.ContinuingSync) {
								QuickXPlain.ContinuingSync.notifyAll();
							}
						}

						// QuickXPlain.ContinuingSync.notifyAll();
					}
					synchronized (runningThreadsSync) {
						runningThreads--;
					}
				}
			}
		};

		threadPool.execute(currentThread);
	}

	public void cancel() {
		if (currentThread != null) {
			// Debug.syncMsg("Found conflict to reuse. Stopping MergeXplain.");
			finished = true;
			cancelled = true;
			currentThread.interrupt();
		}
	}

	public static void restartThreadpool() {
		threadPool.shutdownNow();
		try {
			threadPool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

		}
		if (!threadPool.isTerminated()) {
			System.out.println("QuickXplain threadpool could not be shut down.");
		}
		threadPool = Executors.newFixedThreadPool(maxThreadPoolSize);
	}

	/**
	 * The method determines a split position for a given list's length. The strategy to split given some split points is set in the class
	 * 
	 * @param sp
	 *            the list
	 * @return the element in the middle (split position)
	 */
	public int split(List<Constraint> constraints) {
		// System.out.println("-- Split called: " +
		// Utilities.printConstraintList(constraints, model));

		// of there are only two elements, use the half of the elements
		if (constraints.size() == 2) {
			return 1;
		}
		return Math.round(constraints.size() / 2);
	}

	/**
	 * The recursive quickxplain method
	 * 
	 * @param background
	 * @param constraints
	 * @return a minimal conflict
	 * 
	 *         TODO: use preferences
	 * @throws DomainSizeException
	 */
	List<Constraint> qxplain(List<Constraint> background, List<Constraint> delta, List<Constraint> constraints, int level) throws DomainSizeException {

		boolean debug = Debug.QX_DEBUGGING;

		String indent = "";
		for (int i = 0; i < level; i++) {
			indent += "  ";
		}
		if (debug) {
			Debug.msg(indent + "Called at level:" + level);
			Debug.msg(indent + "bg:    " + Utilities.printConstraintList(background, this.currentDiagnosisModel));
			Debug.msg(indent + "delta: " + Utilities.printConstraintList(delta, this.currentDiagnosisModel));
			Debug.msg(indent + "cts:   " + Utilities.printConstraintList(constraints, this.currentDiagnosisModel));
		}

		// line 4
		boolean backgroundIsConsistent;
		try {
			backgroundIsConsistent = isConsistent(background);

			if (debug) {
				Debug.msg(indent + "Background consistency: " + backgroundIsConsistent);
			}

			if (delta.size() != 0 && !backgroundIsConsistent) {
				// System.err.println("FATAL in QuickXPlain: empty list in line 4: The background is inconsistent.");
				// Debug.msg("delta.size() = " + delta.size() +
				// "backgroundIsConsistent = " + backgroundIsConsistent);
				// System.exit(1);
				// TODO: Something wrong here. Was dead code.
				if (debug) {
					Debug.msg(indent + "Delta and inconsistent background");
				}
				return new ArrayList<Constraint>();
			}
			// line 5
			if (constraints.size() == 1) {
				// DJ: anyone listening?
				if (this.constraintListener != null && !this.constraintListener.hasConstraints() && !this.constraintListener.isReleased()) {
					this.constraintListener.setFoundConstraint(constraints.get(0));
					// System.out.println("Ok, the found constraint is: " + constraints.get(0));
				} else {
					// System.out.println("---> No listener here today?");
				}
				if (debug) {
					Debug.msg(indent + "Last constraint.. ");
				}
				return new ArrayList<Constraint>(constraints);
			}
			int split = this.split(constraints);

			List<Constraint> c1 = new ArrayList<Constraint>(constraints.subList(0, split));
			List<Constraint> c2 = new ArrayList<Constraint>(constraints.subList(split, constraints.size()));
			if (debug) {
				Debug.msg(indent + "c1: " + Utilities.printConstraintList(c1, this.currentDiagnosisModel));
				Debug.msg(indent + "c2: " + Utilities.printConstraintList(c2, this.currentDiagnosisModel));
			}

			// create the new background and add all of c1
			List<Constraint> b1 = new ArrayList<Constraint>(background);
			b1.addAll(c1);

			List<Constraint> delta2 = qxplain(b1, new ArrayList<Constraint>(c1), new ArrayList<Constraint>(c2), level + 1);
			if (debug) {
				Debug.msg(indent + "d2: " + Utilities.printConstraintList(delta2, this.currentDiagnosisModel));
			}

			// create lists for second phase
			List<Constraint> b2 = new ArrayList<Constraint>(background);
			b2.addAll(delta2);

			List<Constraint> delta1 = qxplain(b2, delta2, c1, level + 1);

			List<Constraint> result = new ArrayList<Constraint>(delta1);
			result.addAll(delta2);
			if (debug) {
				Debug.msg(indent + "result: " + Utilities.printConstraintList(result, this.currentDiagnosisModel));
			}
			return result;
		} catch (DomainSizeException e) {
			throw e;
		}
	}

	/**
	 * Sorts the given constraints by calculating constraint arities and grouping neighboring constraints
	 * 
	 * @param splitPoints
	 *            an empty list which is filled by split points
	 * @param givenConstraints
	 *            the given constraints
	 * @param inputs
	 *            the list of input variables of the problem.
	 * @param model
	 *            the tests.diagnosis model
	 * @return a sorted list
	 */
	public static List<Constraint> sortConstraintsByArityAndCalculuateSplittingPoints(DiagnosisModel model, Set<String> inputs,
			List<Constraint> splitPoints) {
		Map<String, Set<String>> varsOfConstraints = new HashMap<String, Set<String>>();
		Map<String, Integer> constraintArities = new HashMap<String, Integer>();

		// Get a reverse map
		Map<Constraint, String> ctNames = model.getConstraintNames();
		Map<String, Constraint> ctsByName = Utilities.reverse(ctNames);

		Set<String> constraintNames = new HashSet<String>();
		// Constraint ct;
		// Iterator<Constraint> ct_it = cpmodel.getConstraintIterator();
		// while (ct_it.hasNext()) {
		// ct = ct_it.next();

		for (Constraint ct : model.getPossiblyFaultyStatements()) {
			String name = model.getConstraintName(ct);
			constraintNames.add(name);
			// System.out.println("Constraint " + name);
			Set<Variable> allVarNamesOfConstraint = Utilities.getAllVariablesOfConstraint(ct);
			// System.out.println("Got the following variables: " + vars);
			Set<String> relevantVarNamesOfConstraint = new HashSet<String>();
			for (Variable var : allVarNamesOfConstraint) {
				if (!var.getName().equals(name) && !inputs.contains(var.getName())) {
					relevantVarNamesOfConstraint.add(var.getName());
				}
			}
			varsOfConstraints.put(name, relevantVarNamesOfConstraint);
			// System.out.println("Vars of " + name);
			// System.out.println(relevantVarNamesOfConstraint);
			constraintArities.put(name, relevantVarNamesOfConstraint.size());
		}

		// System.out.println("Arities of constraints: ");
		// Let's sort them according to their arity
		constraintArities = Utilities.sortByValueDescending(constraintArities);
		// System.out.println(constraintArities);
		// Here's the list of constraints to group
		// Allocate them in a list
		List<String> sortedConstraints = new ArrayList<String>();
		// System.out.println("Got " + constraintNames.size() + " constraints");

		// Remember where we created new entries
		while (constraintNames.size() > 0) {
			// get the first of the list
			List<String> constraintKeys = new ArrayList<String>(constraintArities.keySet());
			String first = constraintKeys.get(0);
			// System.out.println("First: " + first);
			// put it in the list
			if (!sortedConstraints.contains(first)) {
				// System.out.println("Adding first");
				sortedConstraints.add(first);
			}

			// remove it from the the map
			constraintArities.remove(first);
			// Remove the name of the list to be worked on
			constraintNames.remove(first);
			// Get the constraints of the first
			Set<String> constraintsOfFirst = varsOfConstraints.get(first);
			// Put them all into the list, remove the names to be worked on and
			// reduce the counter for the rest in the
			// arities map.
			for (String c : constraintsOfFirst) {
				if (!sortedConstraints.contains(c)) {
					sortedConstraints.add(c);
				}
				constraintNames.remove(c);
				for (String key : constraintArities.keySet()) {
					Set<String> theVars = varsOfConstraints.get(key);
					// System.out.println("Have to check vars of first: " + key
					// + ": "+ theVars);
					if (theVars.contains(key)) {
						// System.out.println("Found var, have to reduce the aritiy of "
						// + key);
						Integer count = constraintArities.get(key);
						if (count != null && count > 0) {
							constraintArities.put(key, count - 1);
						} else {
							constraintArities.remove(key);
						}
					}
				}
			}
			// Remember where we added a new constraint
			// System.out.println("Split point after some elements at " +
			// (sortedConstraints.size()-1));
			String splitname = sortedConstraints.get(sortedConstraints.size() - 1);
			// System.out.println("Constraint is " + splitname);
			if (!splitPoints.contains(ctsByName.get(splitname))) {
				splitPoints.add(ctsByName.get(splitname));
			}
			// System.out.println("Current list after iteration: " +
			// sortedConstraints + " (" + sortedConstraints.size() +
			// " elements)");
			constraintArities = Utilities.sortByValueDescending(constraintArities);
			// System.out.println("Current arities: " + constraintArities);
		}
		// System.out.println("Sorted list of size: " + sortedConstraints.size()
		// + "\n" + sortedConstraints);
		// System.out.println("\nSplit points: " +
		// Utilities.printConstraintList(splitPoints, model));

		// Get the list of possibly faulty constraints
		List<Constraint> orderedPossiblyFaultyConstraints = new ArrayList<Constraint>();

		for (String ctName : sortedConstraints) {
			orderedPossiblyFaultyConstraints.add(ctsByName.get(ctName));
		}
		return orderedPossiblyFaultyConstraints;
	}

	/**
	 * Checks if the current bitset is part of the known ones
	 * 
	 * @param all
	 * @param one
	 * @return
	 */
	public static boolean checkSetIsSubsetOfKnown(List<BitSet> all, BitSet one) {
		boolean result = false;
		BitSet tmp = null;
		if (one.cardinality() == 0) {
			return false;
		}
		for (BitSet bs : all) {
			tmp = (BitSet) one.clone();
			tmp.and(bs);
			if (tmp.cardinality() == one.cardinality()) {
				return true;
			}
		}

		return result;
	}

	/**
	 * Checks if the current bitset a superset of a known
	 * 
	 * @param all
	 * @param one
	 * @return
	 */
	public static boolean checkIsSupersetOfKnown(List<BitSet> all, BitSet one) {
		boolean result = false;
		BitSet tmp = null;
		for (BitSet bs : all) {
			tmp = (BitSet) bs.clone();
			tmp.and(one);

			// System.out.println("tmp:" + tmp.cardinality());
			// System.out.println("one:" + one.cardinality());
			// System.out.println("bs:" + bs.cardinality());

			if (bs.cardinality() > 0 && tmp.cardinality() == bs.cardinality() && one.cardinality() >= bs.cardinality()) {
				return true;
			}

			// bs: x 0 x 0
			// on: x 0 x x
		}

		return result;
	}

	/**
	 * Create a bitset from the list with correct indices
	 * 
	 * @param elements
	 * @return
	 */
	public static BitSet makeBitSet(List allElements, List subset) {
		BitSet result = new BitSet(allElements.size());
		int done = 0;
		int i = 0;
		for (Object elem : allElements) {
			if (subset.contains(elem)) {
				result.set(i);
				done++;
			}
			// There cannot be more
			if (done >= subset.size()) {
				return result;
			}
			i++;
		}

		return result;
	}

	/**
	 * Get the session info
	 * 
	 * @return
	 */
	public ExquisiteSession getSessionData() {
		return sessionData;
	}

}