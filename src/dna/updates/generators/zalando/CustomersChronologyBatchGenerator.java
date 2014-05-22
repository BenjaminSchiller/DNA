package dna.updates.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.zalando.EventColumn;
import dna.graph.generators.zalando.EventReader;

public class CustomersChronologyBatchGenerator extends
		ZalandoChronologyBatchGenerator {

	/**
	 * Initializes the {@link CustomersChronologyBatchGenerator}.
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
	public CustomersChronologyBatchGenerator(GraphDataStructure gds,
			long timestampInit, int numberOfLinesPerBatch, String eventsFilepath) {
		super(
				"CustomersChronology",
				gds,
				timestampInit,
				null,
				numberOfLinesPerBatch,
				eventsFilepath,
				new EventColumn[] { EventColumn.PERMANENT_COOKIE_ID },
				true,
				new EventColumn[] { EventColumn.FAMILY_SKU, EventColumn.AKTION },
				true, true);
	}

}
