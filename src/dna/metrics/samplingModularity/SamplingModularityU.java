package dna.metrics.samplingModularity;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * @author Benedict Jahn
 *
 */
public class SamplingModularityU extends SamplingModularity {

	/**
	 * 
	 */
	public SamplingModularityU() {
		// TODO Auto-generated constructor stub
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

	@Override
	public boolean compute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init_() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset_() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Value[] getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Distribution[] getDistributions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
