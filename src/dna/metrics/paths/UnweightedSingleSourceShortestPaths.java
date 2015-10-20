package dna.metrics.paths;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;

public abstract class UnweightedSingleSourceShortestPaths extends
		SingleSourceShortestPaths {

	public UnweightedSingleSourceShortestPaths(String name, int sourceIndex) {
		super(name, sourceIndex);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m instanceof UnweightedSingleSourceShortestPaths;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	@Override
	protected double getCharacteristicPathLength() {
		return this.sssp.computeAverage();
	}

}
