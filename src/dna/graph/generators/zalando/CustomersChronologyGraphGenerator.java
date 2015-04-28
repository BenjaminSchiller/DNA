package dna.graph.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.data.EventColumn;

public class CustomersChronologyGraphGenerator extends
		ZalandoChronologyGraphGenerator {

	/**
	 * Initializes the {@link CustomersChronologyGraphGenerator}.
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
	 *            {@link Old_EventReader}.
	 */
	public CustomersChronologyGraphGenerator(ZalandoGraphDataStructure gds,
			long timestampInit, int maxNumberOfEvents, String pathProducts, boolean isGzippedProducts, String pathLog,
			boolean isGzippedLog) {
		super(
				"CustomersChronology",
				gds,
				timestampInit,
				null,
				maxNumberOfEvents,
				pathProducts, isGzippedProducts, pathLog,
				isGzippedLog,
				new EventColumn[] { EventColumn.USER },
				true,
				new EventColumn[] { EventColumn.PRODUCTFAMILYID, EventColumn.ACTION },
				true, true);
	}

}
