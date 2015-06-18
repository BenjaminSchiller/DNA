package dna.graph.datastructures.recommendation;

import java.util.Comparator;

import dna.graph.datastructures.cost.CostEstimation;
import dna.graph.datastructures.cost.CostFunctions;
import dna.graph.datastructures.count.OperationCount;
import dna.graph.datastructures.count.OperationCounts;
import dna.util.Log;

/**
 * 
 * Comparator for sorting datastructure costs that represent the cost function
 * for all operations of a datastructure. the comparator takes an operation
 * count. the order is then based on the estimated cost of a data structure for
 * executing the given operation count.
 * 
 * @author benni
 *
 */
public class CostFunctionsComparator implements Comparator<CostFunctions> {

	private OperationCount oc;

	public CostFunctionsComparator(OperationCount oc) {
		this.oc = oc;
	}

	@Override
	public int compare(CostFunctions c1, CostFunctions c2) {
		double diff = CostEstimation.estimateCosts(c1, oc)
				- CostEstimation.estimateCosts(c2, oc);
		if (diff < 0) {
			return -1;
		} else if (diff == 0) {
			return 0;
		} else {
			return 1;
		}
	}

}
