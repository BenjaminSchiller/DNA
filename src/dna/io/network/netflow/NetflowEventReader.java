package dna.io.network.netflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dna.graph.edges.network.NetworkEdge;
import dna.io.network.NetworkEvent;
import dna.io.network.NetworkReader;
import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.util.Config;

/**
 * The NetflowEventReader is a sophisticated implementation of the
 * NetworkReader. It is used for reading NetflowEvents from a netflow file. <br>
 * <br>
 * 
 * It iterates through the given file and always buffers the n+1-th list entry.
 * This way it can be distinguished whether new events are available. However,
 * this requires the netflow list to be sorted by timestamp.<br>
 * <br>
 * 
 * Furthermore, it contains a queue of UpdateEvent's for additional event
 * handling, i.e. in order to queue changes made to the graph and revert them at
 * a later time.<br>
 * <br>
 * 
 * Additional configuration fields for netflow-based graph modeling are also
 * available, like batch-length and edge-lifetime.
 * 
 * @author Rwilmes
 * 
 */
public class NetflowEventReader extends NetworkReader {

	// statics
	public static final int gnuplotOffset = Config
			.getInt("GNUPLOT_TIMESTAMP_OFFSET");

	// CLASS
	protected String separator;
	protected NetflowEvent bufferedEvent;
	protected NetflowEventField[] fields;

	// configuration
	protected int edgeLifeTimeSeconds;
	protected int batchIntervalSeconds;

	protected boolean generateEmptyBatches;
	protected boolean removeZeroDegreeNodes;
	protected boolean removeZeroWeightEdges;

	// times
	protected DateTime initTimestamp;
	protected DateTime maximumTimestamp;
	protected DateTime minimumTimestamp;

	// labels
	protected ArrayList<String> labelsOccuredInCurrentBatch;

	// event queue
	protected LinkedList<UpdateEvent> eventQueue = new LinkedList<UpdateEvent>();

	protected boolean debug;

	// reader
	protected boolean finished;

	protected DateTimeFormatter timeFormat;
	protected DateTimeFormatter dateFormat;

	protected String dir;
	protected String filename;

	protected int dataOffsetSeconds;

	public NetflowEventReader(String dir, String filename,
			NetflowEventField... fields) throws FileNotFoundException {
		this(dir, filename, Config.get("NETFLOW_READER_DEFAULT_SEPARATOR"),
				Config.get("NETFLOW_READER_DEFAULT_DATE_FORMAT"), Config
						.get("NETFLOW_READER_DEFAULT_TIME_FORMAT"), 0, fields);
	}

	public NetflowEventReader(String dir, String filename, String separator,
			String dateFormat, String timeFormat, int dataOffsetSeconds,
			NetflowEventField... fields) throws FileNotFoundException {
		super(dir, filename);
		this.dir = dir;
		this.filename = filename;
		this.separator = separator;
		this.fields = fields;
		this.timeFormat = DateTimeFormat.forPattern(timeFormat);
		this.dateFormat = DateTimeFormat.forPattern(dateFormat);

		this.dataOffsetSeconds = dataOffsetSeconds;

		this.removeZeroDegreeNodes = Config
				.getBoolean("NETFLOW_READER_DEFAULT_REMOVE_ZERO_DEGREE_NODES");
		this.removeZeroWeightEdges = Config
				.getBoolean("NETFLOW_READER_DEFAULT_REMOVE_ZERO_WEIGHT_EDGES");

		this.finished = false;

		this.eventQueue = new LinkedList<UpdateEvent>();

		skipToInitEvent();

		this.debug = false;
	}

	protected void skipToInitEvent() {
		try {
			this.bufferedEvent = parseLine(readString());

			if (this.minimumTimestamp != null) {
				while (this.bufferedEvent.getTime().isBefore(
						this.minimumTimestamp)) {
					this.bufferedEvent = parseLine(readString());

					if (this.bufferedEvent == null)
						break;
				}
			}

			if (this.bufferedEvent != null)
				this.initTimestamp = this.bufferedEvent.getTime();
			else
				this.finished = true;
		} catch (IOException e) {
			this.finished = true;
			e.printStackTrace();
		}
	}

