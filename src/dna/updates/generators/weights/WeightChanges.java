package dna.updates.generators.weights;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.EdgeWeightSelection;
import dna.graph.weights.Weights.NodeWeightSelection;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeWeight;
import dna.util.parameters.ObjectParameter;

public class WeightChanges extends BatchGenerator {

	private EdgeWeightSelection ew;

	private int edges;

	private NodeWeightSelection nw;

	private int nodes;

	public WeightChanges(NodeWeightSelection nw, int nodes,
			EdgeWeightSelection ew, int edges) {
		super("WeightChanges", new ObjectParameter("EW", ew));
		this.nw = nw;
		this.nodes = nodes;
		this.ew = ew;
		this.edges = edges;
	}

	public WeightChanges(NodeWeightSelection nw, int nodes) {
		this(nw, nodes, null, 0);
	}

	public WeightChanges(EdgeWeightSelection ew, int edges) {
		this(null, 0, ew, edges);
	}

	@SuppressWarnings("rawtypes")
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

		for (Edge e : edges) {
			b.add(new EdgeWeight((IWeightedEdge) e, Weights.getWeight(this.ew)));
		}

		for (Node n : nodes) {
			b.add(new NodeWeight((IWeightedNode) n, Weights.getWeight(this.nw)));
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
