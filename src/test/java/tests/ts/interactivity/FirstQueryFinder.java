package tests.ts.interactivity;

import org.exquisite.diagnosis.models.Diagnosis;

import java.util.List;

/**
 * A query finder that simply returns the first query as the best one.
 * 
 * @author Schmitz
 *
 */
public class FirstQueryFinder<T> implements IBestQueryFinder<T> {

	@Override
	public IUserQuery findBestQuery(List<IUserQuery> possibleQueries, List<Diagnosis<T>> diagnoses) {
		return possibleQueries.get(0);
	}

}
