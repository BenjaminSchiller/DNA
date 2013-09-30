package dna.updates.generators;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.EdgeWeightSelection;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeWeight;
import dna.util.parameters.ObjectParameter;

public class RandomEdgeWeights extends BatchGenerator {

	private EdgeWeightSelection ew;

	private int count;

	public RandomEdgeWeights(EdgeWeightSelection ew, int count) {
		super("RandomEdgeWeights", new ObjectParameter("EW", ew),
				new ObjectParameter("C", count));
		this.ew = ew;
		this.count = count;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, this.count, 0, 0, 0);

		HashSet<Edge> edges = new HashSet<Edge>();
		while (edges.size() < this.count) {
			Edge e = g.getRandomEdge();
			if (edges.contains(e)) {
				continue;
			}
			edges.add(e);
			b.add(new EdgeWeight((IWeightedEdge) e, Weights.getWeight(this.ew)));
		}

		return b;
	}

	@Override
	public void reset() {
	}

}
