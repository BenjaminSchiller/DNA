package dna.metrics.workload.operations;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.workload.Operation;

/**
 * operation to iterate over all elements of a list
 * 
 * @author benni
 *
 */
public class Iterate extends Operation {

	/**
	 * 
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions of this operation per execution
	 */
	public Iterate(ListType list, int times) {
		super("Iterate", list, times);
	}

	@SuppressWarnings("unused")
	@Override
	protected void createWorkloadE(Graph g) {
		Edge edge = null;
		for (IElement e : g.getEdges()) {
			edge = (Edge) e;
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void createWorkloadV(Graph g) {
		Node node = null;
		for (IElement n : g.getNodes()) {
			node = (Node) n;
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void createWorkloadIn(Graph g) {
		DirectedEdge edge = null;
		for (IElement n_ : g.getNodes()) {
			for (IElement e_ : ((DirectedNode) n_).getIncomingEdges()) {
				edge = (DirectedEdge) e_;
			}
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void createWorkloadOut(Graph g) {
		DirectedEdge edge = null;
		for (IElement n_ : g.getNodes()) {
			for (IElement e_ : ((DirectedNode) n_).getOutgoingEdges()) {
				edge = (DirectedEdge) e_;
			}
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void createWorkloadNeighbors(Graph g) {
		DirectedNode node = null;
		for (IElement n_ : g.getNodes()) {
			for (IElement nn_ : ((DirectedNode) n_).getNeighbors()) {
				node = (DirectedNode) nn_;
			}
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void createWorkloadAdj(Graph g) {
		Edge edge = null;
		for (IElement n_ : g.getNodes()) {
			for (IElement e_ : ((Node) n_).getEdges()) {
				edge = (Edge) e_;
			}
		}
	}

}
