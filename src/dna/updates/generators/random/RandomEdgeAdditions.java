package dna.updates.generators.random;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.util.parameters.IntParameter;

public class RandomEdgeAdditions extends BatchGenerator {

	private int count;

	public RandomEdgeAdditions(int count) {
		super("RandomEdgeAdditions", new IntParameter("C", count));
		this.count = count;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, this.count, 0, 0);

		HashSet<Edge> added = new HashSet<Edge>();
		while (added.size() < this.count
				&& g.getEdgeCount() + added.size() < g.getMaxEdgeCount()) {
			Node n1 = g.getRandomNode();
			Node n2 = g.getRandomNode();
			if (n1.equals(n2)) {
				continue;
			}
			Edge e = g.getGraphDatastructures().newEdgeInstance(n1, n2);
			if (added.contains(e) || g.containsEdge(e)) {
				continue;
			}
			added.add(e);
			b.add(new EdgeAddition(e));
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return g.getEdgeCount() < g.getMaxEdgeCount();
	}
}
