package dna.updates.generators.random;

import java.util.HashSet;

import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeRemoval;
import dna.util.parameters.IntParameter;

public class RandomEdgeRemovals extends BatchGenerator {

	private int count;

	public RandomEdgeRemovals(int count) {
		super("RandomEdgeRemovals", new IntParameter("C", count));
		this.count = count;
	}

	@Override
	public Batch generate(IGraph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, 0, 0, this.count, 0);

		HashSet<Edge> removed = new HashSet<Edge>();
		while (removed.size() < this.count && removed.size() < g.getEdgeCount()) {
			Edge e = g.getRandomEdge();
			if (removed.contains(e)) {
				continue;
			}
			removed.add(e);
			b.add(new EdgeRemoval(e));
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return g.getEdgeCount() > 0;
	}

}
