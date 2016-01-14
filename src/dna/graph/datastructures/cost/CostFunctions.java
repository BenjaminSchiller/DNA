package dna.graph.datastructures.cost;

import java.io.IOException;

import dna.graph.IElement;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.count.OperationCount.Operation;

/**
 * 
 * holds the cost functions for all operations of a datastructure for a specific
 * data type dt. the basis for each cost function are measurements performed for
 * different operations and sizes and are read from files.
 * 
 * @author benni
 *
 */
public class CostFunctions {

	public Class<? extends IDataStructure> ds = null;
	public Class<? extends IElement> dt = null;

	public CostFunction INIT = null;
	public CostFunction ADD_SUCCESS = null;
	public CostFunction ADD_FAILURE = null;
	public CostFunction RANDOM_ELEMENT = null;
	public CostFunction SIZE = null;
	public CostFunction ITERATE = null;
	public CostFunction CONTAINS_SUCCESS = null;
	public CostFunction CONTAINS_FAILURE = null;
	public CostFunction GET_SUCCESS = null;
	public CostFunction GET_FAILURE = null;
	public CostFunction REMOVE_SUCCESS = null;
	public CostFunction REMOVE_FAILURE = null;

	/**
	 * 
	 * @param ds
	 *            datastructure
	 */
	private CostFunctions(Class<? extends IDataStructure> ds,
			Class<? extends IElement> dt) {
		this.ds = ds;
		this.dt = dt;
	}

	/**
	 * 
	 * @param ds
	 *            datastructure
	 * @return all cost functions for a datastructure read from the runtime
	 *         measurements
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static CostFunctions read(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt)
			throws NumberFormatException, IOException {
		CostFunctions c = new CostFunctions(ds, dt);
		c.INIT = new CostFunctionFitted(ds, dt, Operation.INIT);

		c.ADD_SUCCESS = new CostFunctionFitted(ds, dt, Operation.ADD_SUCCESS);
		c.ADD_FAILURE = new CostFunctionFitted(ds, dt, Operation.ADD_FAILURE);
		c.RANDOM_ELEMENT = new CostFunctionFitted(ds, dt,
				Operation.RANDOM_ELEMENT);
		c.SIZE = new CostFunctionFitted(ds, dt, Operation.SIZE);
		c.ITERATE = new CostFunctionFitted(ds, dt, Operation.ITERATE);
		c.CONTAINS_SUCCESS = new CostFunctionFitted(ds, dt,
				Operation.CONTAINS_SUCCESS);
		c.CONTAINS_FAILURE = new CostFunctionFitted(ds, dt,
				Operation.CONTAINS_FAILURE);
		c.GET_SUCCESS = new CostFunctionFitted(ds, dt, Operation.GET_SUCCESS);
		c.GET_FAILURE = new CostFunctionFitted(ds, dt, Operation.GET_FAILURE);
		c.REMOVE_SUCCESS = new CostFunctionFitted(ds, dt,
				Operation.REMOVE_SUCCESS);
		c.REMOVE_FAILURE = new CostFunctionFitted(ds, dt,
				Operation.REMOVE_FAILURE);

		// c.INIT = CostFunction.read(mainDataDir, ds, dt, Operation.INIT);
		// c.ADD_SUCCESS = CostFunction.read(mainDataDir, ds, dt,
		// Operation.ADD_SUCCESS);
		// c.ADD_FAILURE = CostFunction.read(mainDataDir, ds, dt,
		// Operation.ADD_FAILURE);
		// c.RANDOM_ELEMENT = CostFunction.read(mainDataDir, ds, dt,
		// Operation.RANDOM_ELEMENT);
		// c.SIZE = CostFunction.read(mainDataDir, ds, dt, Operation.SIZE);
		// c.ITERATE = CostFunction.read(mainDataDir, ds, dt,
		// Operation.ITERATE);
		// c.CONTAINS_SUCCESS = CostFunction.read(mainDataDir, ds, dt,
		// Operation.CONTAINS_SUCCESS);
		// c.CONTAINS_FAILURE = CostFunction.read(mainDataDir, ds, dt,
		// Operation.CONTAINS_FAILURE);
		// c.GET_SUCCESS = CostFunction.read(mainDataDir, ds, dt,
		// Operation.GET_SUCCESS);
		// c.GET_FAILURE = CostFunction.read(mainDataDir, ds, dt,
		// Operation.GET_FAILURE);
		// c.REMOVE_SUCCESS = CostFunction.read(mainDataDir, ds, dt,
		// Operation.REMOVE_SUCCESS);
		// c.REMOVE_FAILURE = CostFunction.read(mainDataDir, ds, dt,
		// Operation.REMOVE_FAILURE);
		return c;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("costs for " + this.ds.getSimpleName() + "\n");
		buff.append("  INIT: " + INIT + "\n");
		buff.append("  ADD_SUCCESS: " + ADD_SUCCESS + "\n");
		buff.append("  ADD_FAILURE: " + ADD_FAILURE + "\n");
		buff.append("  RANDOM_ELEMENT: " + RANDOM_ELEMENT + "\n");
		buff.append("  SIZE: " + SIZE + "\n");
		buff.append("  ITERATE: " + ITERATE + "\n");
		buff.append("  CONTAINS_SUCCESS: " + CONTAINS_SUCCESS + "\n");
		buff.append("  CONTAINS_FAILURE: " + CONTAINS_FAILURE + "\n");
		buff.append("  GET_SUCCESS: " + GET_SUCCESS + "\n");
		buff.append("  GET_FAILURE: " + GET_FAILURE + "\n");
		buff.append("  REMOVE_SUCCESS: " + REMOVE_SUCCESS + "\n");
		buff.append("  REMOVE_FAILURE: " + REMOVE_FAILURE + "\n");
		return buff.toString();
	}

}
