package dna.updates.generators.sampling;

import java.util.Iterator;
import java.util.LinkedList;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.updates.generators.sampling.startNode.StartNodeSelectionStrategy;
import dna.util.Rand;

/**
 * Sampling algorithm that randomly selects the next node out of all nodes from
 * the graph.
 * 
 * @author Benedict
 * 
 */
public class UniformSampling extends SamplingAlgorithm {

	LinkedList<IElement> notVisited;

	/**
	 * Creates an instance of the uniform sampling algorithm
	 * 
	 * @param fullGraph
	 *            the graph the algorithm shall walk on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param costPerBatch
	 *            how many steps the algorithm shall perform for one batch
	 * @param ressouce
	 *            the maximum count of steps the algorithm shall perform, if
	 *            initialized with 0 or below the algorithm will walk until the
	 *            graph is fully visited
	 * @param parameters
	 *            the parameters which makes this algorithm unique and which
	 *            will be added to the name
	 */
	public UniformSampling(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, SamplingStop stop) {
		super("US", fullGraph, startNodeStrategy, costPerBatch, resource, stop);

		notVisited = makeList(fullGraph.getNodes());
	}

	@Override
	protected Node findNextNode() {
		if (notVisited.isEmpty()) {
			noNodeFound();
			return null;
		}
		return (Node) notVisited.remove(Rand.rand.nextInt(notVisited.size()));
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		Node startingNode = startNode.getStartNode(this.fullGraph);
		notVisited.remove(startingNode);
		return startingNode;
	}

	@Override
	protected void localReset() {
		notVisited = makeList(fullGraph.getNodes());
	}

	private LinkedList<IElement> makeList(Iterable<IElement> iterable) {
		Iterator<IElement> iter = iterable.iterator();
		LinkedList<IElement> list = new LinkedList<IElement>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

}
