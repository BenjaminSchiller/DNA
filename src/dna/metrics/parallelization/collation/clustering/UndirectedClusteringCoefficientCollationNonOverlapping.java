package dna.metrics.parallelization.collation.clustering;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.parallelization.collation.Collation;
import dna.metrics.parallelization.partitioning.NonOverlappingPartition;
import dna.metrics.parallelization.partitioning.Partition;
import dna.series.data.lists.LongList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;

public class UndirectedClusteringCoefficientCollationNonOverlapping extends
		Collation<UndirectedClusteringCoefficient> {

	public UndirectedClusteringCoefficientCollationNonOverlapping() {
		super("UndirectedClusteringCoefficientCollationNonOverlapping");
	}

	@Override
	public boolean collate(IGraph g, Partition[] partitions) {
		this.metric.localCC = new NodeValueList("localCC",
				g.getMaxNodeIndex() + 1);
		this.metric.nodePotentialCount = new LongList(g.getMaxNodeIndex() + 1);
		this.metric.nodeTriangleCount = new LongList(g.getMaxNodeIndex() + 1);

		// get triangles from partitions (per node)
		UndirectedClusteringCoefficient m;
		for (Partition p : partitions) {
			m = (UndirectedClusteringCoefficient) p.getMetric();
			for (Node n : p.getNodes()) {
				long t = m.nodeTriangleCount.getValue(n.getIndex());
				this.metric.nodeTriangleCount.setValue(n.getIndex(), t);
			}
		}

		// add triangles (per node) for edges between partitions
		for (Partition p_ : partitions) {
			NonOverlappingPartition p = (NonOverlappingPartition) p_;
			for (Edge e : p.getExternalEdges()) {
				Node pNode, otherNode;
				if (p.isResponsibleFor(e.getN1())) {
					pNode = e.getN1();
					otherNode = e.getN2();
				} else {
					pNode = e.getN2();
					otherNode = e.getN1();
				}
				for (IElement neighbor_ : pNode.getEdges()) {
					Node neighbor = ((Edge) neighbor_).getDifferingNode(pNode);
					if (neighbor.equals(otherNode)) {
						continue;
					}
					if (otherNode.hasEdge(otherNode, neighbor)) {
						if (p.isResponsibleFor(neighbor)
								|| (neighbor.getIndex() < otherNode.getIndex())) {
							this.metric.nodeTriangleCount
									.incr(pNode.getIndex());
						}
					}
				}
			}
		}

		// compute potentials (per node)
		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			if (n.getDegree() <= 1) {
				this.metric.nodePotentialCount.setValue(n.getIndex(), 0);
			} else {
				long p = n.getDegree() * (n.getDegree() - 1) / 2;
				this.metric.nodePotentialCount.setValue(n.getIndex(), p);
				this.metric.potentialCount += p;
			}
		}

		// compute local CC (per node)
		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			long p = this.metric.nodePotentialCount.getValue(n.getIndex());
			long t = this.metric.nodeTriangleCount.getValue(n.getIndex());
			if (p == 0) {
				this.metric.localCC.setValue(n.getIndex(), 0);
			} else {
				double lcc = (double) t / (double) p;
				this.metric.localCC.setValue(n.getIndex(), lcc);
			}
		}

		// compute potentials (total)
		this.metric.potentialCount = this.metric.nodePotentialCount.getSum();

		// compute triangles (total)
		this.metric.triangleCount = this.metric.nodeTriangleCount.getSum();

		this.metric.globalCC = (double) this.metric.triangleCount
				/ (double) this.metric.potentialCount;
		this.metric.averageCC = ArrayUtils.avgIgnoreNaN(this.metric.localCC
				.getValues());

		return true;
	}

	@Override
	public boolean isCollatable(Metric m) {
		return m != null && m instanceof UndirectedClusteringCoefficient;
	}

	@Override
	public Collation<UndirectedClusteringCoefficient> clone() {
		return new UndirectedClusteringCoefficientCollationNonOverlapping();
	}

}
