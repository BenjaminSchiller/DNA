package dna.updates.samplingAlgorithms.startNodeSelection;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;

/**
 * This start node selection strategy selects the node with the highest degree
 * in the graph
 * 
 * @author Benedict Jahn
 * 
 */
public class HighestDegreeSelection extends StartNodeSelectionStrategy {

	/**
	 * Creates an instance of the highest degree selection
	 * 
	 * @param g
	 *            the graph from which we choose the start node
	 */
	public HighestDegreeSelection(Graph g) {
		super(g);
	}

	@Override
	public Node getStartNode() {
		Node startNode = null;
		int maxDegree = 0;

		for (IElement n : g.getNodes()) {
			int tempDegree = getDegreeFromNode((Node) n);

			if (tempDegree > maxDegree) {
				startNode = (Node) n;
				maxDegree = tempDegree;
			}

		}
		return startNode;
	}

	@Override
	public int resourceCost() {
		return g.getNodeCount();
	}

}
