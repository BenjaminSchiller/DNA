package dna.graph.datastructures.config;

import java.util.Comparator;

import dna.graph.datastructures.cost.CostEstimation;
import dna.graph.datastructures.cost.CostFunctionsSMap;
import dna.graph.datastructures.count.OperationCounts;

/**
 * 
 * Comparator for sorting DSCOnfig objects based on an estimation of their costs
 * for executing an OperationCounts. As parameters, such an OperationCounts with
 * operation counts for V, E, ... is supplied as well as a mapping of
 * IDataStructure classes to cost functions.
 * 
 * @author benni
 *
 */
public class DSConfigComparator implements Comparator<DSConfig> {

	private OperationCounts ocs;
	private CostFunctionsSMap map;

	public DSConfigComparator(OperationCounts ocs, CostFunctionsSMap map) {
		this.ocs = ocs;
		this.map = map;
	}

	@Override
	public int compare(DSConfig dsc1, DSConfig dsc2) {
		double diff = CostEstimation.estimateCosts(dsc1, ocs, map)
				- CostEstimation.estimateCosts(dsc2, ocs, map);
		if (diff < 0) {
			return -1;
		} else if (diff == 0) {
			return 0;
		} else {
			return 1;
		}
	}

}
