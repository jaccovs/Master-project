package tests.ts.interactivity;

import org.exquisite.diagnosis.models.Diagnosis;

import java.util.List;

/**
 * Interface for a best query finder. Best query finders are used to select the query, that should be asked next to narrow down the set of diagnoses.
 * 
 * @author Schmitz
 *
 */
public interface IBestQueryFinder<T> {

	/**
	 * Should return the next query to use.
	 * 
	 * @param possibleQueries
	 * @param diagnoses
	 * @return
	 */
	IUserQuery findBestQuery(List<IUserQuery> possibleQueries, List<Diagnosis<T>> diagnoses);
}
