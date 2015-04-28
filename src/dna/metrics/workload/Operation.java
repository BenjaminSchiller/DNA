package dna.metrics.workload;

import dna.graph.Graph;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;
import dna.util.parameters.StringParameter;

/**
 * 
 * an operation generates workload of a specific kind. the workload is created
 * on a specific list type (V or E) and for a specified number of repetitions
 * (times).
 * 
 * @author benni
 *
 */
public abstract class Operation extends ParameterList {
	public enum ListType {
		V, E
	}

	protected ListType list;

	protected int times;

	/**
	 * 
	 * @param name
	 *            name of the operation, should be the class name
	 * @param listType
	 *            type of list for this instance
	 * @param times
	 *            number of executions of operation per workload creation
	 * @param parameters
	 *            addition parameters
	 */
	public Operation(String name, ListType listType, int times,
			Parameter... parameters) {
		super(name, new Parameter[] {
				new StringParameter("listType", listType.toString()),
				new IntParameter("times", times) }, parameters);
		this.list = listType;
		this.times = times;
	}

	/**
	 * generates the respective workload on the pre-defined list. Before this,
	 * the initialization must be executed!
	 * 
	 * @param g
	 *            graph
	 */
	public void createWorkload(Graph g) {
		switch (this.list) {
		case E:
			for (int i = 0; i < this.times; i++) {
				this.createWorkloadE(g);
			}
			break;
		case V:
			for (int i = 0; i < this.times; i++) {
				this.createWorkloadV(g);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * this initialization is called once for each graph, even if the workload
	 * is created many times. this can be used to execute operations that only
	 * need to be executes once as a setup of the workload, e.g., select a
	 * random subset of the list for use during workload generation.
	 * 
	 * !!! the runtime of this method is not counted towards the total runtime
	 * of the workload !!!
	 * 
	 * @param g
	 *            graph
	 */
	public abstract void init(Graph g);

	/**
	 * creates the specified workload on the global edge list
	 * 
	 * @param g
	 *            graph
	 */
	protected abstract void createWorkloadE(Graph g);

	/**
	 * creates the specified workload on the global node list
	 * 
	 * @param g
	 *            graph
	 */
	protected abstract void createWorkloadV(Graph g);

	/**
	 * 
	 * @return type of the list this operation is executed on
	 */
	public ListType getListType() {
		return list;
	}

	/**
	 * 
	 * @return number of times to repeat this workload
	 */
	public int getTimes() {
		return times;
	}

}
