package dna.metrics.parallelization.collation.clustering;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.parallelization.collation.Collation;
import dna.metrics.parallelization.partitioning.Partition;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;

public class UndirectedClusteringCoefficientCollationOverlapping extends
		Collation<UndirectedClusteringCoefficient> {

	public UndirectedClusteringCoefficientCollationOverlapping() {
		super("UndirectedClusteringCoefficientCollationOverlapping");
	}

	@Override
	public boolean collate(Graph g, Partition[] partitions) {
		this.metric.triangleCount = 0;
		this.metric.triangleCount = 0;
		this.metric.potentialCount = 0;
		this.metric.averageCC = 0;
		this.metric.localCC = new NodeValueList("localCC",
				g.getMaxNodeIndex() + 1);
		for (Partition p : partitions) {
			UndirectedClusteringCoefficient m = (UndirectedClusteringCoefficient) p
					.getMetric();
			for (Node n : p.getNodes()) {
				this.metric.triangleCount += m.nodeTriangleCount.getValue(n
						.getIndex());
				this.metric.potentialCount += m.nodePotentialCount.getValue(n
						.getIndex());
				this.metric.localCC.setValue(n.getIndex(),
						m.localCC.getValue(n.getIndex()));
			}
		}

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
		return new UndirectedClusteringCoefficientCollationOverlapping();
	}

}
