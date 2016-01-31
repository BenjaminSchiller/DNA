package dna.graph.datastructures.cost;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dna.graph.IElement;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.count.OperationCount.Operation;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Config;

public class CostFunctionFitted extends CostFunction {
	public static enum DataType {
		Node, Edge
	}

	public static enum FitType {
		Avg, Med, AvgSD, MedSD
	}

	public static FitType defaultFitType = FitType.AvgSD;
	public static int[] defaultSizes = new int[] { 100, 1000, 10000, 50000,
			100000 };

	private FitType ft;
	private DataType dt;
	private Class<? extends IDataStructure> ds;
	private Operation op;
	private int[] sizes;
	private String[] functions;

	public static DataType getDataType(Class<? extends IElement> dt) {
		if (Node.class.isAssignableFrom(dt)) {
			return DataType.Node;
		} else if (Edge.class.isAssignableFrom(dt)) {
			return DataType.Edge;
		} else {
			throw new IllegalArgumentException("unknowon data type: "
					+ dt.getSimpleName());
		}
	}

	public CostFunctionFitted(Class<? extends IDataStructure> ds,
			Class<? extends IElement> dt, Operation o) {
		this(defaultFitType, getDataType(dt), ds, o, defaultSizes);
	}

	public CostFunctionFitted(FitType ft, DataType dt,
			Class<? extends IDataStructure> ds, Operation op, int[] sizes) {
		super();
		this.ft = ft;
		this.dt = dt;
		this.ds = ds;
		this.op = op;
		this.sizes = sizes;
		this.functions = new String[this.sizes.length];
		int count = 0;
		for (int i = 0; i < this.sizes.length; i++) {
			String key = this.ft + "_" + this.dt + "_"
					+ this.ds.getSimpleName() + "_" + this.op + "_"
					+ this.sizes[i];
			if (Config.containsKey(key)) {
				this.functions[i] = Config.get(key);
				if (this.functions[i].equals("")) {
					// System.out.println("null: " + i + " @ " + this.sizes[i]);
					this.functions[i] = null;
				} else {
					count++;
				}
			} else {
				this.functions[i] = null;
				System.out.println("not found: " + i + " @ " + this.sizes[i]);
			}
		}
		if (count == 0) {
			this.sizes = new int[] { 1 };
			this.functions = new String[] { Integer.toString(Integer.MAX_VALUE) };
		} else if (count < this.functions.length - 1) {
			// System.out.println("shrinking to " + count + " entries: "
			// + this.toString());
			int[] tempSizes = new int[count];
			String[] tempFunctions = new String[count];
			int index = 0;
			for (int i = 0; i < this.sizes.length; i++) {
				if (this.functions[i] != null) {
					tempSizes[i] = this.sizes[i];
					tempFunctions[index] = this.functions[i];
					index++;
				}
			}
			this.sizes = tempSizes;
			this.functions = tempFunctions;
		}
		// System.out.println(sizes.length + " => " + this.sizes.length);
	}

	public String toString() {
		return this.ds.getSimpleName() + "-" + this.dt + "-" + this.op;
	}

	@Override
	public double getCost(int listSize) {
		// System.out.println(this.toString() + " for " + listSize);
		for (int i = 0; i < this.sizes.length - 1; i++) {
			if (listSize <= this.sizes[i]) {
				double v = this.evalFunction(i, listSize);
				// System.out.println("  => " + v + " @ " + i);
				return v;
			}
		}
		double v = this.evalFunction(this.functions.length - 1, listSize);
		// System.out.println("  => " + v + " @ last: " + (functions.length - 1)
		// + " vs. " + (sizes.length - 1) + " with "
		// + this.sizes[functions.length - 1] + " using "
		// + this.functions[this.functions.length - 1]);
		return v;
	}

	private double evalFunction(int index, int listSize) {
		try {
			return Math.max(eval(this.functions[index], listSize), 0);
		} catch (ScriptException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static ScriptEngine engine = (new ScriptEngineManager())
			.getEngineByName("js");

	public static double eval(String expr, double x) throws ScriptException {
		if (expr.contains("log(x)")) {
			expr = expr.replace("log(x)", Double.toString(Math.log(x)));
		}
		if (expr.contains("x**2")) {
			expr = expr.replace("x**2", "x*x");
		}
		if (expr.contains("x")) {
			expr = expr.replace("x", Double.toString(x));
		}
		return (double) engine.eval(expr);
	}
}
