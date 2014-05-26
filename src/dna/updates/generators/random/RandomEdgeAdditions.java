package dna.updates.generators.random;

import java.math.BigInteger;
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
		int maxEdgesToAdd = this.count;

		int oldEdgeCount = g.getEdgeCount();
		BigInteger maxEdgeCount = g.getMaxEdgeCount();
		BigInteger absoluteMaximumToInsert = maxEdgeCount.subtract(BigInteger
				.valueOf(oldEdgeCount));
		/**
		 * absoluteMaximumToInsert holds the upper bound of edges to be inserted
		 * into the current graph
		 */
		if (absoluteMaximumToInsert
				.compareTo(BigInteger.valueOf(maxEdgesToAdd)) < 0) {
			/**
			 * The absolute maximum is smaller than the number of edges that
			 * should be inserted by this batch generator. But as we cannot
			 * insert more edges than the maximum dictated by the graph itself,
			 * adjust the bound
			 */
			maxEdgesToAdd = absoluteMaximumToInsert.intValue();
		}

		while (added.size() < maxEdgesToAdd) {
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
		int oldEdgeCount = g.getEdgeCount();
		BigInteger maxEdgeCount = g.getMaxEdgeCount();
		BigInteger absoluteMaximumToInsert = maxEdgeCount.subtract(BigInteger
				.valueOf(oldEdgeCount));
		return (absoluteMaximumToInsert.compareTo(BigInteger.ZERO) > 0);
	}
}
