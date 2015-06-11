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
public class CostFunction {

	public Class<? extends IDataStructure> ds;
	public Class<? extends IElement> dt;
	public Operation o;
	public double[] runtimes;
	public int stepSize;

	/**
	 * 
	 * @param ds
	 *            datastructure
	 * @param dt
	 *            data type
	 * @param o
	 *            operation
	 * @param values
	 *            aggregated values of runtime measurements
	 */
	public CostFunction(Class<? extends IDataStructure> ds,
			Class<? extends IElement> dt, Operation o,
			ArrayList<AggregatedValue> values) {
		this.ds = ds;
		this.dt = dt;
		this.o = o;
		this.runtimes = new double[values.size()];
		this.stepSize = Integer.parseInt(values.get(0).getName());
		for (int i = 0; i < values.size(); i++) {
			// this.runtimes[i] = values.get(i).getAvg();
			this.runtimes[i] = values.get(i).getMedian();
		}
	}

	/**
	 * 
	 * @param mainDataDir
	 *            directory (with ending /) where all results are stored
	 * @param ds
	 *            datastructure
	 * @param o
	 *            operation
	 * @return cost function fo specified ds and o, values for estimation read
	 *         from runtime measurements
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static CostFunction read(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			Operation o) throws NumberFormatException, IOException {
		return new CostFunction(ds, dt, o, RuntimeMeasurement.read(mainDataDir,
				ds, dt, o));
	}

	/**
	 * 
	 * @param size
	 *            list size
	 * @return estimated runtime of executing an operation o in a
	 */
	public double getCost(int size) {
		if (size == 0) {
			return 0;
		}
		int floor = (int) Math.floor((double) size / stepSize) - 1;
		int ceil = (int) Math.ceil((double) size / stepSize) - 1;
		int remainder = size % stepSize;

		if (ceil >= runtimes.length || floor >= runtimes.length) {
			return runtimes[runtimes.length - 1];
		}
		if (floor < 0) {
			return 0 + remainder * runtimes[ceil] / stepSize;
		}

		return runtimes[floor] + remainder * (runtimes[ceil] - runtimes[floor])
				/ stepSize;
	}

	public String toString() {
		return this.ds.getSimpleName() + "(" + this.o.toString() + "): "
				+ this.runtimes.length + " x " + this.stepSize + " (max="
				+ ArrayUtils.max(this.runtimes) + ")";
	}
}
