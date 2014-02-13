package dna.graph.startNodeSelection;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * @author Benedict
 * 
 */
public class HighestRandomDegreeSumSelection extends StartNodeSelectionStrategy {

	private int n;
	private int m;

	/**
	 * 
	 * @param g
	 * @param numberOfNodes
	 * @param numberOfNeighbors
	 */
	public HighestRandomDegreeSumSelection(Graph g, int numberOfNodes,
			int numberOfNeighbors) {
		super(g);

		this.n = numberOfNodes;
		this.m = numberOfNeighbors;
	}

	@Override
	public Node getStartNode() {

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
	public int resourceCost() {
		return n * m;
	}

}
