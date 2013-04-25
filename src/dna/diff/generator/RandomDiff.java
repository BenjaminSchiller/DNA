package dna.diff.generator;

import java.util.ArrayList;

import dna.diff.Diff;
import dna.graph.old.OldEdge;
import dna.graph.old.OldGraph;
import dna.util.Rand;

public class RandomDiff extends DiffGenerator {
	public RandomDiff(int add, int remove, boolean undirected) {
		super("RandomDiff-" + add + "-" + remove + "-" + undirected);
		this.add = add;
		this.remove = remove;
		this.undirected = undirected;
	}

	private int add;

	private int remove;

	private boolean undirected;

	@Override
	public Diff generate(OldGraph g) {
		Diff d = new Diff(g.getNodes().length, g.getTimestamp(),
				g.getTimestamp() + 1);

		while (d.getAddedEdges().size() < add) {
			OldEdge e = Rand.edge(g);
			if (g.containsEdge(e)) {
				continue;
			}
			d.addAddedEdges(e);
			if (undirected) {
				d.addAddedEdges(e.invert());
			}
		}

		ArrayList<OldEdge> edges = new ArrayList<OldEdge>(g.getEdges());
		while (d.getRemovedEdges().size() < remove) {
			OldEdge e = edges.get(Rand.rand.nextInt(edges.size()));
			d.addRemovedEdge(e);
			if (undirected) {
				d.addRemovedEdge(e.invert());
			}
		}

		return d;
	}
}
