package dna.updates.directed;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.util.parameters.IntParameter;

/**
 * 
 * batch generator for random edge additions. edges are added uniformly at
 * random from all possible (non-existing) edges in the graph. no edge is added
 * twice.
 * 
 * @author benni
 * 
 */
public class RandomDirectedEdgeAdditions extends DirectedBatchGenerator {

	private int additions;

	/**
	 * 
	 * @param additions
	 *            number of random edges to add per batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomDirectedEdgeAdditions(int additions, GraphDataStructure datastructures) {
		super("randomDirectedEdgeAdditions", new IntParameter("additions", additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<DirectedEdge> generate(Graph graph) {
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(this.ds, graph.getTimestamp(), graph.getTimestamp() + 1, 0,
				0, 0, this.additions, 0, 0);
		HashSet<DirectedEdge> added = new HashSet<DirectedEdge>(this.additions);
		while (batch.getSize() < this.additions) {
			DirectedNode n1 = (DirectedNode) graph.getRandomNode();
			DirectedNode n2 = (DirectedNode) graph.getRandomNode();
			if (n1.equals(n2)) {
				continue;
			}
			DirectedEdge e = (DirectedEdge) this.ds.newEdgeInstance(n1, n2);
			if (graph.containsEdge(e) || added.contains(e)) {
				continue;
			}
			added.add(e);
			batch.add(new EdgeAddition<DirectedEdge>(e));
		}

		return batch;
	}

	@Override
	public void reset() {
	}

}
