package dna.parallel.collation.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.connectivity.WCBasic;
import dna.metrics.connectivity.WCBasicR;
import dna.metrics.connectivity.WCComponent;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.OverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.nodevaluelists.NodeValueList;

public class WCBasicOverlappingCollation extends
		Collation<WCBasicR, OverlappingPartition> {
	public WCBasicOverlappingCollation(String auxDir, String inputDir,
			int partitionCount, int run, Sleeper sleeper) {
		super("WCBasicOverlappingCollation", MetricType.exact,
				PartitionType.Overlapping, new WCBasicR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] { "WCBasicR",
						"WCBasicU" }, new String[0], new String[0],
				new String[] { "ids" });
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean collate(CollationData cd) {
		m.components = new ArrayList<WCComponent>();

		// get ids from worker batch data
		NodeValueList[] ids = new NodeValueList[cd.bd.length];
		int c = 0;
		for (MetricData md : this.getSources(cd)) {
			ids[c++] = md.getNodeValues().get("ids");
		}

		int maxIndex = 0;
		HashMap<Integer, Node> coarseNodes = new HashMap<Integer, Node>();
		HashSet<Edge> coarseEdges = new HashSet<Edge>();
		for (int i = 0; i < aux.nodes.length; i++) {
			for (Node n : (Set<Node>) aux.nodes[i]) {
				if (n.getIndex() > maxIndex) {
					maxIndex = n.getIndex();
				}
				int index = (int) ids[i].getValue(n.getIndex());
				for (int j = 0; j < aux.nodes.length; j++) {
					if (i != j) {
						if (!Double.isNaN(ids[j].getValue(n.getIndex()))) {
							int index2 = (int) ids[j].getValue(n.getIndex());
							if (index != index2) {
								Node n1 = this.getCoarseNode(coarseNodes,
										aux.gds, index);
								Node n2 = this.getCoarseNode(coarseNodes,
										aux.gds, index2);
								Edge e = aux.gds.newEdgeInstance(n1, n2);
								if (!coarseEdges.contains(e)) {
									coarseEdges.add(e);
									e.connectToNodes();
								}
							}
						}
					}
				}
			}
		}

		ArrayList<WCComponent> components = WCBasic
				.getComponents(new HashSet<IElement>(coarseNodes.values()));

		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		for (WCComponent component : components) {
			for (Node n : component.getNodes()) {
				if (n.getIndex() != component.getIndex()) {
					mapping.put(n.getIndex(), component.getIndex());
				}
			}
		}

		System.out.println("COMPONENTS:");
		for (WCComponent component : components) {
			System.out.println(component);
		}

		System.out.println("MAPPING:");
		for (int key : mapping.keySet()) {
			System.out.println("  " + key + " => " + mapping.get(key));
		}

		m.ids = new NodeValueList("ids", maxIndex + 1);
		for (int i = 0; i < aux.nodes.length; i++) {
			for (Node n : (Set<Node>) aux.nodes[i]) {
				int index = (int) ids[i].getValue(n.getIndex());
				if (mapping.containsKey(index)) {
					m.ids.setValue(n.getIndex(), mapping.get(index));
				} else {
					m.ids.setValue(n.getIndex(), index);
				}
			}
		}

		return true;
	}

	protected Node getCoarseNode(HashMap<Integer, Node> coarseNodes,
			GraphDataStructure gds, int index) {
		if (coarseNodes.containsKey(index)) {
			return coarseNodes.get(index);
		} else {
			Node n = gds.newNodeInstance(index);
			coarseNodes.put(index, n);
			return n;
		}
	}

	/**
	 * 
	 * computes the minimum value (!= NaN) for the given node over all lists.
	 * 
	 * @param ids
	 * @param n
	 * @return
	 */
	protected int getMinIndex(NodeValueList[] ids, Node n) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < ids.length; i++) {
			double v = ids[i].getValue(n.getIndex());
			if (Double.isNaN(v) || v >= min) {
				continue;
			}
			min = (int) v;
		}
		return min;
	}

	protected HashMap<Integer, WCComponent> initComdddddponents(
			HashSet<Integer> indexes) {
		HashMap<Integer, WCComponent> map = new HashMap<Integer, WCComponent>();
		for (int index : indexes) {
			WCComponent c = new WCComponent();
			m.components.add(c);
			map.put(index, c);
		}
		return map;
	}

	protected HashMap<Integer, Integer> getMappingFromDecrease(
			HashMap<Integer, Integer> decrease, GraphDataStructure gds) {
		HashSet<Integer> indexes = new HashSet<Integer>();
		indexes.addAll(decrease.keySet());
		indexes.addAll(decrease.values());

		HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
		for (int index : indexes) {
			nodes.put(index, gds.newNodeInstance(index));
		}

		for (Entry<Integer, Integer> entry : decrease.entrySet()) {
			Edge e = gds.newEdgeInstance(nodes.get(entry.getKey()),
					nodes.get(entry.getValue()));
			e.connectToNodes();
		}

		ArrayList<WCComponent> components = WCBasic
				.getComponents(new HashSet<IElement>(nodes.values()));

		HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		for (WCComponent component : components) {
			for (Node n : component.getNodes()) {
				if (n.getIndex() != component.getIndex()) {
					mapping.put(n.getIndex(), component.getIndex());
				}
			}
		}

		return mapping;
	}

	protected int getMapping(HashMap<Integer, Integer> decrease, int index) {
		if (!decrease.containsKey(index)) {
			return index;
		} else {
			return getMapping(decrease, decrease.get(index));
		}
	}
}
