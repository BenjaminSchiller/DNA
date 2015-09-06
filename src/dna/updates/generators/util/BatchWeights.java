package dna.updates.generators.util;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.weights.IWeighted;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.util.ArrayUtils;
import dna.util.parameters.ObjectParameter;

public class BatchWeights extends BatchGenerator {

	private BatchGenerator bg;

	private WeightSelection nw;
	private WeightSelection ew;

	public BatchWeights(BatchGenerator bg, WeightSelection nw,
			WeightSelection ew) {
		super("Weighted" + bg.getNamePlain(), ArrayUtils.append(
				bg.getParameters(), new ObjectParameter("NW", nw),
				new ObjectParameter("EW", ew)));
		this.bg = bg;
		this.nw = nw;
		this.ew = ew;
	}

	@Override
	public Batch generate(IGraph g) {
		Batch b = this.bg.generate(g);
		GraphDataStructure gds = g.getGraphDatastructures();

		if (this.nw != null && !this.nw.equals(WeightSelection.None)) {
			for (NodeAddition u : b.getNodeAdditions()) {
				Weight nodeWeight = gds.newNodeWeight(this.nw);
				((IWeighted) u.getNode()).setWeight(nodeWeight);
			}
		}

		if (this.ew != null && !this.ew.equals(WeightSelection.None)) {
			for (EdgeAddition u : b.getEdgeAdditions()) {
				Weight edgeWeight = gds.newEdgeWeight(this.ew);
				((IWeighted) u.getEdge()).setWeight(edgeWeight);
			}
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return this.bg.isFurtherBatchPossible(g);
	}

}
