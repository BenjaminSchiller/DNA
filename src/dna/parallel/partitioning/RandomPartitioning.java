package dna.parallel.partitioning;

import java.util.List;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.util.Rand;

public class RandomPartitioning extends Partitioning {

	public RandomPartitioning() {
		super("RandomPartitioning");
	}

	@Override
	protected List<Node>[] partition(Graph g, int partitionCount) {
		List<Node>[] nodess = this.getInitialList(g, partitionCount);
		for (IElement n : g.getNodes()) {
			nodess[Rand.rand.nextInt(partitionCount)].add((Node) n);
		}

		return nodess;
	}

}
