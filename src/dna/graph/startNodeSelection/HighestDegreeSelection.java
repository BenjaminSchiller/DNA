package dna.graph.startNodeSelection;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.Log;
import dna.util.Rand;

/**
 * Selects n random nodes and chooses the one with the highest degree out of
 * them to be the start node.
 * 
 * @author Benedict
 * 
 */
public class HighestDegreeSelection implements StartNodeSelectionStrategy {

	private Graph g;
	private int n;

	/**
	 * Initializes the HighestDegreeSelection start node selection strategy.
	 * 
	 * @param g
	 *            The graph from which the start node is selected
	 * @param n
	 *            The number of random nodes, from which the strategy will
	 *            choose a start node
	 */
	public HighestDegreeSelection(Graph g, int n) {
		this.n = n;
		this.g = g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dna.graph.startNodeSelection.StartNodeSelectionStrategy#getStartNode()
	 */
	@Override
	public Node getStartNode() {

		int maxNodeID = g.getNodeCount();
		int maxDegree = 0;

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {

			DirectedNode resultNode = null;

			for (int i = 0; i < n; i++) {

				DirectedNode tempNode = (DirectedNode) g.getNode(Rand.rand
						.nextInt(maxNodeID));
				int tempDegree = tempNode.getOutDegree();

				if (tempDegree > maxDegree) {
					resultNode = tempNode;
					maxDegree = tempDegree;
				}
			}

			return resultNode;

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			UndirectedNode resultNode = null;

			for (int i = 0; i < n; i++) {

				UndirectedNode tempNode = (UndirectedNode) g.getNode(Rand.rand
						.nextInt(maxNodeID));
				int tempDegree = tempNode.getDegree();

				if (tempDegree > maxDegree) {
					resultNode = tempNode;
					maxDegree = tempDegree;
				}
			}

			return resultNode;

		} else {
			Log.error("DD - unsupported node type "
					+ this.g.getGraphDatastructures().getNodeType());
			return null;
		}

	}

	@Override
	public int resourceCost() {
		return n;
	}

}
