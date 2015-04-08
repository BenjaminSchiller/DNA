package dna.metrics.paths;

import java.util.Comparator;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.IWeighted;
import dna.graph.weights.IntWeight;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.BinnedDistributionDouble;
import dna.series.data.DistributionLong;
/**
 * 
 * Adaption of IntWeightedAllPairsShortestPathR
 * 
 * using NodeWeights and DoubleValues
 * 
 * @author barracuda317 (Maurice Wendt)
 * @date 25.10.2014
 */
public class DoubleWeightedAllPairsShortestPathsR extends
		DoubleWeightedAllPairsShortestPaths implements IRecomputation {
	
	

	public DoubleWeightedAllPairsShortestPathsR(double binsize) {
		super("DoubleWeightedAllPairsShortestPathsR", binsize);
		this.binsize=binsize;
	}

	@Override
	public boolean recompute() {
		this.apsp = new BinnedDistributionDouble("APSP", binsize);
		for (IElement source_ : this.g.getNodes()) {
			Node source = (Node) source_;

			double[] dist = this.getInitialDist(this.g.getMaxNodeIndex() + 1);
			Node[] previous = new Node[this.g.getMaxNodeIndex() + 1];
			boolean[] visited = new boolean[g.getMaxNodeIndex() + 1];
			PriorityQueue<Node> Q = new PriorityQueue<Node>(g.getNodeCount(),
					new DistComparator(dist));

			dist[source.getIndex()] = 0;
			Q.add(source);

			while (!Q.isEmpty()) {
				Node current = (Node) Q.remove();

				if (visited[current.getIndex()]) {
					continue;
				}
				visited[current.getIndex()] = true;

				if (current instanceof DirectedNode) {
					for (IElement e : ((DirectedNode) current)
							.getOutgoingEdges()) {
						Node n = ((DirectedEdge) e).getDst();
						Double3dWeight w = (Double3dWeight) ((IWeighted) n).getWeight();
						this.process(source, current, n, w.getZ(),
								dist, previous, visited, Q);
					}
				} else if (current instanceof UndirectedNode) {
					for (IElement e : ((UndirectedNode) current).getEdges()) {
						Node n = ((UndirectedEdge) e).getDifferingNode(current);
						Double3dWeight w = (Double3dWeight) ((IWeighted) e).getWeight();
						this.process(source, current, n, w.getZ(),
								dist, previous, visited, Q);
					}
				}
			}

			for (double d : dist) {
				if (d > 0 && d != Integer.MAX_VALUE) {
					this.apsp.incr(d);
				}
			}
		}

		return true;
	}

	protected class DistComparator implements Comparator<Node> {

		private double[] dist;

		public DistComparator(double[] dist) {
			this.dist = dist;
		}

		@Override
		public int compare(Node o1, Node o2) {
			double diff = this.dist[o1.getIndex()] - this.dist[o2.getIndex()];
			if(diff > 0)
				return 1;
			else if (diff < 0)
				return -1;
			else
				return 0;
		}
	}

	protected void process(Node source, Node current, Node n, double weight,
			double[] dist, Node[] previous, boolean[] visited,
			PriorityQueue<Node> Q) {
		if (n.getIndex() == source.getIndex()) {
			return;
		}
		double newDist = dist[current.getIndex()] + weight;
		if (previous[n.getIndex()] == null || newDist < dist[n.getIndex()]) {
			dist[n.getIndex()] = newDist;
			previous[n.getIndex()] = current;
			Q.add(n);
		}
	}

	protected double[] getInitialDist(int size) {
		double[] dist = new double[size];
		for (int i = 0; i < dist.length; i++) {
			dist[i] = Integer.MAX_VALUE;
		}
		return dist;
	}

}
