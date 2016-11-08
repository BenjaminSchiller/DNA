package dna.io.network;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import dna.graph.edges.network.NetworkEdge;
import dna.io.Reader;
import dna.io.network.netflow.UpdateEvent;

public abstract class NetworkReader extends Reader {

	public NetworkReader(String dir, String filename)
			throws FileNotFoundException {
		super(dir, filename);
	}

	/** Reads and returns all events until the threshold is reached. **/
	public abstract ArrayList<NetworkEvent> getEventsUntil(DateTime threshold);

	/** Returns all decrement events until the threshold. **/
	public abstract ArrayList<UpdateEvent> getDecrementEvents(long threshold);

	/** Returns the timestamp of the next event or -1 if no event is buffered. **/
	public abstract long getNextEventTimestamp();

	/**
	 * Returns the timestamp of the next decrement-edge event or -1 if queue is
	 * empty.
	 **/
	public abstract long getNextDecrementEventsTimestamp();

	/** Returns a map with the sum of all weight decrementals per edge. **/
	public abstract HashMap<String, Integer> getWeightDecrementals(
			ArrayList<NetworkEdge> decrementEdges);

	public abstract boolean isNextEventPossible();

	public abstract boolean isEventQueueEmpty();

	public abstract boolean isGenerateEmptyBatches();
}
