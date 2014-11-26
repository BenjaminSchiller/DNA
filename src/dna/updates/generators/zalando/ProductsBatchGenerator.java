package dna.updates.generators.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.zalando.ZalandoGraphDataStructure;
import dna.graph.generators.zalando.EventColumn;
import dna.graph.generators.zalando.EventReader;

public class ProductsBatchGenerator extends ZalandoEqualityBatchGenerator {

	/**
	 * Initializes the {@link ProductsBatchGenerator}.
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
	public ProductsBatchGenerator(ZalandoGraphDataStructure gds,
			long timestampInit, int numberOfLinesPerBatch, String eventsFilepath) {
		super("Products", gds, timestampInit, null, numberOfLinesPerBatch,
				eventsFilepath, new EventColumn[] { EventColumn.FAMILYSKU },
				true, new EventColumn[] { EventColumn.PERMANENTCOOKIEID,
						EventColumn.AKTION }, true, false);
	}

}
