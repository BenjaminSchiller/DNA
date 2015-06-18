package dna.graph.datastructures.recommendation;

import java.util.Comparator;

import dna.graph.datastructures.cost.CostEstimation;
import dna.graph.datastructures.cost.CostFunctions;
import dna.graph.datastructures.count.OperationCount;

public class HotswapComparator implements Comparator<CostFunctions> {

	private CostFunctions current;
	private OperationCount oc;
	private int batches;
	private int amortization;

	public HotswapComparator(CostFunctions current, OperationCount oc,
			int batches, int amortization) {
		this.current = current;
		this.oc = oc;
		this.batches = batches;
		this.amortization = amortization;
	}

	@Override
	public int compare(CostFunctions c1, CostFunctions c2) {
		double diff = this.estimateAll(c1) - this.estimateAll(c2);
		if (diff < 0) {
			return -1;
		} else if (diff == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	private double estimateAll(CostFunctions c) {
		double cost = 0;
		cost += this.estimateSwap(c);
		cost += CostEstimation.estimateCosts(c, oc) * amortization / batches;
		// cost += CostEstimation.estimateCosts(c, oc);
		return cost;
	}

	private double estimateSwap(CostFunctions c) {
		if (c.ds.equals(current.ds)) {
			return 0;
		}
		double cost = 0;
		cost += this.estimateInitialization(c);
		cost += this.estimateIteration();
		cost += this.estimateFill(c);
		return cost;
	}

	private double estimateInitialization(CostFunctions c) {
		return this.oc.listCount * c.INIT.getCost(oc.listSize);
	}

	private double estimateIteration() {
		return this.oc.listCount * this.current.ITERATE.getCost(oc.listSize);
	}

	private double estimateFill(CostFunctions c) {
		double cost = 0;
		for (int i = 1; i <= oc.listSize; i++) {
			cost += c.ADD_SUCCESS.getCost(i);
		}
		return cost;
	}
}
