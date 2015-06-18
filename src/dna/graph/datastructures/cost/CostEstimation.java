package dna.graph.datastructures.cost;

import dna.graph.datastructures.config.DSConfig;
import dna.graph.datastructures.config.DSConfigDirected;
import dna.graph.datastructures.config.DSConfigUndirected;
import dna.graph.datastructures.count.OperationCount;
import dna.graph.datastructures.count.OperationCounts;
import dna.graph.datastructures.count.OperationCountsDirected;
import dna.graph.datastructures.count.OperationCountsUndirected;
import dna.util.Log;

/**
 * 
 * methods for estimating the costs of datastructure operations / combinations
 * 
 * @author benni
 *
 */
public class CostEstimation {

	/**
	 * 
	 * the costs are estimated as the sum of operation count times its cost on
	 * the size of lists specified by oc
	 * 
	 * @param c
	 *            cost functions of a data structure
	 * @param oc
	 *            operation count
	 * @return estimation of the runtime costs for executing all operations from
	 *         operation count oc on datastructure ds specified by it costs c
	 */
	public static double estimateCosts(CostFunctions c, OperationCount oc) {
		double cost = 0;
		cost += c.INIT.getCost(oc.listSize) * oc.INIT;
		cost += c.ADD_SUCCESS.getCost(oc.listSize) * oc.ADD_SUCCESS;
		cost += c.ADD_FAILURE.getCost(oc.listSize) * oc.ADD_FAILURE;
		cost += c.RANDOM_ELEMENT.getCost(oc.listSize) * oc.RANDOM_ELEMENT;
		cost += c.SIZE.getCost(oc.listSize) * oc.SIZE;
		cost += c.ITERATE.getCost(oc.listSize) * oc.ITERATE;
		cost += c.CONTAINS_SUCCESS.getCost(oc.listSize) * oc.CONTAINS_SUCCESS;
		cost += c.CONTAINS_FAILURE.getCost(oc.listSize) * oc.CONTAINS_FAILURE;
		cost += c.GET_SUCCESS.getCost(oc.listSize) * oc.GET_SUCCESS;
		cost += c.GET_FAILURE.getCost(oc.listSize) * oc.GET_FAILURE;
		cost += c.REMOVE_SUCCESS.getCost(oc.listSize) * oc.REMOVE_SUCCESS;
		cost += c.REMOVE_FAILURE.getCost(oc.listSize) * oc.REMOVE_FAILURE;
		return cost;
	}

	public static double estimateCosts(CostFunctionsS c, OperationCount oc) {
		switch (oc.lt) {
		case GlobalEdgeList:
			return estimateCosts(c.edges, oc);
		case GlobalNodeList:
			return estimateCosts(c.nodes, oc);
		case LocalEdgeList:
			return estimateCosts(c.edges, oc);
		case LocalInEdgeList:
			return estimateCosts(c.edges, oc);
		case LocalNodeList:
			return estimateCosts(c.nodes, oc);
		case LocalOutEdgeList:
			return estimateCosts(c.edges, oc);
		default:
			Log.error("unknown list type " + oc.lt);
			return -1;
		}
	}

	public static double estimateCosts(DSConfig dsc, OperationCounts oc,
			CostFunctionsSMap map) {
		if (oc instanceof OperationCountsDirected) {
			return estimateCosts((DSConfigDirected) dsc,
					(OperationCountsDirected) oc, map);
		} else if (oc instanceof OperationCountsUndirected) {
			return estimateCosts((DSConfigUndirected) dsc,
					(OperationCountsUndirected) oc, map);
		} else {
			Log.error("unknown type of operation counts :" + oc.getClass());
			return -1;
		}
	}

