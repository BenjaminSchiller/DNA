package dna.metrics.betweenessCentrality;

import dna.updates.Batch;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class BCComp extends BetweenessCentrality {

	public BCComp() {
		super("BCComp", ApplicationType.Recomputation);
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
