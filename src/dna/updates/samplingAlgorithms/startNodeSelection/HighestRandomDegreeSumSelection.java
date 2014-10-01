package dna.updates.samplingAlgorithms.startNodeSelection;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Selects n random nodes and chooses m random neighbors of these nodes. The
 * node in n with the highest degree sum (degrees of n and all m neighbors
 * summed up) is the new start node
 * 
 * @author Benedict Jahn
 * 
 */
public class HighestRandomDegreeSumSelection extends StartNodeSelectionStrategy {

	private int n;
	private int m;

	/**
	 * Creates an instance of the highest random degree sum start node selection
	 * strategy
	 * 
	 * @param numberOfNodes
	 *            the number of nodes we will randomly choose from the graph
	 * @param numberOfNeighbors
	 *            the number of random neighbors of the randomly chosen nodes
	 *            which will be considered for the degree sum
	 */
	public HighestRandomDegreeSumSelection(int numberOfNodes,
			int numberOfNeighbors) {
		super();

		this.n = numberOfNodes;
		this.m = numberOfNeighbors;
	}

	@Override
	public Node getStartNode(Graph g) {

		int maxDegreeSum = 0;

		Node resultNode = null;

		for (int i = 0; i < n; i++) {

			Node tempNode = g.getRandomNode();

			int tempDegreeSum = getDegreeFromNode(tempNode);

			ArrayList<Node> neighbors = getNeighbors(tempNode);

			int neighborCount = neighbors.size();

			int steps = m;
			if (neighborCount < m) {
				steps = neighborCount;
			}

			for (int j = 0; j < steps; j++) {

				tempDegreeSum += getDegreeFromNode(neighbors.get(Rand.rand
						.nextInt(neighborCount)));

			}

			if (tempDegreeSum > maxDegreeSum) {
				resultNode = tempNode;
				maxDegreeSum = tempDegreeSum;
			}
		}

		return resultNode;
	}

	@Override
	public int resourceCost(Graph g) {
		return n * m;
	}

}
