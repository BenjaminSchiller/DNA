package dna.graph.generators.timestamped;

/**
 * Represents a timestamped edge and is used by the TimestampedReader. A
 * timestamped edge consists of a from node index, a to node index and a
 * timestamp when the edge appeared. For directed edges, from=src, to=dst, for
 * undirected edges, the order / assignment does not have any meaning.
 * 
 * @author benni
 *
 */
public class TimestampedEdge implements Comparable<TimestampedEdge> {
	private int from;

	private int to;

	private long timestamp;

	/**
	 * 
	 * @param from
	 *            index of the from / src node
	 * @param to
	 *            index of the to / dst node
	 * @param timestamp
	 *            timestamp when the edge appears
	 */
	public TimestampedEdge(int from, int to, long timestamp) {
		this.from = from;
		this.to = to;
		this.timestamp = timestamp;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public int compareTo(TimestampedEdge o) {
		long temp = this.timestamp - o.timestamp;
		if (temp == 0) {
			return 0;
		} else if (temp < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	public String toString() {
		return this.timestamp + ": " + this.from + " -> " + this.to;
	}
}
