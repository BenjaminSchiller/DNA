package dna.graph.startNodeSelection;

import java.util.Collection;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;

/**
 * @author Benedict
 * 
 */
public class HighestDegreeSelection extends StartNodeSelectionStrategy {

	/**
	 * 
	 * @param g
	 */
	public HighestDegreeSelection(Graph g) {
		super(g);
	}

	@Override
	public Node getStartNode() {
		Collection<IElement> nodeList = g.getNodes();
		Node startNode = null;
		int maxDegree = 0;

		for (IElement n : nodeList) {
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
		// TODO Realistisch? oder eher 0?
		// return graph.getNodeCount();
		return 0;
	}

}
