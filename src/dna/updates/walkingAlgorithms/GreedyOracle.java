package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import java.util.Collections;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.updates.walkingAlgorithms.SortableNode.SortType;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class GreedyOracle extends WalkingAlgorithm {

	ArrayList<SortableNode> greyZone;

	/**
	 * @param name
	 * @param fullGraph
	 * @param startNodeStrategy
	 * @param onlyVisitedNodesToGraph
	 * @param costPerBatch
	 * @param resource
	 * @param parameters
	 */
	public GreedyOracle(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		greyZone = new ArrayList<SortableNode>(fullGraph.getNodeCount());
	}

	@Override
	protected Node findNextNode() {
		if(greyZone.isEmpty()){
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
		Node firstNode = startNode.getStartNode();
		ArrayList<Node> neighbors = getAllNeighbors(firstNode);
		for (Node n : neighbors) {
				greyZone.add(new SortableNode(n, this,
						SortType.SORT_BY_UNSEEN_NEIGHBORS));
		}
		return firstNode;
	}

}

/**
 * This class makes nodes sortable in the way that the node with the highest
 * degree of unseen nodes is the best
 * 
 * @author Benedict
 * 
 */
class SortableNode implements Comparable<SortableNode> {

	public enum SortType {
		SORT_BY_VISITED_NEIGHBORS, SORT_BY_UNVISITED_NEIGHBORS, SORT_BY_UNSEEN_NEIGHBORS
	};

	private Node n;
	private WalkingAlgorithm algo;
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
	public SortableNode(Node n, WalkingAlgorithm algo, SortType sortType) {
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
	 * Calculates the yield (the count of unseen neighbors of this node)
	 */
	private int getYield() {
		if(oldTimeStamp != 0 && oldTimeStamp == algo.getTimeStamp()){
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
		} else if(myCount < oCount) {
			return 1;
		} else{
			return 0;
		}
	}

}
