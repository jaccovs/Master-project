package tests.ts.interactivity;

import org.exquisite.diagnosis.models.Diagnosis;

import java.util.List;

/**
 * Interface for user interaction. User interaction is used to determine the possible queries and to simulate the user interaction with a chosen
 * query.
 * 
 * @author Schmitz
 *
 */
public interface IUserInteraction<T> {

	/**
	 * Should return all possible queries based on the given diagnoses. The already used queries have to be excluded from the returned list.
	 * 
	 * @param diagnoses
	 * @return
	 */
	List<IUserQuery> calculatePossibleQueries(List<Diagnosis<T>> diagnoses);

	/**
	 * Should return the diagnoses that are supported by the given query.
	 * 
	 * @param query
	 * @param diagnoses
	 * @return
	 */
	List<Diagnosis<T>> getSupportedDiagnoses(IUserQuery query, List<Diagnosis<T>> diagnoses);

	/**
	 * Should simulate the user interaction with the given query and return a DiagnosisModelExpansion containing the changes to the diagnosis model
	 * based on the users answer.
	 * 
	 * @param query
	 * @return
	 */
	DiagnosisModelExpansion simulateUserInteraction(IUserQuery query);

}
