package dna.metrics.paths;

import java.util.ArrayList;
import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.util.Rand;

public abstract class UnweightedMultiSourceShortestPaths extends
		UnweightedAllPairsShortestPaths {

	public static enum SourceSelection {
		RANDOM
	}

	public int sources;

	public UnweightedMultiSourceShortestPaths(String name, int sources,
			String[] nodeTypes) {
		super(name, MetricType.heuristic, nodeTypes);
		// super(name, MetricType.heuristic, nodeTypes, new StringParameter(
		// "sourceSelection", SourceSelection.RANDOM.toString()),
		// new IntParameter("sources", sources));
		this.sources = sources;
	}

	public UnweightedMultiSourceShortestPaths(String name, int sources) {
		super(name, MetricType.heuristic);
		// super(name, MetricType.heuristic, new StringParameter(
		// "sourceSelection", SourceSelection.RANDOM.toString()),
		// new IntParameter("sources", sources));
		this.sources = sources;
	}

	protected Iterable<Node> getSources() {
		// System.out.println("xxx - GETTING SOURCES...");
		HashSet<Node> all = new HashSet<Node>();
		for (IElement n : this.getNodesOfAssignedTypes()) {
			all.add((Node) n);
		}
		// System.out.println("xxx - " + all.size() + " nodes to select from");

		if (all.size() == 0) {
			// System.out.println("xxx - using none!");
			return new ArrayList<Node>(0);
		}

		Node[] available = new Node[all.size()];
		int x = 0;
		for (Node n : all) {
			available[x++] = n;
		}

		ArrayList<Node> selected = new ArrayList<Node>(this.sources);
		for (int i = 0; i < this.sources; i++) {
			selected.add(available[Rand.rand.nextInt(available.length)]);
		}

		// System.out.println("xxx - selected " + selected.size() + " nodes: " +
		// selected);

		return selected;

		// if (all.size() < this.sources) {
		// System.out.println("xxx - using all!");
		// return all;
		// }
		//
		// HashSet<Integer> indexes_ = new HashSet<Integer>();
		// while (indexes_.size() < this.sources) {
		// indexes_.add(Rand.rand.nextInt(all.size()));
		// }
		// int[] indexes = new int[indexes_.size()];
		// int x = 0;
		// for (int index : indexes_) {
		// indexes[x++] = index;
		// }
		//
		// // System.out.println("INDEXES: " + indexes.length + " => " +
		// indexes_);
		//
		// ArrayList<Node> nodes = new ArrayList<Node>(this.sources);
		// int index = 0;
		// int i = 0;
		// for (Node n : all) {
		// if (index == indexes[i]) {
		// // System.out.println("   ADDING: " + i + " => " + n +
		// // " @ index=" + index);
		// nodes.add(n);
		// i++;
		// }
		// if (i >= indexes.length) {
		// break;
		// }
		// index++;
		// }
		// System.out.println("xxx -   => " + nodes.size() + " => " + nodes);
		// return nodes;
	}
}