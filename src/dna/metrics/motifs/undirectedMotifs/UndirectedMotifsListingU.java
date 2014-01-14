package dna.metrics.motifs.undirectedMotifs;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class UndirectedMotifsListingU extends UndirectedMotifsComputation {

	public UndirectedMotifsListingU() {
		super("UndirectedMotifsListingU", ApplicationType.BeforeAndAfterUpdate,
				MetricType.exact);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

}
