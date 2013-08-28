package dna.updates.undirected;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
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
public class RandomUndirectedEdgeAdditions extends UndirectedBatchGenerator {

	private int additions;

	/**
	 * 
	 * @param additions
	 *            number of random edges to add per batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomUndirectedEdgeAdditions(int additions, GraphDataStructure datastructures) {
		super("randomUndirectedEdgeAdditions", new IntParameter("additions", additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<UndirectedEdge> generate(Graph graph) {
		Batch<UndirectedEdge> batch = new Batch<UndirectedEdge>(this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, 0, 0, 0, this.additions, 0, 0);
		HashSet<UndirectedEdge> added = new HashSet<UndirectedEdge>(this.additions);
		while (batch.getSize() < this.additions) {
			UndirectedNode n1 = (UndirectedNode) graph.getRandomNode();
			UndirectedNode n2 = (UndirectedNode) graph.getRandomNode();
			if (n1.equals(n2)) {
				continue;
			}
			UndirectedEdge e = (UndirectedEdge) this.ds.newEdgeInstance(n1, n2);
			if (graph.containsEdge(e) || added.contains(e)) {
				continue;
			}
			added.add(e);
			batch.add(new EdgeAddition<UndirectedEdge>(e));
		}

		return batch;
	}

	@Override
	public void reset() {
	}

}
