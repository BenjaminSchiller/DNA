package dna.graph.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;

public class CustomersGraphGenerator extends ZalandoEqualityGraphGenerator {

	/**
	 * Initializes the {@link CustomersGraphGenerator}.
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
	public CustomersGraphGenerator(GraphDataStructure gds, long timestampInit,
			int maxNumberOfEvents, String eventsFilepath) {
		super(
				"Customers",
				gds,
				timestampInit,
				null,
				maxNumberOfEvents,
				eventsFilepath,
				new EventColumn[] { EventColumn.PERMANENT_COOKIE_ID },
				true,
				new EventColumn[] { EventColumn.FAMILY_SKU, EventColumn.AKTION },
				true, false);
	}

}
