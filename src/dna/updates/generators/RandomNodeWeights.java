package dna.updates.generators;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.NodeWeightSelection;
import dna.updates.batch.Batch;
import dna.updates.update.NodeWeight;
import dna.util.parameters.ObjectParameter;

public class RandomNodeWeights extends BatchGenerator {

	private NodeWeightSelection nw;

	private int count;

	public RandomNodeWeights(NodeWeightSelection nw, int count) {
		super("RandomNodeWeights", new ObjectParameter("NW", nw),
				new ObjectParameter("C", count));
		this.nw = nw;
		this.count = count;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, this.count, 0, 0, 0);

		HashSet<Node> nodes = new HashSet<Node>();
		while (nodes.size() < this.count && nodes.size() < g.getNodeCount()) {
			Node n = g.getRandomNode();
			if (nodes.contains(n)) {
				continue;
			}
			nodes.add(n);
			b.add(new NodeWeight((IWeightedNode) n, Weights.getWeight(this.nw)));
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return g.getNodeCount() > 0;
	}

}
