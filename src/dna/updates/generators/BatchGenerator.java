package dna.updates.generators;

import dna.graph.Graph;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class BatchGenerator extends ParameterList {

	public BatchGenerator(String name, Parameter... parameters) {
		super(name, parameters);
	}

	public abstract Batch generate(Graph g);

	public abstract void reset();

	public abstract boolean isFurtherBatchPossible(Graph g);

}
