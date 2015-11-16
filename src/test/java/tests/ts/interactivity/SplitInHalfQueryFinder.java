package tests.ts.interactivity;

import org.exquisite.diagnosis.models.Diagnosis;

import java.util.List;

public class SplitInHalfQueryFinder<T> implements IBestQueryFinder<T> {

	IUserInteraction userInteraction;

	public SplitInHalfQueryFinder(IUserInteraction userInteraction) {
		this.userInteraction = userInteraction;
	}

	@Override
	public IUserQuery findBestQuery(List<IUserQuery> possibleQueries, List<Diagnosis<T>> diagnoses) {

		IUserQuery nearest = null;
		float difference = diagnoses.size();
		float halvedDiagnoses = diagnoses.size() / 2.0f;

		for (IUserQuery query : possibleQueries) {
			List<Diagnosis<T>> supportedDiagnoses = userInteraction.getSupportedDiagnoses(query, diagnoses);

			float newDifference = Math.abs(supportedDiagnoses.size() - halvedDiagnoses);
			if (nearest == null || newDifference < difference) {
				nearest = query;
				difference = newDifference;

				// if difference is 0 or 0.5, we cannot find any better query, so we can stop searching
				if (difference < 1.0f) {
					break;
				}
			}
		}

		return nearest;
	}

}
