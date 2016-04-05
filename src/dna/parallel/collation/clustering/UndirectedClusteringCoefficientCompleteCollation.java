package dna.parallel.collation.clustering;

import java.util.Set;

import dna.graph.nodes.Node;
import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.lists.LongList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;

public class UndirectedClusteringCoefficientCompleteCollation extends
		Collation<UndirectedClusteringCoefficient, CompletePartition> {

	public UndirectedClusteringCoefficientCompleteCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("UndirectedClusteringCoefficientCompleteCollation",
				MetricType.exact, PartitionType.Complete,
				new UndirectedClusteringCoefficientR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] {
						"UndirectedClusteringCoefficientR",
						"UndirectedClusteringCoefficientU" }, new String[0],
				new String[0], new String[] { "nodePotentialCount",
						"nodeTriangleCount" });
	}

	@Override
	public boolean collate(CollationData cd) {
		m.nodePotentialCount = new LongList(0);
		m.nodeTriangleCount = new LongList(0);

		m.triangleCount = 0;
		m.potentialCount = 0;
		m.localCC = new NodeValueList("localCC", 0);
		int i = 0;
		for (MetricData md : this.getSources(cd)) {
			for (Node n : (Set<Node>) cd.aux.nodes[i]) {
				m.localCC.setValue(n.getIndex(),
						md.getNodeValues().get("localCC")
								.getValue(n.getIndex()));
				long p = (long) md.getNodeValues().get("nodePotentialCount")
						.getValue(n.getIndex());
				long t = (long) md.getNodeValues().get("nodeTriangleCount")
						.getValue(n.getIndex());
				m.nodePotentialCount.setValue(n.getIndex(), p);
				m.nodeTriangleCount.setValue(n.getIndex(), t);
				m.triangleCount += t;
				m.potentialCount += p;
			}
			i++;
		}
		m.globalCC = 1.0 * m.triangleCount / m.potentialCount;
		m.averageCC = ArrayUtils.avgIgnoreNaN(m.localCC.getValues());

		return true;
	}

}
