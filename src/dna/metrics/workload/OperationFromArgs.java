package dna.metrics.workload;

import dna.metrics.algorithms.IRecomputation;
import dna.metrics.workload.Operation.ListType;
import dna.metrics.workload.operations.AddSuccess;
import dna.metrics.workload.operations.BFS;
import dna.metrics.workload.operations.ContainsFailure;
import dna.metrics.workload.operations.ContainsSuccess;
import dna.metrics.workload.operations.DFS;
import dna.metrics.workload.operations.GetFailure;
import dna.metrics.workload.operations.GetSuccess;
import dna.metrics.workload.operations.Iterate;
import dna.metrics.workload.operations.MetricComputation;
import dna.metrics.workload.operations.RandomElement;
import dna.util.fromArgs.MetricFromArgs;
import dna.util.fromArgs.MetricFromArgs.MetricType;

public class OperationFromArgs {
	public static enum OperationType {
		AddSuccess, BFS, ContainsFailure, ContainsSuccess, DFS, GetFailure, GetSuccess, Iterate, MetricComputation, RandomElement
	};

	public static Operation parse(OperationType operationType, ListType list,
			int times, String[] args) {
		switch (operationType) {
		case AddSuccess:
			return new AddSuccess(list, times);
		case BFS:
			return new BFS(times, Integer.parseInt(args[0]));
		case ContainsFailure:
			return new ContainsFailure(list, times);
		case ContainsSuccess:
			return new ContainsSuccess(list, times, Integer.parseInt(args[0]));
		case DFS:
			return new DFS(times, Integer.parseInt(args[0]));
		case GetFailure:
			return new GetFailure(list, times);
		case GetSuccess:
			return new GetSuccess(list, times, Integer.parseInt(args[0]));
		case Iterate:
			return new Iterate(list, times);
		case MetricComputation:
			String[] args2 = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				args2[i - 1] = args[i];
			}
			return new MetricComputation(times,
					(IRecomputation) MetricFromArgs.parse(
							MetricType.valueOf(args[0]), args2));
		case RandomElement:
			return new RandomElement(list, times);
		default:
			throw new IllegalArgumentException("invalid operation type: "
					+ operationType);
		}
	}
}
