package dna.graph.generators.network;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.edges.network.UpdateEvent;
import dna.io.network.NetworkEvent;
import dna.io.network.NetworkReader;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.Log;
import dna.util.parameters.Parameter;

/**
 * A batch-generator which creates batches based on netflow events read by a
 * NetflowEventReader from a netflow-list file.
 * 
 * @author Rwilmes
 * 
 */
public abstract class NetworkBatch extends BatchGenerator {

	protected int batchIntervalSeconds;
	protected DateTime threshold;

	protected boolean init;
	protected boolean finished;

	protected NetworkReader reader;

	protected NetworkEvent bufferedEvent;

	protected boolean firstBatch = true;

	public NetworkBatch(String name, NetworkReader reader,
			int batchIntervalSeconds) throws FileNotFoundException {
		super(name, new Parameter[0]);
		this.reader = reader;
		this.batchIntervalSeconds = batchIntervalSeconds;

		this.init = false;
		this.finished = false;
	}

	public abstract Batch craftBatch(Graph g, DateTime timestamp,
			ArrayList<NetworkEvent> events, ArrayList<UpdateEvent> otherEvents,
			HashMap<String, Integer> edgeWeighChanges);

	/** Increments the threshold by the given batchLength. **/
	protected void incrementThreshold() {
		threshold = threshold.plusSeconds(batchIntervalSeconds);
	}

	/** Sets the threshold to the next step. **/
	protected void stepToThreshold(long nextThreshold) {
		long diff = nextThreshold - threshold.getMillis();
		double multi = diff / (1000.0 * batchIntervalSeconds);
		int multiplier = (multi < 1) ? 1 : (int) Math.floor(multi);
		threshold = threshold.plusSeconds(batchIntervalSeconds * multiplier);
	}

	public Batch generate(Graph graph) {
		if (!init) {
			this.threshold = new DateTime(TimeUnit.SECONDS.toMillis(graph
					.getTimestamp())).plusSeconds(batchIntervalSeconds);
			init = true;
		}

		// get events
		ArrayList<NetworkEvent> events = reader.getEventsUntil(threshold);
		ArrayList<UpdateEvent> decrementEvents = reader
				.getDecrementEvents(threshold.getMillis());

		// if both empty -> increase threshold and call generate again
		if (events.isEmpty() && decrementEvents.isEmpty()) {
			if (reader.isGenerateEmptyBatches()) {
				incrementThreshold();
			} else {
				long nextEventTimestamp = reader.getNextEventTimestamp();
				long nextDecrementTimestamp = reader
						.getNextDecrementEventsTimestamp();

				if (nextEventTimestamp > -1 && nextDecrementTimestamp > -1) {
					// both evens valid -> step to next timestamp
					stepToThreshold(Math.min(nextEventTimestamp,
							nextDecrementTimestamp));
				} else if (nextEventTimestamp == -1) {
					if (nextDecrementTimestamp == -1) {
						// no next events, should not occur
						Log.warn("no next events in queue!");
					} else {
						// only next decrement edge event valid
						stepToThreshold(nextDecrementTimestamp);
					}
				} else if (nextDecrementTimestamp == -1) {
					// only next event valid
					stepToThreshold(nextEventTimestamp);
				}

				return generate(graph);
			}
		}

		// return crafted batch
		if (firstBatch)
			firstBatch = false;
		else {
			if (!reader.isNextEventPossible() && reader.isEventQueueEmpty())
				finished = true;
		}

		return craftBatch(graph, threshold, events, decrementEvents, null);
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		if (finished)
			return false;
		else
			return true;
	}
}
