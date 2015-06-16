package dna.graph.datastructures.hotswap;

import dna.graph.datastructures.count.Counting;
import dna.graph.datastructures.count.OperationCount.AggregationType;
import dna.graph.datastructures.count.OperationCounts;

public class Hotswap {
	
	protected static boolean enabled = false;

	public static void enable() {
		enabled = true;
	}

	public static void disable() {
		enabled = false;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static int amortizationRounds = 1;
	public static int pastBatchCount = 4;

	public static void check() {
		System.out.println("checking hotswap");
		if (Counting.batchApplication.size() < pastBatchCount) {
			System.out.println("exiting, still "
					+ (pastBatchCount - Counting.batchApplication.size())
					+ " rounds to go...");
			return;
		}
		OperationCounts ocs = Counting.addLastRounds(pastBatchCount,
				AggregationType.LAST);
		System.out.println(ocs);
	}

	public static void execute() {
		System.out.println("executing hotswap");
	}
}
