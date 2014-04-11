package dna.updates.generators.weights;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weightsNew.IWeightedEdge;
import dna.graph.weightsNew.IWeightedNode;
import dna.graph.weightsNew.Weight;
import dna.graph.weightsNew.Weight.WeightSelection;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeWeight;

public class WeightChanges extends BatchGenerator {

	int nodes;
	int edges;

	private WeightSelection nSelection;
	private WeightSelection eSelection;

	public WeightChanges(int nodes, WeightSelection nSelection, int edges,
			WeightSelection eSelection) {
		super("WeightChanges");
		this.nodes = nodes;
		this.nSelection = nSelection;
		this.edges = edges;
		this.eSelection = eSelection;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, 0, 0, this.nodes, 0, 0, this.edges);

		HashSet<Edge> edges = new HashSet<Edge>();
		while (edges.size() < this.edges) {
			edges.add(g.getRandomEdge());
		}

		HashSet<Node> nodes = new HashSet<Node>();
		while (nodes.size() < this.nodes) {
			nodes.add(g.getRandomNode());
		}
		
		Weight w;
		for (Edge e : edges) {
			w = g.getGraphDatastructures().newEdgeWeight(eSelection);
			b.add(new EdgeWeight((IWeightedEdge) e, w));
		}

		for (Node n : nodes) {
			w = g.getGraphDatastructures().newNodeWeight(nSelection);
			b.add(new NodeWeight((IWeightedNode) n, w));
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return g.getNodeCount() >= this.nodes && g.getEdgeCount() >= this.edges;
	}

}