	/** Returns a map with the sum of all weight decrementals per edge. **/
	public HashMap<String, Integer> getWeightDecrementals(
			ArrayList<NetworkEdge> allEdges) {
		HashMap<String, Integer> wcMap = new HashMap<String, Integer>();
		for (int i = 0; i < allEdges.size(); i++) {
			NetworkEdge e = allEdges.get(i);
			decrementWeightChanges(e.getSrc(), e.getDst(), wcMap);
		}

		return wcMap;
	}

	protected void decrementWeightChanges(int src, int dst,
			HashMap<String, Integer> map) {
		// add incrementals to map
		String identifier = getIdentifier(src, dst);
		if (map.containsKey(identifier)) {
			map.put(identifier, map.get(identifier) - 1);
		} else {
			map.put(identifier, -1);
		}
	}

	protected String getIdentifier(int src, int dst) {
		return src + "->" + dst;
	}

	/**
	 * Returns the timestamp of the next decrement-edge event or -1 if queue is
	 * empty.
	 **/
	public long getNextDecrementEventsTimestamp() {
		// System.out.println("getting next decr edge timestamp");
		// System.out.println("\tdecredges: " + this.edgeQueue.size());
		// if(this.edgeQueue.size() > 0)
		// System.out.println("\tfirstedgetime: " +
		// getFirstEdgeFromQueue().getTime());
		if (isEventQueueEmpty())
			return -1;
		else
			return getFirstEventFromQueue().getTime();
	}

	/** Returns all decrement events until the threshold. **/
	public ArrayList<UpdateEvent> getDecrementEvents(long threshold) {
		ArrayList<UpdateEvent> list = new ArrayList<UpdateEvent>();

		boolean finished = false;
		while (!finished && !eventQueue.isEmpty()) {
			UpdateEvent e = getFirstEventFromQueue();
			long t = e.getTime();

			if (t <= threshold)
				list.add(popFirstEventFromQueue());
			else
				finished = true;
		}

		return list;
	}

	public void addUpdateEventToQueue(UpdateEvent e) {
		// only add events that are before maximumTimestamp
		if (this.maximumTimestamp != null)
			if (e.getTime() > this.maximumTimestamp.getMillis())
				return;

		eventQueue.add(e);
	}

	/** Returns the first event from the queue. **/
	public UpdateEvent getFirstEventFromQueue() {
		return eventQueue.getFirst();
	}

	/** Removes and returns the first event from the queue. **/
	public UpdateEvent popFirstEventFromQueue() {
		return eventQueue.removeFirst();
	}

	/** Returns true if the event queue is empty. **/
	public boolean isEventQueueEmpty() {
		return eventQueue.isEmpty();
	}

	/** Returns the timestamp of the next event or -1 if no event is buffered. **/
	public long getNextEventTimestamp() {
		if (bufferedEvent != null)
			return bufferedEvent.getTime().getMillis();
		else
			return -1;
	}

	/** Reads and returns all events until the threshold is reached. **/
	public ArrayList<NetworkEvent> getEventsUntil(DateTime threshold) {
		ArrayList<NetworkEvent> events = new ArrayList<NetworkEvent>();

		// if next event above threshold -> return empty list
		if (bufferedEvent != null)
			if (bufferedEvent.getTime().isAfter(threshold))
				return events;

		// read events and add them to list
		while (isNextEventPossible()) {
			// if next-event after threshold, return
			if (bufferedEvent.getTime().isAfter(threshold))
				return events;

			events.add(getNextEvent());
		}

		// return
		return events;
	}

