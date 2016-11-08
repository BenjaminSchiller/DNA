package dna.metrics.paths;

import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;

public abstract class UnweightedAllPairsShortestPaths extends
		AllPairsShortestPaths {

	public UnweightedAllPairsShortestPaths(String name, MetricType metricType,
			String[] nodeTypes) {
		super(name, metricType, nodeTypes);
	}

	public UnweightedAllPairsShortestPaths(String name, MetricType metricType) {
		super(name, metricType);
	}

	public UnweightedAllPairsShortestPaths(String name) {
		super(name);
	}

	public UnweightedAllPairsShortestPaths(String name, String[] nodeTypes) {
		super(name, nodeTypes);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof UnweightedAllPairsShortestPaths;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	@Override
	public double getCharacteristicPathLength() {
		return this.apsp.computeAverage();
	}

	protected void compute(Node n) {
		boolean[] seen = new boolean[this.g.getMaxNodeIndex() + 1];
		int[] dist = new int[this.g.getMaxNodeIndex() + 1];
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(n);
		seen[n.getIndex()] = true;
		dist[n.getIndex()] = 0;

		while (!queue.isEmpty()) {
			Node current = queue.pop();

			if (current instanceof DirectedNode) {
				DirectedNode curr = (DirectedNode) current;
				for (IElement out_ : curr.getOutgoingEdges()) {
					DirectedNode out = ((DirectedEdge) out_).getDst();
					if (!seen[out.getIndex()]) {
						queue.addLast(out);
						seen[out.getIndex()] = true;
						dist[out.getIndex()] = dist[curr.getIndex()] + 1;
						this.apsp.incr(dist[out.getIndex()]);
					}
				}
			} else if (current instanceof UndirectedNode) {
				UndirectedNode curr = (UndirectedNode) current;
				for (IElement out_ : curr.getEdges()) {
					UndirectedNode out = (UndirectedNode) ((UndirectedEdge) out_)
							.getDifferingNode(curr);
					if (!seen[out.getIndex()]) {
						queue.addLast(out);
						seen[out.getIndex()] = true;
						dist[out.getIndex()] = dist[curr.getIndex()] + 1;
						this.apsp.incr(dist[out.getIndex()]);
					}
				}
			}

		}
	}

}
