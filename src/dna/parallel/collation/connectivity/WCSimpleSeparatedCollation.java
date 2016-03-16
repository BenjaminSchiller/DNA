package dna.parallel.collation.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.connectivity.WCComponent;
import dna.metrics.connectivity.WCSimpleR;
import dna.parallel.auxData.SeparatedAuxData;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.partition.SeparatedPartition;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.nodevaluelists.NodeValueList;

public class WCSimpleSeparatedCollation extends
		Collation<WCSimpleR, SeparatedPartition> {
	public WCSimpleSeparatedCollation(String auxDir, String inputDir,
			int partitionCount, int run, Sleeper sleeper) {
		super("WCSimpleSeparatedCollation", MetricType.exact,
				PartitionType.Separated, new WCSimpleR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] { "WCSimpleR" },
				new String[0], new String[0], new String[] { "ids" });
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean collate(CollationData cd) {
		WCSimpleR m = (WCSimpleR) this.m;
		m.components = new ArrayList<WCComponent>();
		HashMap<Node, WCComponent> mapping = new HashMap<Node, WCComponent>();

		/*
		 * re-create partitions from partitions
		 */
		int p = 0;
		for (MetricData md : this.getSources(cd)) {
			HashMap<Integer, WCComponent> compMap = new HashMap<Integer, WCComponent>();

			NodeValueList ids = md.getNodeValues().get("ids");
			for (Node n : (Set<Node>) cd.aux.nodes[p]) {
				int index = (int) ids.getValue(n.getIndex());
				WCComponent c = null;
				if (!compMap.containsKey(index)) {
					c = new WCComponent(index);
					m.components.add(c);
					compMap.put(index, c);
				} else {
					c = compMap.get(index);
				}
				c.nodes.add(n);
				mapping.put(n, c);
			}
			p++;
		}

		/*
		 * merge partitions based on bridges
		 */
		for (Edge e : ((SeparatedAuxData) cd.aux).bridges) {
			WCComponent c1 = mapping.get(e.getN1());
			WCComponent c2 = mapping.get(e.getN2());
			if (c1 == c2) {
				continue;
			}
			c1.nodes.addAll(c2.nodes);
			for (Node n : c2.nodes) {
				mapping.put(n, c1);
			}
			m.components.remove(c2);
		}

		m.sortComponents();

		return true;
	}
}
