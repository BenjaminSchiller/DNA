package dna.metrics.connectedComponents;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class ConnectedComponentR extends ConnectedComponent {

	public ConnectedComponentR() {
		super("CCUndirectedR", ApplicationType.Recomputation);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
