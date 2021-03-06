package tests.ts.interactivity;

import org.exquisite.core.engines.tree.Node;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.List;

/**
 * A diagnosis engine that will simulate user interactivity to always return the one correct diagnosis.
 * 
 * @author Schmitz
 *
 */
public class InteractivityDiagnosisEngine<T> extends AbstractHSDagEngine<T> {

	/**
	 * Inner engine is used to calculate the diagnosis candidates.
	 */
	IDiagnosisEngine<T> innerEngine;

	/**
	 * User interaction is used to determine and to simulate the user interaction
	 */
	IUserInteraction<T> userInteraction;

	/**
	 * Best query finder is used to determine the next query that should be asked to the user
	 */
	IBestQueryFinder bestQueryFinder;

	int numberOfQueries = 0;
	int numberOfDiagnosisRuns = 0;
	long diagnosisNanos = 0;

	float diagnosisTime;
	float userInteractionTime;

	public InteractivityDiagnosisEngine(ExcelExquisiteSession sessionData, IDiagnosisEngine<T> innerEngine,
										int diagnosesPerQuery,
										IUserInteraction userInteraction, IBestQueryFinder bestQueryFinder) {
		super(sessionData);

		this.userInteraction = userInteraction;
		this.bestQueryFinder = bestQueryFinder;

		sessionData.getConfiguration().maxDiagnoses = diagnosesPerQuery;

		this.innerEngine = innerEngine;
	}

	public int getNumberOfQueries() {
		return numberOfQueries;
	}

	public int getNumberOfDiagnosisRuns() {
		return numberOfDiagnosisRuns;
	}

	public float getDiagnosisTime() {
		return diagnosisTime;
	}

	public float getUserInteractionTime() {
		return userInteractionTime;
	}

	@Override
	public List<Diagnosis<T>> calculateDiagnoses() throws DiagnosisException {
		long startTime = System.nanoTime();

		List<Diagnosis<T>> diagnoses;

		do {
			innerEngine.resetEngine();
			innerEngine.getDiagnosisModel().getDiagnosisModel() = innerEngine.getDiagnosisModel();
			numberOfDiagnosisRuns++;
			long innerStart = System.nanoTime();
			diagnoses = innerEngine.calculateDiagnoses();
			long innerEnd = System.nanoTime();
			diagnosisNanos += innerEnd - innerStart;

			if (diagnoses.size() > 1) {
				List<IUserQuery> possibleQueries = userInteraction.calculatePossibleQueries(diagnoses);
				if (possibleQueries.size() == 0) {
					System.out.println("No query options found!");
				}
				IUserQuery bestQuery = bestQueryFinder.findBestQuery(possibleQueries, diagnoses);
				numberOfQueries++;
				DiagnosisModelExpansion diagModelExpansion = userInteraction.simulateUserInteraction(bestQuery);

				expandDiagnosisModel(innerEngine.getDiagnosisModel(), diagModelExpansion);
			}
		} while (diagnoses.size() > 1);

		finishedTime = System.nanoTime();

		diagnosisTime = diagnosisNanos / 1000000f;
		userInteractionTime = (finishedTime - startTime - diagnosisNanos) / 1000000f;

		return diagnoses;
	}

	/**
	 * Changes the given diagnosis model with the changes stored in the DiagnosisModelExpansion
	 * 
	 * @param model
	 * @param diagModelExpansion
	 */
	private void expandDiagnosisModel(DiagnosisModel<T> model, DiagnosisModelExpansion diagModelExpansion) {

		model.getPossiblyFaultyStatements().removeAll(diagModelExpansion.getPossiblyFaultyConstraintsToRemove());

		model.getCorrectStatements().addAll(diagModelExpansion.getCorrectConstraintsToAdd());

		model.getFaultyStatements().addAll(diagModelExpansion.getCertainlyFaultyConstraintsToAdd());
	}

	@Override
	public void expandNodes(List<Node<T>> nodesToExpand) throws DomainSizeException {
		// Nothing to do here

	}

}
