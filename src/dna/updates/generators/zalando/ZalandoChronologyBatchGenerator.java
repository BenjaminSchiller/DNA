package dna.updates.generators.zalando;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.zalando.data.Event;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.generators.zalando.parser.EventFilter;
import dna.graph.nodes.Node;

/**
 * The most general version of a {@link ZalandoBatchGenerator} for graphs with
 * directed edges between nodes <i>A</i>, <i>B</i> if event for node <i>A</i>
 * has happened before event for node <i>B</i>.
 * 
 * @see CustomersChronologyBatchGenerator
 * @see ProductsActionsChronologyBatchGenerator
 */
public class ZalandoChronologyBatchGenerator extends ZalandoBatchGenerator {

	/**
	 * Initializes the {@link ZalandoChronologyBatchGenerator}.
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
	 *            {@link Old_Event}s must pass it in order to be used for graph
	 *            generation. All {@link Old_Event}s are used if this is
	 *            {@code null}.
	 * @param numberOfLinesPerBatch
	 *            The maximum number of {@code Event}s used for each batch. It
	 *            is the <u>maximum</u> number because the log file may have
	 *            fewer lines.
	 * @param eventsFilepath
	 *            The full path of the Zalando log file. Will be passed to
	 *            {@link Old_EventReader}.
	 * @param columnsToAddAsNodes
	 *            The {@link Old_EventColumn}s of an event which values will be
	 *            represented as nodes in the graph.
	 * @param oneNodeForEachColumn
	 *            If this is true, each value of each
	 *            {@code columnsToAddAsNodes} will be a single node (e.g.
	 *            <i>Product1</i>, <i>SALE</i>). If this is false each value of
	 *            all {@code columnsToAddAsNodes} together will be a single node
	 *            (e.g. <i>Product1|SALE</i>).
	 * @param columnsToCheckForEquality
	 *            The {@link Old_EventColumn}s of an event which values will be
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
	public ZalandoChronologyBatchGenerator(String name,
			ZalandoGraphDataStructure gds, long timestampInit,
			EventFilter eventFilter, int numberOfLinesPerBatch,
			String pathProducts, boolean isGzippedProducts, String pathLog,
			boolean isGzippedLog, EventColumn[] columnsToAddAsNodes,
			boolean oneNodeForEachColumn,
			EventColumn[] columnsToCheckForEquality,
			boolean allColumnsMustBeEqual, boolean absoluteWeights,
			int omitFirstEvents) {
		super(name, gds, timestampInit, eventFilter, numberOfLinesPerBatch,
				pathProducts, isGzippedProducts, pathLog, isGzippedLog,
				columnsToAddAsNodes, oneNodeForEachColumn,
				columnsToCheckForEquality, allColumnsMustBeEqual,
				absoluteWeights, omitFirstEvents);
	}

	/**
	 * Adds an {@link Edge} <u>from each {@link Node}</u> in the given graph
	 * <u>to all nodes for given event</u> with equal values in
	 * {@link #columnGroupsToCheckForEquality}.
	 * <p>
	 * Instead of adding multiple {@link Edge}s with the same {@link Node}s, the
	 * weight of the {@link Edge} is increased by 1 each time.
	 * </p>
	 * <p>
	 * The edge is not added to the graph directly, it is added to
	 * {@link #edgeAdditions} or {@link #edgeWeights}. {@link #generate(IGraph)}
	 * adds these to the batch to update the graph.
	 * </p>
	 * 
	 * @param event
	 *            The {@link Old_Event} for which values the edges should be
	 *            added.
	 * 
	 * @see #addEdge(Node, Node, Object)
	 */
	@Override
	void addEdgesForColumns(IGraph g, Event event) {
		// TODO
//		final GraphDataStructure gds = g.getGraphDatastructures();
		int nodeForEventIndex, mappingForColumnGroup;
		for (EventColumn[] eventColumnGroup : this.columnGroupsToAddAsNodes) {
			nodeForEventIndex = this.mappings.getMapping(eventColumnGroup,
					event);

			for (EventColumn[] columnGroup : this.columnGroupsToCheckForEquality) {
				mappingForColumnGroup = this.mappings.getMapping(columnGroup,
						event);

				for (int otherNodeIndex : this.nodesSortedByColumnGroupsToCheckForEquality
						.getNodes(mappingForColumnGroup, nodeForEventIndex)) {

					final Node nodeForEvent = this.nodesAdded
							.get(nodeForEventIndex);
					final Node otherNode = this.nodesAdded.get(otherNodeIndex);

					if (this.nodesSortedByColumnGroupsToCheckForEquality
							.node1ValueLessOrEqualNode2Value(
									mappingForColumnGroup, nodeForEventIndex,
									otherNodeIndex))
						this.addEdge(g, otherNode, nodeForEvent, 1);
				}
			}
		}
	}

}
