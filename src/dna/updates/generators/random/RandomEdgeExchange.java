package dna.updates.generators.random;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.util.parameters.IntParameter;

public class RandomEdgeExchange extends BatchGenerator {

	private int edges;

	private int maxFails;

	public RandomEdgeExchange(int edges, int maxFails) {
		super("RandomEdgeExchange", new IntParameter("EDGES", edges),
				new IntParameter("MAX_FAILS", maxFails));
		this.edges = edges;
		this.maxFails = maxFails;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1);

		int fails = 0;
		HashSet<Edge> removedEdges = new HashSet<Edge>();
		HashSet<Edge> addedEdges = new HashSet<Edge>();
		while (b.getSize() / 4 < this.edges && fails < this.maxFails) {
			if (fails > this.maxFails) {
				break;
			}
			if (g.getGraphDatastructures().getEdgeType()
					.isAssignableFrom(DirectedEdge.class)) {
				DirectedEdge e1 = (DirectedEdge) g.getRandomEdge();
				DirectedEdge e2 = (DirectedEdge) g.getRandomEdge();
				if (e1.getSrc().equals(e2.getSrc())
						|| e1.getSrc().equals(e2.getDst())
						|| e1.getDst().equals(e2.getSrc())
						|| e1.getDst().equals(e2.getDst())) {
					fails++;
					continue;
				}
				if (removedEdges.contains(e1) || removedEdges.contains(e2)) {
					fails++;
					continue;
				}
				Edge e1_ = g.getGraphDatastructures().newEdgeInstance(
						e1.getSrc(), e2.getDst());
				Edge e2_ = g.getGraphDatastructures().newEdgeInstance(
						e2.getSrc(), e1.getDst());
				if (addedEdges.contains(e1_) || addedEdges.contains(e2_)) {
					fails++;
					continue;
				}
				if (g.containsEdge(e1_) || g.containsEdge(e2_)) {
					fails++;
					continue;
				}
				b.add(new EdgeRemoval(e1));
				b.add(new EdgeRemoval(e2));
				b.add(new EdgeAddition(e1_));
				b.add(new EdgeAddition(e2_));
				removedEdges.add(e1);
				removedEdges.add(e2);
				addedEdges.add(e1_);
				addedEdges.add(e2_);
			} else if (g.getGraphDatastructures().getEdgeType()
					.isAssignableFrom(UndirectedEdge.class)) {
				UndirectedEdge e1 = (UndirectedEdge) g.getRandomEdge();
				UndirectedEdge e2 = (UndirectedEdge) g.getRandomEdge();
				if (e1.getNode1().equals(e2.getNode1())
						|| e1.getNode1().equals(e2.getNode2())
						|| e1.getNode2().equals(e2.getNode1())
						|| e1.getNode2().equals(e2.getNode2())) {
					fails++;
					continue;
				}
				if (removedEdges.contains(e1) || removedEdges.contains(e2)) {
					fails++;
					continue;
				}
				Edge e1_ = g.getGraphDatastructures().newEdgeInstance(
						e1.getNode1(), e2.getNode2());
				Edge e2_ = g.getGraphDatastructures().newEdgeInstance(
						e1.getNode2(), e2.getNode1());
				if (addedEdges.contains(e1_) || addedEdges.contains(e2_)) {
					fails++;
					continue;
				}
				if (g.containsEdge(e1_) || g.containsEdge(e2_)) {
					fails++;
					continue;
				}
				b.add(new EdgeRemoval(e1));
				b.add(new EdgeRemoval(e2));
				b.add(new EdgeAddition(e1_));
				b.add(new EdgeAddition(e2_));
				removedEdges.add(e1);
				removedEdges.add(e2);
				addedEdges.add(e1_);
				addedEdges.add(e2_);
			}
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return this.edges < g.getEdgeCount() / 2;
	}
}
