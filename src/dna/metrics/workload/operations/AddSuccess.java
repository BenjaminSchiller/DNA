package dna.metrics.workload.operations;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;

/**
 * 
 * adds a new node / edge to the graph that does not exist yet (hence
 * successful)
 * 
 * @author benni
 * 
 */
public class AddSuccess extends Operation {

	private Queue<Edge> newEdges;

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 */
	public AddSuccess(ListType list, int times) {
		super("AddSuccess", list, times);
	}

	@Override
	public void init(IGraph g) {
		if (ListType.E.equals(this.list)) {
			HashSet<Edge> newEdges = new HashSet<Edge>(this.times);
			while (newEdges.size() < this.times) {
				Node src = g.getRandomNode();
				Node dst = g.getRandomNode();
				Edge e = g.getGraphDatastructures().newEdgeInstance(src, dst);
				if (!g.containsEdge(e)) {
					newEdges.add(e);
				}
			}
			this.newEdges = new PriorityQueue<Edge>();
			for (Edge e : newEdges) {
				this.newEdges.add(e);
			}
		}
	}

	@Override
	protected void createWorkloadE(IGraph g) {
		g.addEdge(this.newEdges.poll());
	}

	@Override
	protected void createWorkloadV(IGraph g) {
		Node node = g.getGraphDatastructures().newNodeInstance(
				g.getMaxNodeIndex() + 1);
		g.addNode(node);
	}

}
