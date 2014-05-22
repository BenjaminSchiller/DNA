package dna.graph.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;

public class ProductsActionsChronologyGraphGenerator extends
		ZalandoChronologyGraphGenerator {

	/**
	 * Initializes the {@link ProductsActionsChronologyGraphGenerator}.
	 * 
	 * @param gds
	 *            The {@link GraphDataStructure} of the graph to generate.
	 * @param timestampInit
	 *            The time right before start creating the graph.
	 * @param maxNumberOfEvents
	 *            The maximum number of {@code Event}s used for graph
	 *            generation. It is the <u>maximum</u> number because the log
	 *            file may have fewer lines.
	 * @param eventsFilepath
	 *            The full path of the Zalando log file. Will be passed to
	 *            {@link EventReader}.
	 */
	public ProductsActionsChronologyGraphGenerator(GraphDataStructure gds,
			long timestampInit, int maxNumberOfEvents, String eventsFilepath) {
		super("ProductsActionsChronology", gds, timestampInit, null,
				maxNumberOfEvents, eventsFilepath, new EventColumn[] {
						EventColumn.FAMILY_SKU, EventColumn.AKTION }, false,
				new EventColumn[] { EventColumn.SESSION_ID }, false, true);
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
	void addEdgesForColumns(Event event) {
		int nodeForEventIndex, mappingForColumnGroup;
		nodeForEventIndex = this.mappings.getGlobalMapping(
				this.columnGroupsToAddAsNodes[0], event);

		mappingForColumnGroup = this.mappings.getGlobalMapping(
				this.columnGroupsToCheckForEquality[0], event);

		for (int otherNodeIndex : this.nodesSortedByColumnGroupsToCheckForEquality
				.getNodes(mappingForColumnGroup, nodeForEventIndex)) {
			if (this.nodesSortedByColumnGroupsToCheckForEquality
					.node1ValueLessOrEqualNode2Value(mappingForColumnGroup,
							nodeForEventIndex, otherNodeIndex)) {
				this.addEdge(this.gds.newNodeInstance(otherNodeIndex),
						this.gds.newNodeInstance(nodeForEventIndex), 1);

				// for each SessionID connect newest node only to the second
				// newest node, not to every old node
				this.nodesSortedByColumnGroupsToCheckForEquality.removeNode(
						mappingForColumnGroup, otherNodeIndex);
			}
		}
	}

}
