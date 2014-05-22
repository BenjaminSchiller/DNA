package dna.graph.generators.zalando;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.generators.zalando.ZalandoBatchGenerator;

/**
 * Keeps the indices of {@link Node}s sorted by mappings of {@link EventColumn}
 * groups. Used by {@link ZalandoGraphGenerator} and
 * {@link ZalandoBatchGenerator} to add {@link Edge}s between common
 * {@link Node}s. To set the right weight of the {@link Edge}, the number of
 * {@link Event}s where each value of a {@link Node} is seen together with the
 * appropriate value for {@link Edge} is saved as "value" for these two, too.
 */
public class EdgeValuesForNodes {

	// <mapping of column group value <node-index, number of events for value
	// and node>>
	private HashMap<Integer, HashMap<Integer, Integer>> map;

	/**
	 * Creates {@link EdgeValuesForNodes} without any values.
	 */
	public EdgeValuesForNodes() {
		this.map = new HashMap<Integer, HashMap<Integer, Integer>>();
	}

	/**
	 * Adds the given node index to the set of nodes with equal values for given
	 * given edge column group mapping. The set will be created if it does not
	 * exist yet. If the given node index is already in the set for given
	 * mapping (sets of different mappings have no influence here), its value
	 * will be increased by 1. The value is equal to the number of events where
	 * edge column group value where observed and the node created for.
	 * 
	 * @see EventMappings About edge column groups
	 */
	public void addNode(int edgeColumnGroupMapping, int nodeIndex) {
		HashMap<Integer, Integer> m;
		if (this.map.containsKey(edgeColumnGroupMapping)) {
			m = this.map.get(edgeColumnGroupMapping);

			if (m.containsKey(nodeIndex))
				m.put(nodeIndex, m.get(nodeIndex) + 1);
			else
				m.put(nodeIndex, 1);
		} else {
			m = new HashMap<Integer, Integer>();
			m.put(nodeIndex, 1);
		}
		this.map.put(edgeColumnGroupMapping, m);
	}

	/**
	 * @param edgeColumnGroupMapping
	 *            The mapping of a value of
	 *            {@link ZalandoGraphGenerator#columnGroupsToCheckForEquality}
	 *            of an {@link Event}.
	 * @return All {@link Node} indices which {@link Event}s they where created
	 *         for have the same mapping of their
	 *         {@link ZalandoGraphGenerator#columnGroupsToCheckForEquality} as
	 *         given {@code edgeColumnGroupMapping}.
	 * 
	 * @see EventMappings About edge column groups
	 */
	Set<Integer> getNodes(int edgeColumnGroupMapping) {
		if (this.map.containsKey(edgeColumnGroupMapping))
			return this.map.get(edgeColumnGroupMapping).keySet();
		else
			return new HashSet<Integer>();
	}

	/**
	 * This method is just like {@link #getNodes(int)} but the result set will
	 * not contain given node index. Is is used by graph and batch generators to
	 * not add an edge to the node excluded.
	 * 
	 * @param edgeColumnGroupMapping
	 *            The mapping of a value of
	 *            {@link ZalandoGraphGenerator#columnGroupsToCheckForEquality}
	 *            of an {@link Event}.
	 * @param nodeIndexException
	 *            The returned set of node indices will not contain this node
	 *            index.
	 * @return All {@link Node} indices (but not {@code nodeMappingException})
	 *         which {@link Event}s they where created for have the same mapping
	 *         of their
	 *         {@link ZalandoGraphGenerator#columnGroupsToCheckForEquality} as
	 *         given {@code edgeColumnGroupMapping}.
	 * 
	 * @see EventMappings About edge column groups
	 */
	public Set<Integer> getNodes(int edgeColumnGroupMapping,
			int nodeIndexException) {
		final Set<Integer> m = new HashSet<Integer>(
				this.getNodes(edgeColumnGroupMapping));
		m.remove(nodeIndexException);
		return m;
	}

	/**
	 * @param edgeColumnGroupMapping
	 *            The mapping for a value of an edge column group.
	 * @param node1Index
	 *            The index of the first {@link Node}.
	 * @param node2Index
	 *            The index of the second {@link Node}.
	 * @return True if and only if the number of events for given edge column
	 *         group mapping and first node is less or equal to the number of
	 *         events for given edge column group an second node. If the number
	 *         for the first node is greater than the number for the second node
	 *         <u>or if one of the parameters does not exist, false is
	 *         returned</u>.
	 * 
	 * @see EventMappings About edge column groups
	 */
	public boolean node1ValueLessOrEqualNode2Value(int edgeColumnGroupMapping,
			int node1Index, int node2Index) {
		if (!this.map.containsKey(edgeColumnGroupMapping))
			return false;
		final HashMap<Integer, Integer> m = this.map
				.get(edgeColumnGroupMapping);

		if (!m.containsKey(node1Index) || !m.containsKey(node2Index))
			return false;
		final int value1 = m.get(node1Index);
		final int value2 = m.get(node2Index);

		return value1 <= value2;
	}

	/**
	 * Removes the node with the given index of the set of node (indices) with
	 * the given mapping of an edge column group. Nothing will happen if one of
	 * the given values does not exist.
	 * 
	 * @param edgeColumnGroupMapping
	 *            The identifier of the set from where to remove the node.
	 * @param nodeIndex
	 *            The index of the node to remove.
	 * 
	 * @see EventMappings About edge column groups
	 */
	public void removeNode(int edgeColumnGroupMapping, int nodeIndex) {
		if (this.map.containsKey(edgeColumnGroupMapping))
			this.map.get(edgeColumnGroupMapping).remove(nodeIndex);
	}

}
