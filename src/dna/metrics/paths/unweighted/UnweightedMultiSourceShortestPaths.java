package dna.metrics.paths.unweighted;

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
		HashSet<Node> all = new HashSet<Node>();
		for (IElement n : this.getNodesOfAssignedTypes()) {
			all.add((Node) n);
		}

		if (all.size() == 0) {
			return new ArrayList<Node>(0);
		}

		if (all.size() < this.sources) {
			return all;
		}

		HashSet<Integer> indexes_ = new HashSet<Integer>();
		while (indexes_.size() < this.sources) {
			indexes_.add(Rand.rand.nextInt(all.size()));
		}
		int[] indexes = new int[indexes_.size()];
		int x = 0;
		for (int index : indexes_) {
			indexes[x++] = index;
		}

		// System.out.println("INDEXES: " + indexes.length + " => " + indexes_);

		ArrayList<Node> nodes = new ArrayList<Node>(this.sources);
		int index = 0;
		int i = 0;
		for (Node n : all) {
			if (index == indexes[i]) {
				// System.out.println("   ADDING: " + i + " => " + n +
				// " @ index=" + index);
				nodes.add(n);
				i++;
			}
			if (i >= indexes.length) {
				break;
			}
			index++;
		}
		// System.out.println("   => " + nodes.size() + " => " + nodes);
		return nodes;
	}
}
