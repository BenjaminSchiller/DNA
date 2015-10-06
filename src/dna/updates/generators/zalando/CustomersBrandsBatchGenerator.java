package dna.updates.generators.zalando;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.data.Event;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.generators.zalando.parser.EventFilter;
import dna.graph.nodes.Node;
import dna.graph.nodes.zalando.ZalandoNode;

public class CustomersBrandsBatchGenerator extends
		ZalandoEqualityBatchGenerator {

	/**
	 * Initializes the {@link CustomersBrandsBatchGenerator}.
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
	public CustomersBrandsBatchGenerator(ZalandoGraphDataStructure gds,
			long timestampInit, String filterProperties,
			int numberOfLinesPerBatch, String pathProducts,
			boolean isGzippedProducts, String pathLog, boolean isGzippedLog,
			int omitFirstEvents) {
		super("CustomersProducts", gds, timestampInit, EventFilter
				.fromFile(filterProperties)
		/* new DefaultEventFilter() /* null */, numberOfLinesPerBatch,
				pathProducts, isGzippedProducts, pathLog, isGzippedLog,
				new EventColumn[] { EventColumn.USER, EventColumn.BRAND },
				true,
				new EventColumn[] { EventColumn.USER, EventColumn.BRAND },
				true, false, omitFirstEvents);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <b>Because the "customers brands graph" is bipartite, only edges from
	 * customers to brands are added, not between customers or between
	 * brands.</b>
	 */
	@Override
	void addEdgesForColumns(Graph g, Event event) {
		int nodeForEventIndex, mappingForColumnGroup;
		nodeForEventIndex = this.mappings.getMapping(
				this.columnGroupsToAddAsNodes[0], event);

		for (EventColumn[] columnGroup : this.columnGroupsToCheckForEquality) {
			mappingForColumnGroup = this.mappings
					.getMapping(columnGroup, event);

			for (int otherNodeIndex : this.nodesSortedByColumnGroupsToCheckForEquality
					.getNodes(mappingForColumnGroup, nodeForEventIndex)) {
				// add edges only between nodes of different columns (bipartite
				// graph)

				final Node nodeForEvent = this.nodesAdded
						.get(nodeForEventIndex);
				final Node otherNode = this.nodesAdded.get(otherNodeIndex);
				if (!ZalandoNode.equalType(nodeForEvent, otherNode)) {
					this.addBidirectionalEdge(g, nodeForEvent, otherNode, 1);
				}
			}
		}
	}

}
