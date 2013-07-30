package dna.metrics.richClubConnectivity;

import dna.updates.Batch;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCOneDegreeComp extends RCCOneDegree {

	public RCCOneDegreeComp() {
		super("RCCOneDegreeComp", ApplicationType.Recomputation);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

}
