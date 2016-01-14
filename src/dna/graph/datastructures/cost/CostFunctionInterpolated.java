package dna.graph.datastructures.cost;

import java.io.IOException;
import java.util.ArrayList;

import dna.graph.IElement;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.count.OperationCount.Operation;
import dna.series.aggdata.AggregatedValue;
import dna.util.ArrayUtils;

public class CostFunctionInterpolated {
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
	public CostFunctionInterpolated(Class<? extends IDataStructure> ds,
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
		return new CostFunctionFitted(ds, dt, o);
		// return new CostFunction(ds, dt, o,
		// RuntimeMeasurement.read(mainDataDir,
		// ds, dt, o));
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
		int index = this.getIndex(size);
		double fraction = this.getFraction(size);
		if (fraction > 0) {
			return this.runtimes[index] + fraction
					* (this.runtimes[index + 1] - this.runtimes[index]);
		} else {
			return this.runtimes[index];
		}
	}

	private int getIndex(int size) {
		if (size <= 100) {
			return size - 1;
		} else if (size <= 1000) {
			return 100 + (int) Math.floor((size - 100) / 10) - 1;
		} else if (size <= 10000) {
			return 190 + (int) Math.floor((size - 1000) / 100) - 1;
		} else if (size <= 100000) {
			return 280 + (int) Math.floor((size - 10000) / 1000) - 1;
		} else if (size <= 1000000) {
			return 370 + (int) Math.floor((size - 100000) / 10000) - 1;
		} else if (size <= 10000000) {
			return 460 + (int) Math.floor((size - 1000000) / 100000) - 1;
		} else {
			return -1;
		}
	}

	private double getFraction(int size) {
		if (size <= 100) {
			return (double) 0;
		} else if (size <= 1000) {
			return (double) ((size - 100) % 10) / 10;
		} else if (size <= 10000) {
			return (double) ((size - 1000) % 100) / 100;
		} else if (size <= 100000) {
			return (double) ((size - 10000) % 1000) / 1000;
		} else if (size <= 1000000) {
			return (double) ((size - 100000) % 10000) / 10000;
		} else if (size <= 10000000) {
			return (double) ((size - 1000000) % 100000) / 100000;
		} else {
			return -1;
		}
	}

	private double getCostComplete(int size) {
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
