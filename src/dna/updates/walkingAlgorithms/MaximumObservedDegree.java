package dna.updates.walkingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.graph.startNodeSelection.StartNodeSelectionStrategy;
import dna.util.parameters.Parameter;

/**
 * Implementation of Giovannis Maximum Observed Degree (MOD) sampling algorithm
 * 
 * @author Benedict
 * 
 */
public class MaximumObservedDegree extends WalkingAlgorithm {

	TreeMap<Integer, Integer> sortedGreyZone;
	HashMap<Integer, Integer> greyZone;
	Node currentNode;

	/**
	 * Creates an MOD instance
	 * 
	 * @param name
	 *            the name of the sampling algorithm
	 * @param fullGraph
	 *            the graph the algorithm shall operate on
	 * @param startNodeStrat
	 *            the strategy how the algorithm will select the first node
	 * @param onlyVisitedNodesToGraph
	 *            if set to true the generator will only put visited nodes in
	 *            the batch
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
	public MaximumObservedDegree(String name, Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super(name, fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		greyZone = new HashMap<Integer, Integer>();
		NeighborComparator nc = new NeighborComparator(greyZone);
		sortedGreyZone = new TreeMap<Integer, Integer>(nc);
	}

	@Override
	protected Node findNextNode() {

		ArrayList<Node> neighbors = getUnvisitedNeighbors(currentNode);
		for (Node n : neighbors) {
			int id = n.getIndex();
			if (greyZone.containsKey(id)) {
				int size = greyZone.get(id);
				greyZone.put(id, size + 1);
				sortedGreyZone.put(id, size + 1);
			} else {
				int size = getVisitedNeighbors(n).size();
				greyZone.put(id, size);
				sortedGreyZone.put(id, size);
			}
		}

		if (greyZone.isEmpty()) {
			noNodeFound();
			return null;
		}

		int retID = sortedGreyZone.pollFirstEntry().getKey();
		greyZone.remove(retID);
		currentNode = fullGraph.getNode(retID);
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		return currentNode;
	}

}

/**
 * Creates an comparator which compares the size of nodes, stored in another
 * collection
 * 
 * @author Benedict
 * 
 */
class NeighborComparator implements Comparator<Integer> {

	private Map<Integer, Integer> map;

	/**
	 * Creates an comparator
	 * 
	 * @param map
	 *            the map in which the (Node.ID, Size) pairs are stored
	 */
	public NeighborComparator(Map<Integer, Integer> map) {
		this.map = map;
	}

	/**
	 * Compares the sizes of the two (Node.ID, Site) pairs, it retrieves the
	 * values from the other map which also has to store these pairs
	 */
	@Override
	public int compare(Integer arg0, Integer arg1) {
		if (map.get(arg0) > map.get(arg1)) {
			return -1;
		} else if(map.get(arg0) < map.get(arg1)){
			return 1;
		} else {
			return 0;
		}
	}

}
