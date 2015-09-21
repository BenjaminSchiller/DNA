package dna.metrics.parallelization.collation.clustering;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.parallelization.collation.PartitionedMetric;
import dna.metrics.parallelization.partitioning.Partition;
import dna.util.ArrayUtils;

public class PartitionedUndirectedClusteringCoefficientR extends
		UndirectedClusteringCoefficient implements IRecomputation, Cloneable,
		PartitionedMetric {

	public PartitionedUndirectedClusteringCoefficientR() {
		super("PartitionedUndirectedClusteringCoefficientR");
	}

	@Override
	public boolean recompute() {
		// Timer t = new Timer();
		boolean succ = this.compute();
		// t.end();
		// System.out.println("RECOMPUTED for " + this.g + " ("
		// + (t.getDutation() / 1000000) + " msec)");
		return succ;
	}

	@Override
	public Object clone() {
		return new PartitionedUndirectedClusteringCoefficientR();
	}

	@Override
	protected boolean computeUndirected() {

		// System.out.println(this.getName() + ": "
		// + this.partition.getNodes().size() + " / "
		// + this.partition.getGraph().getNodeCount());
		for (Node node : this.partition.getNodes()) {
			UndirectedNode a = (UndirectedNode) this.partition.getGraph()
					.getNode(node.getIndex());

			for (IElement e1Uncasted : a.getEdges()) {
				UndirectedEdge e1 = (UndirectedEdge) e1Uncasted;
				UndirectedNode b = (UndirectedNode) e1.getDifferingNode(a);
				for (IElement e2Uncasted : a.getEdges()) {
					UndirectedEdge e2 = (UndirectedEdge) e2Uncasted;
					UndirectedNode c = (UndirectedNode) e2.getDifferingNode(a);
					if (b.equals(c)) {
						continue;
					}
					this.nodePotentialCount.incr(a.getIndex());
					if (b.hasEdge(b, c)) {
						this.nodeTriangleCount.incr(a.getIndex());
					}
				}
			}
			this.nodeTriangleCount.div(a.getIndex(), 2);
			this.nodePotentialCount.div(a.getIndex(), 2);

			this.update(a.getIndex());
		}

		this.update();
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	protected Partition partition;

	@Override
	public Partition getPartition() {
		return partition;
	}

	@Override
	public void setPartition(Partition partition) {
		this.partition = partition;
	}

}
