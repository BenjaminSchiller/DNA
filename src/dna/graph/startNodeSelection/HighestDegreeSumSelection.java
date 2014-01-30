package dna.graph.startNodeSelection;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.Log;
import dna.util.NodeUtils;
import dna.util.Rand;

/**
 * @author Benedict
 * 
 */
public class HighestDegreeSumSelection implements StartNodeSelectionStrategy {

	private Graph g;
	private int n;
	private int m;

	/**
	 * 
	 * @param g
	 * @param numberOfNodes
	 * @param numberOfNeighbors
	 */
	public HighestDegreeSumSelection(Graph g, int numberOfNodes,
			int numberOfNeighbors) {

		this.g = g;
		this.n = numberOfNodes;
		this.m = numberOfNeighbors;
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
		int maxDegreeSum = 0;

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {

			DirectedNode resultNode = null;

			for (int i = 0; i < n; i++) {

				DirectedNode tempNode = (DirectedNode) g.getNode(Rand.rand
						.nextInt(maxNodeID));

				int tempDegreeSum = tempNode.getOutDegree();

				ArrayList<DirectedNode> neighbors = NodeUtils
						.getNeighbors(tempNode);
				int neighborCount = neighbors.size();

				for (int j = 0; j < m; j++) {

					tempDegreeSum += neighbors.get(
							Rand.rand.nextInt(neighborCount)).getDegree();

				}

				if (tempDegreeSum > maxDegreeSum) {
					resultNode = tempNode;
					maxDegreeSum = tempDegreeSum;
				}
			}

			return resultNode;

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			UndirectedNode resultNode = null;

			for (int i = 0; i < n; i++) {

				UndirectedNode tempNode = (UndirectedNode) g.getNode(Rand.rand
						.nextInt(maxNodeID));

				int tempDegreeSum = tempNode.getDegree();

				ArrayList<UndirectedNode> neighbors = NodeUtils
						.getNeighbors(tempNode);
				int neighborCount = neighbors.size();

				for (int j = 0; j < m; j++) {

					tempDegreeSum += neighbors.get(
							Rand.rand.nextInt(neighborCount)).getDegree();

				}

				if (tempDegreeSum > maxDegreeSum) {
					resultNode = tempNode;
					maxDegreeSum = tempDegreeSum;
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
		return n * m;
	}

}
