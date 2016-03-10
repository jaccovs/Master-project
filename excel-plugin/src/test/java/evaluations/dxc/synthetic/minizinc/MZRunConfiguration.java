package evaluations.dxc.synthetic.minizinc;

import evaluations.configuration.AbstractRunConfiguration;

public class MZRunConfiguration extends AbstractRunConfiguration {

	public MZRunConfiguration(int threads) {
		super(threads);
	}

	@Override
	public String getName() {
		return "MZ_" + threads;
	}

}
