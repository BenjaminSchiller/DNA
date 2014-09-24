package dna.updates.samplingAlgorithms.startNodeSelection;

import java.util.Arrays;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.series.data.NodeValueList;

/**
 * Selects a start node based on values of a NodeValueList
 * 
 * @author Benedict Jahn
 * 
 */
public class NodeValueListSelection extends StartNodeSelectionStrategy {

	private double[] nvl;
	private NodeValueListOrderBy nvlOrder;

	/**
	 * Creates an instance of the NodeValueList based start node selection
	 * strategy
	 * 
	 * @param g
	 *            the graph from which the start node is selected
	 * @param nvl
	 *            the NodeValueList of a specific metric for the graph g
	 * @param nvlOrder
	 *            the ordering on which the selection of the start node is done
	 */
	public NodeValueListSelection(Graph g, NodeValueList nvl,
			NodeValueListOrderBy nvlOrder) {
		super(g);
		this.nvl = nvl.getValues();
		this.nvlOrder = nvlOrder;
		if (nvlOrder != NodeValueListOrderBy.median
				&& nvlOrder != NodeValueListOrderBy.minimum
				&& nvlOrder != NodeValueListOrderBy.maximum) {
			throw new IllegalArgumentException(
					"The NodeValueList based start node selection only accepts lists sorted by minimum, maximum or average.");
		}
	}

	@Override
	public Node getStartNode() {
		if (nvlOrder == NodeValueListOrderBy.median) {
			return selectMedian();
		} else if (nvlOrder == NodeValueListOrderBy.maximum) {
			return selectMaximum();
		} else {
			return selectMinimum();
		}
	}

	@Override
	public int resourceCost() {
		return nvl.length;
	}

	/**
	 * Selects the node with the median value of the NodeValueList
	 */
	private Node selectMedian() {
		int index = 0;
		double[] tempArr = new double[nvl.length];
		for (int i = 0; i < nvl.length; i++) {
			tempArr[i] = nvl[i];
		}

		Arrays.sort(tempArr);
		double median = tempArr[nvl.length / 2];
		for (int i = 0; i < nvl.length; i++) {
			if (nvl[i] == median) {
				index = i;
				break;
			}
		}

		return g.getNode(index);
	}

	/**
	 * Selects the node with the maximal value of the NodeValueList
	 */
	private Node selectMaximum() {
		double tempVal = Double.MIN_VALUE;
		int index = 0;
		for (int i = 0; i < nvl.length; i++) {
			if (tempVal < nvl[i]) {
				tempVal = nvl[i];
				index = i;
			}
		}
		return g.getNode(index);
	}

	/**
	 * Selects the node with the minimal value of the NodeValueList
	 */
	private Node selectMinimum() {
		double tempVal = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < nvl.length; i++) {
			if (tempVal > nvl[i]) {
				tempVal = nvl[i];
				index = i;
			}
		}
		return g.getNode(index);
	}

}
