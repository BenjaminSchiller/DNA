package dna.graph.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

/**
 * The most general version of a {@link ZalandoGraphGenerator} for graphs with
 * edges between nodes with equal properties.
 * 
 * @see CustomersGraphGenerator
 * @see ProductsGraphGenerator
 * @see CustomersProductsGraphGenerator
 * @see CustomersActionsGraphGenerator
 * @see ProductsActionsGraphGenerator
 */
class ZalandoEqualityGraphGenerator extends ZalandoGraphGenerator {

	/**
	 * Initializes the {@link ZalandoEqualityGraphGenerator}.
	 * 
	 * @param name
	 *            The name of the graph to generate. The final name will be
	 *            <i>Zalando</i>{@code name}<i>Directed</i> or <i>Zalando</i>
	 *            {@code name}<i>Undirected</i>, depending on {@code gds}.
	 * @param gds
	 *            The {@link GraphDataStructure} of the graph to generate.
	 * @param timestampInit
	 *            The time right before start creating the graph.
	 * @param eventFilter
	 *            If this is set to an {@link EventFilter} != {@code null}, all
	 *            {@link Event}s must pass it in order to be used for graph
	 *            generation. All {@link Event}s are used if this is
	 *            {@code null}.
	 * @param maxNumberOfEvents
	 *            The maximum number of {@code Event}s used for graph
	 *            generation. It is the <u>maximum</u> number because the log
	 *            file may have fewer lines.
	 * @param eventsFilepath
	 *            The full path of the Zalando log file. Will be passed to
	 *            {@link EventReader}.
	 * @param columnsToAddAsNodes
	 *            The {@link EventColumn}s of an event which values will be
	 *            represented as nodes in the graph.
	 * @param oneNodeForEachColumn
	 *            If this is true, each value of each
	 *            {@code columnsToAddAsNodes} will be a single node (e.g.
	 *            <i>Product1</i>, <i>SALE</i>). If this is false each value of
	 *            all {@code columnsToAddAsNodes} together will be a single node
	 *            (e.g. <i>Product1|SALE</i>).
	 * @param columnsToCheckForEquality
	 *            The {@link EventColumn}s of an event which values will be
	 *            represented as edges in the graph.
	 * @param allColumnsMustBeEqual
	 *            If this is true, all the values of all
	 *            {@code columnsToCheckForEquality} must be equal for the nodes
	 *            of two events to add an edge between those two events. If this
	 *            is false, at least one value of all
	 *            {@code columnsToCheckForEquality} must be equal.
	 * @param absoluteWeights
	 *            If this is true, the weight of an edge is the number of
	 *            relations represented by the edge between the two nodes
	 *            connected by this edge. So it is greater if the two nodes have
	 *            much in common. If this is false, the weight is the inverse of
	 *            this number. So two nodes are "close together" if they have
	 *            much in common.
	 */
	public ZalandoEqualityGraphGenerator(String name, ZalandoGraphDataStructure gds,
			long timestampInit, EventFilter eventFilter, int maxNumberOfEvents,
			String eventsFilepath, EventColumn[] columnsToAddAsNodes,
			boolean oneNodeForEachColumn,
			EventColumn[] columnsToCheckForEquality,
			boolean allColumnsMustBeEqual, boolean absoluteWeights) {
		super(name, gds, timestampInit, eventFilter, maxNumberOfEvents,
				eventsFilepath, columnsToAddAsNodes, oneNodeForEachColumn,
				columnsToCheckForEquality, allColumnsMustBeEqual,
				absoluteWeights);
	}

	/**
	 * Adds an edge from given source node to given target node. If the graph is
	 * undirected, this method adds an edge from given target to given source
	 * node, too.
	 * 
	 * @param node1
	 *            The first node of the edge to add.
	 * @param node2
	 *            The second node of the edge to add.
	 * @param weight
	 *            The weight of the edge to add or -if the edge already exists-
	 *            the weight added to the current weight of the existing edge.
	 * 
	 * @see #addEdge(Node, Node, int)
	 */
	void addBidirectionalEdge(Node node1, Node node2, Object weight) {
		this.addEdge(node1, node2, weight);

		if (this.graph.isDirected())
			this.addEdge(node2, node1, weight);
	}

	/**
	 * Adds an bidirectional {@link Edge} <u>from each {@link Node}</u> for
	 * given event <u>to all other {@link Node}s</u> of {@link Event}s with
	 * equal values in {@link #columnGroupsToCheckForEquality}. If
	 * {@link #graph} is directed, two {@link Edges} are added (<i>A->B</i> and
	 * <i>B->A</i>).
	 * <p>
	 * Instead of adding multiple {@link Edge}s with the same {@link Node}s, the
	 * weight of the {@link Edge} is increased by 1.
	 * </p>
	 * 
	 * @param event
	 *            The {@link Event} for which values the edges should be added.
	 * 
	 * @see #addBidirectionalEdge(Node, Node, int)
	 */
	@Override
	void addEdgesForColumns(Event event) {
		int nodeForEventIndex, mappingForColumnGroup;
		for (EventColumn[] eventColumnGroup : this.columnGroupsToAddAsNodes) {
			nodeForEventIndex = this.mappings.getMapping(
					eventColumnGroup, event);

			for (EventColumn[] columnGroup : this.columnGroupsToCheckForEquality) {
				mappingForColumnGroup = this.mappings.getMapping(
						columnGroup, event);

				for (int otherNodeIndex : this.nodesSortedByColumnGroupsToCheckForEquality
						.getNodes(mappingForColumnGroup, nodeForEventIndex)) {
					if (this.nodesSortedByColumnGroupsToCheckForEquality
							.node1ValueLessOrEqualNode2Value(
									mappingForColumnGroup, nodeForEventIndex,
									otherNodeIndex))
						this.addBidirectionalEdge(
								this.gds.newNodeInstance(nodeForEventIndex, eventColumnGroup),
								this.gds.newNodeInstance(otherNodeIndex, eventColumnGroup), 1);
				}
			}
		}
	}

}
