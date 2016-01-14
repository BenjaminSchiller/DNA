package dna.graph.datastructures.cost;

import java.io.IOException;
import java.util.ArrayList;

import dna.graph.IElement;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.count.OperationCount.Operation;
import dna.series.aggdata.AggregatedValue;
import dna.util.ArrayUtils;

/**
 * 
 * cost function that estimates the cost of executing a specific operation o on
 * a datastructure ds of given size with elements of data type dt. hence, a cost
 * function c_op(s) for an operation op maps each list size to a cost (expected
 * runtime).
 * 
 * measurements are read for sizes of stepSize*k and for those, the average
 * value of all repetitions is assumed. for sizes between available sizes, the
 * cost values are approximated assuming linear growth between the two points.
 * 
 * @author benni
 *
 */
public abstract class CostFunction {
	public abstract double getCost(int size);

	public static CostFunction read() {
		return null;
	}
}
