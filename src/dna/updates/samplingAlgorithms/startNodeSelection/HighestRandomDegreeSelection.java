package dna.updates.samplingAlgorithms.startNodeSelection;

import dna.graph.Graph;
import dna.graph.nodes.Node;

/**
 * Selects n random nodes and chooses the one with the highest degree out of
 * them to be the start node.
 * 
 * @author Benedict Jahn
 * 
 */
public class HighestRandomDegreeSelection extends StartNodeSelectionStrategy {

	private int n;

	/**
	 * Initializes the HighestDegreeSelection start node selection strategy
	 * 
	 * @param n
	 *            The number of random nodes, from which the strategy will
	 *            choose a start node
	 */
	public HighestRandomDegreeSelection(int n) {
		super();
		this.n = n;
	}

	@Override
	public Node getStartNode(Graph g) {

		int maxDegree = 0;

		Node resultNode = null;

		for (int i = 0; i < n; i++) {

			Node tempNode = g.getRandomNode();
			int tempDegree = getDegreeFromNode(tempNode);

			if (tempDegree > maxDegree) {
				resultNode = tempNode;
				maxDegree = tempDegree;
			}
		}

		return resultNode;
	}

	@Override
	public int resourceCost(Graph g) {
		return n;
	}

}
