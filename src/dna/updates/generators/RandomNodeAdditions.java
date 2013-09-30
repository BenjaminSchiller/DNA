package dna.updates.generators;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.util.parameters.IntParameter;

public class RandomNodeAdditions extends BatchGenerator {

	private int count;

	public RandomNodeAdditions(int count) {
		super("RandomNodeAdditions", new IntParameter("C", count));
		this.count = count;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, this.count, 0, 0, 0, 0, 0);

		HashSet<Node> added = new HashSet<Node>();
		int index = g.getMaxNodeIndex() + 1;
		while (added.size() < this.count) {
			Node n = g.getGraphDatastructures().newNodeInstance(index++);
			added.add(n);
			b.add(new NodeAddition(n));
		}

		return b;
	}

	@Override
	public void reset() {
	}

}
