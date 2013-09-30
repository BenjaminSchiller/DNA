package dna.updates.generators;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.NodeRemoval;
import dna.util.parameters.IntParameter;

public class RandomNodeRemovals extends BatchGenerator {

	private int count;

	public RandomNodeRemovals(int count) {
		super("RandomNodeRemovals", new IntParameter("C", count));
		this.count = count;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, this.count, 0, 0, 0, 0);

		HashSet<Node> removed = new HashSet<Node>();
		while (removed.size() < this.count) {
			Node n = g.getRandomNode();
			if (removed.contains(n)) {
				continue;
			}
			removed.add(n);
			b.add(new NodeRemoval(n));
		}

		return b;
	}

	@Override
	public void reset() {
	}

}
