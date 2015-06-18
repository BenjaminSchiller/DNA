package dna.graph.datastructures.recommendation;

import java.util.Comparator;

import dna.graph.datastructures.cost.CostEstimation;
import dna.graph.datastructures.cost.CostFunctionsS;
import dna.graph.datastructures.count.OperationCount;

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
public class CostFunctionsSComparator implements Comparator<CostFunctionsS> {

	private OperationCount oc;

	public CostFunctionsSComparator(OperationCount oc) {
		this.oc = oc;
	}

	@Override
	public int compare(CostFunctionsS c1, CostFunctionsS c2) {
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
