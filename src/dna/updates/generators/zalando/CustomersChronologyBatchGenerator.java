package dna.updates.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.generators.zalando.parser.EventFilter;

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
	 *            {@link Old_EventReader}.
	 */
	public CustomersChronologyBatchGenerator(ZalandoGraphDataStructure gds,
			long timestampInit, String filterProperties,
			int numberOfLinesPerBatch, String pathProducts,
			boolean isGzippedProducts, String pathLog, boolean isGzippedLog, int omitFirstEvents) {
		super("CustomersChronology", gds, timestampInit, EventFilter
				.fromFile(filterProperties)
		/* new DefaultEventFilter() /* null */, numberOfLinesPerBatch,
				pathProducts, isGzippedProducts, pathLog, isGzippedLog,
				new EventColumn[] { EventColumn.USER }, true,
				new EventColumn[] { EventColumn.PRODUCTFAMILYID,
						EventColumn.ACTION }, true, true, omitFirstEvents);
	}

}
