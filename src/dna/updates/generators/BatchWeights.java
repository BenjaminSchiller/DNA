package dna.updates.generators;

import dna.graph.Graph;
import dna.graph.weights.IWeighted;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.EdgeWeightSelection;
import dna.graph.weights.Weights.NodeWeightSelection;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.util.ArrayUtils;
import dna.util.parameters.ObjectParameter;

public class BatchWeights extends BatchGenerator {

	private BatchGenerator bg;

	private NodeWeightSelection nw;

	private EdgeWeightSelection ew;

	public BatchWeights(BatchGenerator bg, NodeWeightSelection nw,
			EdgeWeightSelection ew) {
		super("Weighted" + bg.getNamePlain(), ArrayUtils.append(
				bg.getParameters(), new ObjectParameter("NW", nw),
				new ObjectParameter("EW", ew)));
		this.bg = bg;
		this.nw = nw;
		this.ew = ew;
	}

	public BatchWeights(BatchGenerator bg, NodeWeightSelection nw) {
		super("Weighted" + bg.getNamePlain(), ArrayUtils.append(
				bg.getParameters(), new ObjectParameter("NW", nw)));
		this.bg = bg;
		this.nw = nw;
		this.ew = null;
	}

	public BatchWeights(BatchGenerator bg, EdgeWeightSelection ew) {
		super("Weighted" + bg.getNamePlain(), ArrayUtils.append(
				bg.getParameters(), new ObjectParameter("EW", ew)));
		this.bg = bg;
		this.nw = null;
		this.ew = ew;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = this.bg.generate(g);

		if (this.nw != null && !this.nw.equals(NodeWeightSelection.None)) {
			for (NodeAddition u : b.getNodeAdditions()) {
				((IWeighted) u.getNode()).setWeight(Weights.getWeight(this.nw));
			}
		}

		if (this.ew != null && !this.ew.equals(EdgeWeightSelection.None)) {
			for (EdgeAddition u : b.getEdgeAdditions()) {
				((IWeighted) u.getEdge()).setWeight(Weights.getWeight(this.ew));
			}
		}

		return b;
	}

	@Override
	public void reset() {
	}

}
