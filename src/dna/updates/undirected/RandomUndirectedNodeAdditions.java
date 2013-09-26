package dna.updates.undirected;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.Batch;
import dna.updates.NodeAddition;
import dna.util.parameters.IntParameter;

/**
 * 
 * batch generator for random node additions. new nodes are added starting with
 * the current largest node index. no node is added twice.
 * 
 * @author benni
 * 
 */
public class RandomUndirectedNodeAdditions extends UndirectedBatchGenerator {

	private int additions;

	/**
	 * 
	 * @param additions
	 *            number of nodes to add with each batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomUndirectedNodeAdditions(int additions, GraphDataStructure datastructures) {
		super("randomUndirectedNodeAdditions", new IntParameter("additions", additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<UndirectedEdge> generate(Graph graph) {
		Batch<UndirectedEdge> batch = new Batch<UndirectedEdge>(this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, this.additions, 0, 0, 0, 0, 0);
		int index = graph.getMaxNodeIndex() + 1;
		while (batch.getSize() < this.additions) {
			UndirectedNode n = (UndirectedNode) this.ds.newNodeInstance(index);
			if (!graph.containsNode(n)) {
				batch.add(new NodeAddition<UndirectedEdge>(n));
			}
			index++;
		}
		return batch;
	}

	@Override
	public void reset() {
	}

}
