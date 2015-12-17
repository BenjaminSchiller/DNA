package dna.metrics.clustering.local;

import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.nodes.DirectedNode;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class DirectedLocalClusteringCoefficient extends
		LocalClusteringCoefficient {

	public DirectedLocalClusteringCoefficient(String name, int... indexes) {
		super(name, indexes);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		if (m == null || !(m instanceof DirectedLocalClusteringCoefficient)) {
			return false;
		}
		return ArrayUtils.equals(this.indexes,
				((DirectedLocalClusteringCoefficient) m).indexes);
	}

	protected long computeOpen(int index) {
		DirectedNode n = (DirectedNode) this.g.getNode(index);
		return n.getNeighborCount() * (n.getNeighborCount() - 1);
	}

	protected long computeClosed(int index) {
		DirectedNode n = (DirectedNode) this.g.getNode(index);

		long count = 0;

		for (IElement n1_ : n.getNeighbors()) {
			DirectedNode n1 = (DirectedNode) n1_;
			for (IElement n2_ : n.getNeighbors()) {
				DirectedNode n2 = (DirectedNode) n2_;
				if (n1.equals(n2)) {
					continue;
				}
				if (n1.hasEdge(n1, n2)) {
					count++;
				}
			}
		}

		return count;
	}

	@Override
	public boolean isApplicable(IGraph g) {
		return g.getGraphDatastructures().isNodeType(DirectedNode.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(DirectedNode.class);
	}

}
