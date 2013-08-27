package dna.updates.undirected;

import java.util.HashSet;

import dna.datastructures.GraphDataStructure;
import dna.graph.Graph;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.Batch;
import dna.updates.NodeRemoval;
import dna.util.parameters.IntParameter;

/**
 * 
 * batch generator for random node removals. the nodes to be removed are
 * selected uniformly at random from all existing nodes. no node is removed
 * twice.
 * 
 * @author benni
 * 
 */
public class RandomUndirectedNodeRemoval extends UndirectedBatchGenerator {

	private int removals;

	/**
	 * 
	 * @param removals
	 *            nodes to remove with reach batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomUndirectedNodeRemoval(int removals, GraphDataStructure datastructures) {
		super("randomUndirectedNodeRemoval", new IntParameter("removals", removals), datastructures);
		this.removals = removals;
	}

	@Override
	public Batch<UndirectedEdge> generate(Graph graph) {
		Batch<UndirectedEdge> batch = new Batch<UndirectedEdge>(this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, 0, this.removals, 0, 0, 0, 0);
		HashSet<UndirectedNode> removed = new HashSet<UndirectedNode>(this.removals);
		while (batch.getSize() < this.removals) {
			UndirectedNode n = (UndirectedNode) graph.getRandomNode();
			if (removed.contains(n)) {
				continue;
			}
			removed.add(n);
			batch.add(new NodeRemoval<UndirectedEdge>(n));
		}
		return batch;
	}

	@Override
	public void reset() {
	}
}
