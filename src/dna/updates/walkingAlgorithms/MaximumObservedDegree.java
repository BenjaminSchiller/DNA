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
 * Implementation of Maximum Observed Degree (MOD) sampling algorithm by
 * Giovanni Neglia et Al.
 * 
 * @author Benedict Jahn
 * 
 */
public class MaximumObservedDegree extends WalkingAlgorithm {

	TreeMap<Integer, Double> sortedGreyZone;
	HashMap<Integer, Double> greyZone;
	Node currentNode;
	int maxNodeID;

	/**
	 * Creates an MOD instance
	 * 
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
	public MaximumObservedDegree(Graph fullGraph,
			StartNodeSelectionStrategy startNodeStrategy,
			boolean onlyVisitedNodesToGraph, int costPerBatch, int resource,
			Parameter[] parameters) {
		super("MOD", fullGraph, startNodeStrategy, onlyVisitedNodesToGraph,
				costPerBatch, resource, parameters);

		maxNodeID = fullGraph.getMaxNodeIndex() + 1;
		greyZone = new HashMap<Integer, Double>();
		NeighborComparator nc = new NeighborComparator(greyZone);
		sortedGreyZone = new TreeMap<Integer, Double>(nc);
	}

	@Override
	protected Node findNextNode() {

		ArrayList<Node> neighbors = getUnvisitedNeighbors(currentNode);
		for (Node n : neighbors) {
			int id = n.getIndex();
			if (greyZone.containsKey(id)) {
				double size = greyZone.get(id);
				size = size + (double) 1;

				// The order of the following three commands is compulsory since
				// the remove(key) method of a treeMap uses the compareTo method
				// of the comparator, which is based on the greyZone HashMap in
				// our case
				// So we first have to remove the mapping from the treeMap and
				// for this we need the original mapping to be existent in the
				// HashMap
				// Then we can change value of the mapping in the HashMap to
				// represent the new state
				// And then we can put the new mapping between key and new value
				// in the treeMap which uses the mapping in the hashMap to
				// properly sort it
				sortedGreyZone.remove(id);
				greyZone.put(id, size);
				sortedGreyZone.put(id, size);
				// This was some hell of a bug fix...
			} else {
				int size = getVisitedNeighbors(n).size();
				double offset = size + ((double) id / (double) maxNodeID);
				greyZone.put(id, offset);
				sortedGreyZone.put(id, offset);
			}
		}

		if (greyZone.isEmpty()) {
			noNodeFound();
			return null;
		}

		int retID = sortedGreyZone.lastEntry().getKey();

		sortedGreyZone.remove(retID);
		greyZone.remove(retID);

		currentNode = fullGraph.getNode(retID);
		return currentNode;
	}

	@Override
	protected Node init(StartNodeSelectionStrategy startNode) {
		currentNode = startNode.getStartNode();
		return currentNode;
	}

	@Override
	protected void localReset() {
		greyZone = new HashMap<Integer, Double>();
		NeighborComparator nc = new NeighborComparator(greyZone);
		sortedGreyZone = new TreeMap<Integer, Double>(nc);
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

	private Map<Integer, Double> map;

	/**
	 * Creates an comparator
	 * 
	 * @param map
	 *            the map in which the (Node.ID, Size) pairs are stored
	 */
	public NeighborComparator(Map<Integer, Double> map) {
		this.map = map;
	}

	/**
	 * Compares the sizes of the two (Node.ID, Site) pairs, it retrieves the
	 * values from the other map which also has to store these pairs
	 */
	@Override
	public int compare(Integer arg0, Integer arg1) {
		if (map.get(arg0) < map.get(arg1)) {
			return -1;
		} else if (map.get(arg0) > map.get(arg1)) {
			return 1;
		} else {
			return 0;
		}
	}

}
