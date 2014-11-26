package dna.updates.generators.zalando;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.zalando.Event;
import dna.graph.generators.zalando.EventColumn;
import dna.graph.generators.zalando.EventFilter;
import dna.graph.generators.zalando.EventReader;
import dna.graph.nodes.Node;

/**
 * The most general version of a {@link ZalandoBatchGenerator} for graphs with
 * edges between nodes with equal properties.
 * 
 * @see CustomersBatchGenerator
 * @see ProductsBatchGenerator
 * @see CustomersProductsBatchGenerator
 * @see CustomersBrandsBatchGenerator
 * @see CustomersCategory4BatchGenerator
 * @see CustomersActionsBatchGenerator
 * @see ProductsActionsBatchGenerator
 * @see SessionsCategory4BatchGenerator
 */
public class ZalandoEqualityBatchGenerator extends ZalandoBatchGenerator {

	/**
	 * Initializes the {@link ZalandoEqualityBatchGenerator}.
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
	 * @param numberOfLinesPerBatch
	 *            The maximum number of {@code Event}s used for each batch. It
	 *            is the <u>maximum</u> number because the log file may have
	 *            fewer lines.
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
	public ZalandoEqualityBatchGenerator(String name,
			ZalandoGraphDataStructure gds, long timestampInit,
			EventFilter eventFilter, int numberOfLinesPerBatch,
			String eventsFilepath, EventColumn[] columnsToAddAsNodes,
			boolean oneNodeForEachColumn,
			EventColumn[] columnsToCheckForEquality,
			boolean allColumnsMustBeEqual, boolean absoluteWeights) {
		super(name, gds, timestampInit, eventFilter, numberOfLinesPerBatch,
				eventsFilepath, columnsToAddAsNodes, oneNodeForEachColumn,
				columnsToCheckForEquality, allColumnsMustBeEqual,
				absoluteWeights);
	}

	/**
	 * Adds an edge from given source node to given target node. If the given
	 * graph is undirected, this method adds an edge from given target to given
	 * source node, too.
	 * <p>
	 * The edge is not added to the graph directly, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
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
	void addBidirectionalEdge(Graph g, Node node1, Node node2, Object weight) {
		this.addEdge(g, node1, node2, weight);

		if (g.isDirected())
			this.addEdge(g, node2, node1, weight);
	}

	/**
	 * Adds an bidirectional {@link Edge} <u>from each {@link Node}</u> for
	 * given event <u>to all other {@link Node}s</u> of {@link Event}s with
	 * equal values in {@link #columnGroupsToCheckForEquality}. If the given
	 * graph is directed, two {@link Edges} are added (<i>A->B</i> and
	 * <i>B->A</i>).
	 * <p>
	 * Instead of adding multiple {@link Edge}s with the same {@link Node}s, the
	 * weight of the {@link Edge} is increased by 1.
	 * </p>
	 * <p>
	 * The edge is not added to the graph directly, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(Graph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param event
	 *            The {@link Event} for which values the edges should be added.
	 * 
	 * @see #addBidirectionalEdge(Node, Node, int)
	 */
	@Override
	void addEdgesForColumns(Graph g, Event event) {
		int nodeForEventIndex, mappingForColumnGroup;
		// TODO
		// final GraphDataStructure gds = g.getGraphDatastructures();

		for (EventColumn[] eventColumnGroup : this.columnGroupsToAddAsNodes) {
			nodeForEventIndex = this.mappings.getMapping(
					eventColumnGroup, event);

			for (EventColumn[] columnGroup : this.columnGroupsToCheckForEquality) {
				mappingForColumnGroup = this.mappings.getMapping(
						columnGroup, event);

				for (int otherNodeIndex : this.nodesSortedByColumnGroupsToCheckForEquality
						.getNodes(mappingForColumnGroup, nodeForEventIndex)) {
					
					final Node nodeForEvent = this.nodesAdded
							.get(nodeForEventIndex);
					final Node otherNode = this.nodesAdded.get(otherNodeIndex);
					
					if (this.nodesSortedByColumnGroupsToCheckForEquality
							.node1ValueLessOrEqualNode2Value(
									mappingForColumnGroup, nodeForEventIndex,
									otherNodeIndex))
						this.addBidirectionalEdge(g, nodeForEvent, otherNode, 1);
				}
			}
		}
	}

}
