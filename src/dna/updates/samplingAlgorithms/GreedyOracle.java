package dna.updates.samplingAlgorithms;

import java.util.ArrayList;
import java.util.Collections;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.samplingAlgorithms.SortableNode.SortType;
import dna.updates.samplingAlgorithms.startNodeSelection.StartNodeSelectionStrategy;

/**
 * An implementation of the sampling algorithm of Guha and Khuler. This
 * algorithm is referenced as "greedy oracle" from Giovanni Neglia et Al. The
 * algorithm is called oracle because it has a one hop look ahead and chooses
 * the next node based on the highest count of unseen neighbors.
 * 
 * @author Benedict Jahn
 * 
 */
public class GreedyOracle extends SamplingAlgorithm {

	ArrayList<SortableNode> greyZone;

	/**
	 * Creates an instance of the greedy oracle sampling algorithm
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
	public GreedyOracle(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy, int costPerBatch,
			int resource, SamplingStop stop) {
		super("GO", fullGraph, startNodeStrategy, costPerBatch, resource, stop);

		greyZone = new ArrayList<SortableNode>(fullGraph.getNodeCount());
	}

	@Override
	protected Node findNextNode() {
		if (greyZone.isEmpty()) {
			noNodeFound();
			return null;
		}
		Collections.sort(greyZone);
		Node newNode = greyZone.remove(0).getNode();

		ArrayList<Node> neighbors = getUnseenNeighbors(newNode);
		for (Node n : neighbors) {
			greyZone.add(new SortableNode(n, this,
					SortType.SORT_BY_UNSEEN_NEIGHBORS));
		}
		return newNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		Node firstNode = startNode.getStartNode(this.fullGraph);
		ArrayList<Node> neighbors = getAllNeighbors(firstNode);
		for (Node n : neighbors) {
			if (n != firstNode) {
				greyZone.add(new SortableNode(n, this,
						SortType.SORT_BY_UNSEEN_NEIGHBORS));
			}
		}
		return firstNode;
	}

	@Override
	protected void localReset() {
		greyZone = new ArrayList<SortableNode>(fullGraph.getNodeCount());
	}

}

/**
 * This class makes nodes sortable in the way, that the node with the highest
 * degree of unseen/unvisited/visited nodes is the best
 * 
 * @author Benedict
 * 
 */
class SortableNode implements Comparable<SortableNode> {

	public enum SortType {
		SORT_BY_VISITED_NEIGHBORS, SORT_BY_UNVISITED_NEIGHBORS, SORT_BY_UNSEEN_NEIGHBORS
	};

	private Node n;
	private SamplingAlgorithm algo;
	private SortType sortType;
	private int size;
	private long oldTimeStamp;

	/**
	 * Initializes the sortable node
	 * 
	 * @param n
	 *            the node that shall be sorted
	 * @param algo
	 *            the algorithm on which we operate
	 */
	public SortableNode(Node n, SamplingAlgorithm algo, SortType sortType) {
		this.n = n;
		this.algo = algo;
		this.sortType = sortType;
		size = 0;
		oldTimeStamp = 0;
	}

	/**
	 * Returns the node
	 */
	public Node getNode() {
		return n;
	}

	/**
	 * Calculates the yield, the count of unseen/unvisited/visited neighbors of
	 * this node
	 */
	private int getYield() {
		if (oldTimeStamp != 0 && oldTimeStamp == algo.getTimeStamp()) {
			return size;
		}
		oldTimeStamp = algo.getTimeStamp();
		if (sortType == SortType.SORT_BY_UNVISITED_NEIGHBORS) {
			size = algo.getUnvisitedNeighbors(n).size();
		} else if (sortType == SortType.SORT_BY_VISITED_NEIGHBORS) {
			size = algo.getVisitedNeighbors(n).size();
		} else if (sortType == SortType.SORT_BY_UNSEEN_NEIGHBORS) {
			size = algo.getUnseenNeighbors(n).size();
		}
		return size;
	}

	/**
	 * Compares the yield of two nodes
	 */
	@Override
	public int compareTo(SortableNode o) {
		int oCount = o.getYield();
		int myCount = this.getYield();

		if (oCount < myCount) {
			return -1;
		} else if (myCount < oCount) {
			return 1;
		} else {
			return 0;
		}
	}

}
