package dna.metrics.centrality;

import java.util.ArrayList;
import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;
import dna.util.Rand;

public class BetweennessCentralityRH extends BetweennessCentrality implements
		IRecomputation {

	public int sources;

	public BetweennessCentralityRH(int sources) {
		super("BetweennessCentralityRH", MetricType.heuristic);
		this.sources = sources;
	}

	public BetweennessCentralityRH(int sources, String[] nodeTypes) {
		super("BetweennessCentralityRH", MetricType.heuristic, nodeTypes);
		this.sources = sources;
	}

	@Override
	public boolean recompute() {
		this.initProperties();
		for (Node n : this.getSources()) {
			this.process(n);
		}
		return true;
	}

	protected Iterable<Node> getSources() {
		HashSet<Node> all = new HashSet<Node>();
		for (IElement n : this.getNodesOfAssignedTypes()) {
			all.add((Node) n);
		}
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
		System.out.println("BCH: selected " + selected.size()
				+ " nodes for computation");
		return selected;
	}
}
