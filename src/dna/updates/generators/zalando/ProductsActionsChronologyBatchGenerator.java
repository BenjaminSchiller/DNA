package dna.updates.generators.zalando;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.data.Event;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.generators.zalando.parser.EventFilter;

public class ProductsActionsChronologyBatchGenerator extends
		ZalandoChronologyBatchGenerator {

	/**
	 * Initializes the {@link ProductsActionsChronologyBatchGenerator}.
	 * 
	 * @param gds
	 *            The {@link GraphDataStructure} of the graph to generate.
	 * @param timestampInit
	 *            The time right before start creating the graph.
	 * @param numberOfLinesPerBatch
	 *            The maximum number of {@code Event}s used for each batch. It
	 *            is the <u>maximum</u> number because the log file may have
	 *            fewer lines.
	 * @param eventsFilepath
	 *            The full path of the Zalando log file. Will be passed to
	 *            {@link Old_EventReader}.
	 */
	public ProductsActionsChronologyBatchGenerator(
			ZalandoGraphDataStructure gds, long timestampInit,
			String filterProperties, int numberOfLinesPerBatch,
			String pathProducts, boolean isGzippedProducts, String pathLog,
			boolean isGzippedLog, int omitFirstEvents) {
		super("ProductsActionsChronology", gds, timestampInit, EventFilter
				.fromFile(filterProperties)
		/* new DefaultEventFilter() /* null */, numberOfLinesPerBatch,
				pathProducts, isGzippedProducts, pathLog, isGzippedLog,
				new EventColumn[] { EventColumn.PRODUCTFAMILYID,
						EventColumn.ACTION }, false,
				new EventColumn[] { EventColumn.SESSION }, false, true,
				omitFirstEvents);
	}

	/**
	 * Add edges between the "product actions" of a customer. The edges are
	 * directed, starting at the second newest node of a customer and ending at
	 * the newest node of a customer. The weight of an edge is the number of
	 * customers who have done the product actions in that order.
	 * 
	 * @param event
	 *            The {@link Old_Event} for which values the edges should be
	 *            added.
	 */
	@Override
	void addEdgesForColumns(IGraph g, Event event) {
		int nodeForEventIndex, mappingForColumnGroup;
		nodeForEventIndex = this.mappings.getMapping(
				this.columnGroupsToAddAsNodes[0], event);

		mappingForColumnGroup = this.mappings.getMapping(
				this.columnGroupsToCheckForEquality[0], event);

		for (int otherNodeIndex : this.nodesSortedByColumnGroupsToCheckForEquality
				.getNodes(mappingForColumnGroup, nodeForEventIndex)) {
			if (this.nodesSortedByColumnGroupsToCheckForEquality
					.node1ValueLessOrEqualNode2Value(mappingForColumnGroup,
							nodeForEventIndex, otherNodeIndex)) {
				this.addEdge(g, this.nodesAdded.get(otherNodeIndex),
						this.nodesAdded.get(nodeForEventIndex), 1);

				// for each SessionID connect newest node only to the second
				// newest node, not to every old node
				this.nodesSortedByColumnGroupsToCheckForEquality.removeNode(
						mappingForColumnGroup, otherNodeIndex);
			}
		}
	}

}
