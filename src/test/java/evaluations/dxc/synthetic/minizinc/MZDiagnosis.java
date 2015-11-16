package evaluations.dxc.synthetic.minizinc;

import org.exquisite.diagnosis.models.Diagnosis;

public class MZDiagnosis extends Diagnosis<String> {
	
	private String diagnosis;
	
	public MZDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public String getDiagnosis() {
		return diagnosis;
	}
	
	@Override
	public String toString() {
		return diagnosis;
	}

}