	public static double estimateCosts(DSConfigDirected dsc,
			OperationCountsDirected oc, CostFunctionsSMap map) {
		double costs = 0;
		costs += estimateCosts(map.get(dsc.V), oc.V);
		costs += estimateCosts(map.get(dsc.E), oc.E);
		costs += estimateCosts(map.get(dsc.in), oc.in);
		costs += estimateCosts(map.get(dsc.out), oc.out);
		costs += estimateCosts(map.get(dsc.neighbors), oc.neighbors);
		return costs;
	}

	public static double estimateCosts(DSConfigUndirected dsc,
			OperationCountsUndirected oc, CostFunctionsSMap map) {
		double costs = 0;
		costs += estimateCosts(map.get(dsc.V), oc.V);
		costs += estimateCosts(map.get(dsc.E), oc.E);
		costs += estimateCosts(map.get(dsc.adj), oc.adj);
		return costs;
	}

	/**
	 * 
	 * estimation is computed as the cost of initializing oc.listCount many
	 * lists of size oc.listSize, iterating over oc.listSize many lists of size
	 * oc.listSize and adding oc.listSize single elements
	 * 
	 * @param from
	 *            datastructure to swap from
	 * @param oc
	 *            operation count of the considered list type, only required for
	 *            list size and count
	 * @param to
	 *            datastructure to swap to
	 * @return estimation of the runtime costs for exchanging datastructure for
	 *         operation count oc from datastructure from to datastructure to
	 */
	public static double estimateSwapCosts(CostFunctions from,
			OperationCount oc, CostFunctions to) {
		double cost = 0;
		cost += oc.listCount * to.INIT.getCost(oc.listSize);
		cost += oc.listCount * from.ITERATE.getCost(oc.listSize);
		for (int i = 0; i < oc.listSize; i++) {
			cost += oc.listCount * to.ADD_SUCCESS.getCost(i);
		}
		return cost;
	}

	public static double estimateSwapCosts(DSConfig from, DSConfig to,
			OperationCounts ocs, CostFunctionsSMap map) {
		if (ocs instanceof OperationCountsDirected) {
			return estimateSwapCosts((DSConfigDirected) from,
					(DSConfigDirected) to, (OperationCountsDirected) ocs, map);
		} else {
			return estimateSwapCosts((DSConfigUndirected) from,
					(DSConfigUndirected) to, (OperationCountsUndirected) ocs,
					map);
		}
	}

	public static double estimateSwapCosts(DSConfigDirected from,
			DSConfigDirected to, OperationCountsDirected ocs,
			CostFunctionsSMap map) {
		double cost = 0;

		if (!from.V.equals(to.V))
			cost += estimateSwapCosts(map.get(from.V).nodes, ocs.V,
					map.get(to.V).nodes);
		if (!from.E.equals(to.E))
			cost += estimateSwapCosts(map.get(from.E).edges, ocs.E,
					map.get(to.E).edges);
		if (!from.in.equals(to.in))
			cost += estimateSwapCosts(map.get(from.in).edges, ocs.in,
					map.get(to.in).edges);
		if (!from.out.equals(to.out))
			cost += estimateSwapCosts(map.get(from.out).edges, ocs.out,
					map.get(to.out).edges);
		if (!from.neighbors.equals(to.neighbors))
			cost += estimateSwapCosts(map.get(from.neighbors).nodes,
					ocs.neighbors, map.get(to.neighbors).nodes);

		return cost;
	}

	public static double estimateSwapCosts(DSConfigUndirected from,
			DSConfigUndirected to, OperationCountsUndirected ocs,
			CostFunctionsSMap map) {
		double cost = 0;

		if (!from.V.equals(to.V))
			cost += estimateSwapCosts(map.get(from.V).nodes, ocs.V,
					map.get(to.V).nodes);
		if (!from.E.equals(to.E))
			cost += estimateSwapCosts(map.get(from.E).edges, ocs.E,
					map.get(to.E).edges);
		if (!from.adj.equals(to.adj))
			cost += estimateSwapCosts(map.get(from.adj).edges, ocs.adj,
					map.get(to.adj).edges);

		return cost;
	}
}
