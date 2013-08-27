package dna.updates.directed;

import dna.datastructures.GraphDataStructure;
import dna.graph.Graph;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
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
public class RandomDirectedNodeAdditions extends DirectedBatchGenerator {

	private int additions;

	/**
	 * 
	 * @param additions
	 *            number of nodes to add with each batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomDirectedNodeAdditions(int additions, GraphDataStructure datastructures) {
		super("randomDirectedNodeAdditions", new IntParameter("additions", additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<DirectedEdge> generate(Graph graph) {
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(this.ds, graph.getTimestamp(), graph.getTimestamp() + 1,
				this.additions, 0, 0, 0, 0, 0);
		int index = graph.getMaxNodeIndex() + 1;
		while (batch.getSize() < this.additions) {
			DirectedNode n = (DirectedNode) this.ds.newNodeInstance(index);
			if (!graph.containsNode(n)) {
				batch.add(new NodeAddition<DirectedEdge>(n));
			}
			index++;
		}
		return batch;
	}

	@Override
	public void reset() {
	}

}
