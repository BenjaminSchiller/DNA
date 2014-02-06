package dna.graph.startNodeSelection;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Selects n random nodes and chooses the one with the highest degree out of
 * them to be the start node.
 * 
 * @author Benedict
 * 
 */
public class HighestRandomDegreeSelection extends StartNodeSelectionStrategy {

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
	public HighestRandomDegreeSelection(Graph g, int n) {
		super(g);
		this.n = n;
	}

	@Override
	public Node getStartNode() {

		int maxNodeID = g.getMaxNodeIndex();
		int maxDegree = 0;

		Node resultNode = null;

		for (int i = 0; i < n; i++) {

			Node tempNode = g.getNode(Rand.rand.nextInt(maxNodeID));
			int tempDegree = getDegreeFromNode(tempNode);

			if (tempDegree > maxDegree) {
				resultNode = tempNode;
				maxDegree = tempDegree;
			}
		}

		return resultNode;
	}

	@Override
	public int resourceCost() {
		return n;
	}

}