	/** Returns the next event read from the reader. **/
	public NetflowEvent getNextEvent() {
		NetflowEvent e = this.bufferedEvent;

		String line;
		try {
			line = readString();

			if (line != null) {
				NetflowEvent temp = parseLine(line);

				if (temp.getTime().isAfter(this.maximumTimestamp)) {
					this.bufferedEvent = null;
					this.finished = true;
				} else {
					this.bufferedEvent = temp;
				}
			} else {
				this.bufferedEvent = null;
				this.finished = true;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return e;
	}

	/** Returns false when no new event can be retrieved. **/
	public boolean isNextEventPossible() {
		return !finished || bufferedEvent != null;
	}

	/** Set debug-mode for additional log prints. **/
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/** Parses the given line and returns a new NetflowEvent object. **/
	protected NetflowEvent parseLine(String line) {
		if (debug)
			System.out.println(line);

		String[] splits = line.split(this.separator);

		long id = 0;

		DateTime time = null;
		DateTime date = new DateTime(0);
		String srcAddress = null;
		String dstAddress = null;
		NetflowDirection direction = null;
		String flags = null;
		String connectionState = null;
		double duration = 0;
		String label = null;

		String protocol = null;
		String srcPort = null;
		String dstPort = null;

		int packets = 0;
		int packetsToSrc = 0;
		int packetsToDestination = 0;

		int bytes = 0;
		int bytesToSrc = 0;
		int bytesToDestination = 0;

		for (int i = 0; (i < this.fields.length && i < splits.length); i++) {
			String x = splits[i];
			switch (this.fields[i]) {
			case Bytes:
				bytes = Integer.parseInt(x);
				break;
			case BytesToDst:
				bytesToDestination = Integer.parseInt(x);
				break;
			case BytesToSrc:
				bytesToSrc = Integer.parseInt(x);
				break;
			case ConnectionState:
				connectionState = x;
				break;
			case Date:
				date = this.dateFormat.parseDateTime(x);
				break;
			case Direction:
				if (x.equals("->") || x.equals("-->"))
					direction = NetflowDirection.forward;
				if (x.equals("<-") || x.equals("<--"))
					direction = NetflowDirection.backward;
				if (x.equals("<->") || x.equals("<-->"))
					direction = NetflowDirection.bidirectional;
				break;
			case DstAddress:
				dstAddress = x;
				break;
			case DstPort:
				dstPort = x;
				break;
			case Duration:
				duration = Double.parseDouble(x);
				break;
			case Flags:
				flags = x;
				break;
			case Label:
				label = x;
				break;
			case None:
				break;
			case numberOfNetflows:
				break;
			case Packets:
				packets = Integer.parseInt(x);
				break;
			case PacketsToDst:
				packetsToDestination = Integer.parseInt(x);
				break;
			case PacketsToSrc:
				packetsToSrc = Integer.parseInt(x);
				break;
			case Protocol:
				protocol = x;
				break;
			case SrcAddress:
				srcAddress = x;
				break;
			case SrcPort:
				srcPort = x;
				break;
			case Time:
				time = this.timeFormat.parseDateTime(x);
				break;
			}
		}

		DateTime dateTime = (time != null) ? date.plusSeconds(time
				.getSecondOfDay()) : date;

		dateTime = dateTime.plusSeconds(this.dataOffsetSeconds);

		return new NetflowEvent(id, dateTime, srcAddress, dstAddress, duration,
				direction, connectionState, flags, protocol, srcPort, dstPort,
				packets, packetsToSrc, packetsToDestination, bytes, bytesToSrc,
				bytesToDestination, label);
	}

	/** Returns the set directory. **/
	public String getDir() {
		return dir;
	}

	/** Returns the set filename. **/
	public String getFilename() {
		return filename;
	}

	/** Returns the batch-interval in seconds. **/
	public int getBatchIntervalSeconds() {
		return batchIntervalSeconds;
	}

	/** Returns the fields used for netflow parsing. **/
	public NetflowEventField[] getFields() {
		return fields;
	}

	/** Sets the fields for netflow parsing. **/
	public void setFields(NetflowEventField[] fields) {
		this.fields = fields;
	}

	/** Returns the edge-lifetime in seconds. **/
	public int getEdgeLifeTimeSeconds() {
		return edgeLifeTimeSeconds;
	}

	/** Sets the edge-lifetime in seconds. **/
	public void setEdgeLifeTimeSeconds(int edgeLifeTimeSeconds) {
		this.edgeLifeTimeSeconds = edgeLifeTimeSeconds;
	}

	/** Returns if empty batches will be generated. **/
	public boolean isGenerateEmptyBatches() {
		return generateEmptyBatches;
	}

	/** Sets if empty batches will be generated. **/
	public void setGenerateEmptyBatches(boolean generateEmptyBatches) {
		this.generateEmptyBatches = generateEmptyBatches;
	}

	/** Returns if nodes with zero degree should be removed. **/
	public boolean isRemoveZeroDegreeNodes() {
		return removeZeroDegreeNodes;
	}

	/** Sets if nodes with zero degree should be removed. **/
	public void setRemoveZeroDegreeNodes(boolean removeZeroDegreeNodes) {
		this.removeZeroDegreeNodes = removeZeroDegreeNodes;
	}

	/** Returns if edges with zero weight should be removed. **/
	public boolean isRemoveZeroWeightEdges() {
		return removeZeroWeightEdges;
	}

	/** Sets if edges with zero weight should be removed. **/
	public void setRemoveZeroWeightEdges(boolean removeZeroWeightEdges) {
		this.removeZeroWeightEdges = removeZeroWeightEdges;
	}

	/**
	 * Returns true when the reader is finished, i.e. it has reached the
	 * end-of-file or it has been manually set to be finished. (For example when
	 * the desired number of batches has been reached.)
	 **/
	public boolean isFinished() {
		return finished;
	}

	/** Sets if the reader is finished. **/
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/** Sets the batch interval in seconds. **/
	public void setBatchIntervalSeconds(int batchIntervalSeconds) {
		this.batchIntervalSeconds = batchIntervalSeconds;
	}

	/** Returns the init timestamp. **/
	public DateTime getInitTimestamp() {
		return this.initTimestamp;
	}

	/** Sets the minimum timestamp. **/
	public void setMinimumTimestamp(String date) {
		this.minimumTimestamp = this.timeFormat.parseDateTime(date)
				.plusSeconds(NetflowEventReader.gnuplotOffset);
		skipToInitEvent();
	}

	/** Sets the minimum timestamp. **/
	public void setMinimumTimestamp(long timestampMillis) {
		this.minimumTimestamp = new DateTime(timestampMillis)
				.plusSeconds(NetflowEventReader.gnuplotOffset);
		skipToInitEvent();
	}

	/** Sets the minimum timestamp. **/
	public void setMinimumTimestamp(DateTime date) {
		this.minimumTimestamp = date;
		this.skipToInitEvent();
	}

	/** Sets the maximum timestamp. **/
	public void setMaximumTimestamp(String date) {
		this.maximumTimestamp = this.timeFormat.parseDateTime(date)
				.plusSeconds(NetflowEventReader.gnuplotOffset);
	}

	/** Sets the maximum timestamp. **/
	public void setMaximumTimestamp(long timestampMillis) {
		this.maximumTimestamp = new DateTime(timestampMillis)
				.plusSeconds(NetflowEventReader.gnuplotOffset);
	}

	/** Sets the maximum timestamp. **/
	public void setMaximumTimestamp(DateTime date) {
		this.maximumTimestamp = date;
	}

	/** Returns the data offsert in seconds. **/
	public int getDataOffset() {
		return this.dataOffsetSeconds;
	}

	/** Sets the data offsert in seconds. **/
	public void setDataOffset(int seconds) {
		this.dataOffsetSeconds = seconds;
	}

}
