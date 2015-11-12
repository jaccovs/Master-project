package org.exquisite.diagnosis.ranking.smell;

public enum NeighborComparisonState {

	ISNONR1C1,ISDIFFERENTFROMFORMULACELL,ISSAMEASFORMULACELL,ISSAMEWITHOTHERBUTNOTWITHFORMULACELL;

	
	@Override
	public String toString() {
		return String.valueOf(super.ordinal());
	}
}
