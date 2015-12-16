package dna.metrics.clustering.local;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class UndirectedLocalClusteringCoefficient extends
		LocalClusteringCoefficient {

	public UndirectedLocalClusteringCoefficient(String name, int... indexes) {
		super(name, indexes);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		if (m == null || !(m instanceof UndirectedLocalClusteringCoefficient)) {
			return false;
		}
		return ArrayUtils.equals(this.indexes,
				((UndirectedLocalClusteringCoefficient) m).indexes);
	}

	protected long computeOpenDirected(int index) {
		DirectedNode n = (DirectedNode) this.g.getNode(index);
		return n.getNeighborCount() * (n.getNeighborCount() - 1) / 2;
	}

	protected long computeClosedDirected(int index) {
		DirectedNode n = (DirectedNode) this.g.getNode(index);

		long count = 0;

		for (IElement n1_ : n.getNeighbors()) {
			DirectedNode n1 = (DirectedNode) n1_;
			for (IElement n2_ : n.getNeighbors()) {
				DirectedNode n2 = (DirectedNode) n2_;
				if (n1.equals(n2)) {
					break;
				}
				if (n1.hasNeighbor(n2)) {
					count++;
				}
			}
		}

		return count;
	}

	protected long computeOpenUndirected(int index) {
		UndirectedNode n = (UndirectedNode) this.g.getNode(index);
		return n.getDegree() * (n.getDegree() - 1) / 2;
	}

	protected long computeClosedUndirected(int index) {
		UndirectedNode n = (UndirectedNode) this.g.getNode(index);

		long count = 0;

		for (IElement e1_ : n.getEdges()) {
			UndirectedEdge e1 = (UndirectedEdge) e1_;
			UndirectedNode n1 = (UndirectedNode) e1.getDifferingNode(n);
			for (IElement e2_ : n.getEdges()) {
				UndirectedEdge e2 = (UndirectedEdge) e2_;
				UndirectedNode n2 = (UndirectedNode) e2.getDifferingNode(n);

				if (n1.equals(n2)) {
					break;
				}
				if (n1.hasEdge(n1, n2)) {
					count++;
				}
			}
		}

		return count;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

}
