package tests.ts.interactivity;

import java.util.List;

import org.exquisite.diagnosis.models.Diagnosis;

public class SplitInHalfQueryFinder implements IBestQueryFinder {

	IUserInteraction userInteraction;

	public SplitInHalfQueryFinder(IUserInteraction userInteraction) {
		this.userInteraction = userInteraction;
	}

	@Override
	public IUserQuery findBestQuery(List<IUserQuery> possibleQueries, List<Diagnosis> diagnoses) {

		IUserQuery nearest = null;
		float difference = diagnoses.size();
		float halvedDiagnoses = diagnoses.size() / 2.0f;

		for (IUserQuery query : possibleQueries) {
			List<Diagnosis> supportedDiagnoses = userInteraction.getSupportedDiagnoses(query, diagnoses);

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
