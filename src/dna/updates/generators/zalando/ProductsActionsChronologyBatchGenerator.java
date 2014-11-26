package dna.updates.generators.zalando;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.Event;
import dna.graph.generators.zalando.EventColumn;
import dna.graph.generators.zalando.EventReader;

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
	 *            {@link EventReader}.
	 */
	public ProductsActionsChronologyBatchGenerator(
			ZalandoGraphDataStructure gds, long timestampInit,
			int numberOfLinesPerBatch, String eventsFilepath) {
		super("ProductsActionsChronology", gds, timestampInit, null,
				numberOfLinesPerBatch, eventsFilepath, new EventColumn[] {
						EventColumn.FAMILYSKU, EventColumn.AKTION }, false,
				new EventColumn[] { EventColumn.SESSIONID }, false, true);
	}

	/**
	 * Add edges between the "product actions" of a customer. The edges are
	 * directed, starting at the second newest node of a customer and ending at
	 * the newest node of a customer. The weight of an edge is the number of
	 * customers who have done the product actions in that order.
	 * 
	 * @param event
	 *            The {@link Event} for which values the edges should be added.
	 */
	@Override
	void addEdgesForColumns(Graph g, Event event) {
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
