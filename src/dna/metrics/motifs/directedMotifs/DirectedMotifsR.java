package dna.metrics.motifs.directedMotifs;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class DirectedMotifsR extends DirectedMotifs {

	public DirectedMotifsR(String name, ApplicationType type,
			MetricType metricType) {
		super("DirectedMotifsR", ApplicationType.Recomputation,
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
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		// TODO implement
		return true;
	}

}
