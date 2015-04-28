package dna.metrics.workload;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;

/**
 * 
 * extention of an operation that draws random elements from V and E during
 * initialization. this is useful in case an operation requires random elements
 * but the draw of the random element should not be part of the workload (as it
 * generates additional runtime).
 * 
 * the drawn random elements are stored in a list and returned in round-robin
 * fashion.
 * 
 * @author benni
 *
 */
public abstract class OperationWithRandomSample extends Operation {

	protected Node[] nodes;

	protected int nodesIndex;

	protected Edge[] edges;

	protected int edgesIndex;

	/**
	 * 
	 * @param name
	 *            name of the operation
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions
	 * @param samples
	 *            samples to draw from the specified list
	 */
	public OperationWithRandomSample(String name, ListType list, int times,
			int samples) {
		this(name, list, times, ListType.V.equals(list) ? samples : 0,
				ListType.E.equals(list) ? samples : 0);
	}

	/**
	 * 
	 * @param name
	 *            name of the operation
	 * @param list
	 *            list type to perform the operation on
	 * @param times
	 *            repetitions
	 * @param nodeSamples
	 *            samples to draw from V
	 * @param edgeSamples
	 *            samples to draw from E
	 */
	public OperationWithRandomSample(String name, ListType list, int times,
			int nodeSamples, int edgeSamples) {
		super(name, list, times, new IntParameter("NodeSamples", nodeSamples),
				new IntParameter("EdgeSamples", edgeSamples));
		this.nodes = new Node[nodeSamples];
		this.nodesIndex = 0;
		this.edges = new Edge[edgeSamples];
		this.edgesIndex = 0;
	}

	@Override
	public void init(Graph g) {
		for (int i = 0; i < this.nodes.length; i++) {
			this.nodes[i] = g.getRandomNode();
		}
		for (int i = 0; i < this.edges.length; i++) {
			this.edges[i] = g.getRandomEdge();
		}
	}

	protected Node getSampleNode() {
		return this.nodes[(this.nodesIndex++) % this.nodes.length];
	}

	protected Edge getSampleEdge() {
		return this.edges[(this.edgesIndex++) % this.edges.length];
	}

}
