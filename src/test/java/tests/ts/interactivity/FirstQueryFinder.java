package tests.ts.interactivity;

import java.util.List;

import org.exquisite.diagnosis.models.Diagnosis;

/**
 * A query finder that simply returns the first query as the best one.
 * 
 * @author Schmitz
 *
 */
public class FirstQueryFinder implements IBestQueryFinder {

	@Override
	public IUserQuery findBestQuery(List<IUserQuery> possibleQueries, List<Diagnosis> diagnoses) {
		return possibleQueries.get(0);
	}

}
