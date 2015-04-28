package dna.graph.generators.timestamped;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dna.io.Reader;
import dna.util.Log;
import dna.util.Timer;

/**
 * Reads a list of timestamped edges from a specified file. Each timestamped
 * edge is given in a separate line. The format for a timestamped edge is the
 * following: $SRC$sep$DST$sep$timestamp. $SRC is the source of the edge, $DST
 * is its destination, and $timestamp is the timestamp at when the edge appeared
 * in the graph. $sep is the separator (has to be specified in the constructor
 * of the reader). src and dst can be integers or strings. For integer values it
 * is reasonable to use 0,1,2... If this is not the case or in case arbitrary
 * node names (Strings) are used, the names or indices can be mapped to integers
 * 0,1,2... by enabling it with the "remapIndex" flag.
 * 
 * An example for an edge from 2 to 5 that appeared at timestamp 592 is
 * "2 5 592" with a separator " ". Another example (using re-mapping) is the
 * edge from "AB C" to "GS BBF" at timestamp "123" with separator "--":
 * "AB C--GS FFB--123". In this case, "AB C" and "GS BBF" would be mapped to
 * indices.
 * 
 * @author benni
 *
 */
public class TimestampedReader {
	public String commentPrefix = "%";

	public String separator = ",";

	private boolean remapIndex;

	private String dir;

	private String filename;

	private String name;

	private ArrayList<TimestampedEdge> edges;

	private HashMap<String, Integer> ids;

	private int currentIndex = 0;

	/**
	 * 
	 * @param dir
	 *            directory to read from
	 * @param filename
	 *            name of the file to read
	 * @param name
	 *            name of the graph / dataset
	 * @throws IOException
	 */
	public TimestampedReader(String dir, String filename, String name)
			throws IOException {
		this(dir, filename, name, true);
	}

	/**
	 * 
	 * @param dir
	 *            directory to read from
	 * @param filename
	 *            name of the file to read
	 * @param name
	 *            name of the graph / dataset
	 * @param remapIndex
	 *            flag if the names / indexes of nodes should be re-mapped (to
	 *            0,1,2,...)
	 * @throws IOException
	 */
	public TimestampedReader(String dir, String filename, String name,
			boolean remapIndex) {
		this(dir, filename, name, remapIndex, "%", ",");
	}

	/**
	 * 
	 * @param dir
	 *            directory to read from
	 * @param filename
	 *            name of the file to read
	 * @param name
	 *            name of the graph / dataset
	 * @param remapIndex
	 *            flag if the names / indexes of nodes should be re-mapped (to
	 *            0,1,2,...)
	 * @param separator
	 *            separator used between nodes / timestamp
	 * @param commentPrefix
	 *            prefix of a line that it discarded as a comment
	 */
	public TimestampedReader(String dir, String filename, String name,
			boolean remapIndex, String separator, String commentPrefix) {
		this.dir = dir;
		this.filename = filename;
		this.name = name;
		this.remapIndex = remapIndex;
		this.separator = separator;
		this.commentPrefix = commentPrefix;
	}

	public void read() throws IOException {
		Timer t = new Timer("");
		this.edges = new ArrayList<TimestampedEdge>();
		this.currentIndex = 0;
		Reader reader = new Reader(this.dir, this.filename, commentPrefix, true);
		this.ids = new HashMap<String, Integer>();
		String line = null;
		while ((line = reader.readString()) != null) {
			this.edges.add(this.getEdge(line));
		}
		reader.close();
		Log.debug("reading konect file: " + t.end());
		t = new Timer("");
		Collections.sort(this.edges);
		Log.debug("sorting set of edges: " + t.end());
	}

	/**
	 * 
	 * @return true if there are any more edges available
	 */
	public boolean hasMoreEdges() {
		return this.hasMoreEdges(Long.MAX_VALUE);
	}

	/**
	 * 
	 * @param timestamp
	 * @return true if the there are more edges with a timestamp <= than the
	 *         given one
	 */
	public boolean hasMoreEdges(long timestamp) {
		return this.currentIndex < this.edges.size()
				&& this.edges.get(this.currentIndex).getTimestamp() <= timestamp;
	}

	/**
	 * after retrieving the element, the pointer is moved to the next element
	 * 
	 * @return the next edge in the list
	 */
	public TimestampedEdge next() {
		return this.next(Long.MAX_VALUE);
	}

	/**
	 * returns the next edge in the list, if the next element has a timestamp
	 * greater than or equal to the given one, null will be returned otherwise.
	 * after retrieving the element, the pointer is moved to the next element
	 * 
	 * @param maxTimestamp
	 *            max timestamp of the element
	 * @return the next edge in the list
	 */
	public TimestampedEdge next(long maxTimestamp) {
		if (this.hasMoreEdges()
				&& this.edges.get(this.currentIndex).getTimestamp() <= maxTimestamp) {
			TimestampedEdge edge = this.edges.get(this.currentIndex++);

			return edge;
		}
		return null;
	}

	/**
	 * return the next edge in the list WITHOUT moving the pointer to the next
	 * element
	 * 
	 * @return the next edge in the list
	 */
	public TimestampedEdge peek() {
		if (this.hasMoreEdges()) {
			return this.edges.get(this.currentIndex);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return minimum timestamp found for all edges
	 */
	public long getMinTimestamp() {
		return this.edges.get(0).getTimestamp();
	}

	/**
	 * this returns the timestamp of the last edge in the list sorted by
	 * timestamps. in case this edge is a duplicate the real maximum timestamp
	 * might be smaller!
	 * 
	 * @return maximum timestamp found for all edges
	 */
	public long getMaxTimestamp() {
		return this.edges.get(this.edges.size() - 1).getTimestamp();
	}

	private TimestampedEdge getEdge(String line) {
		String[] temp = line.split(separator);
		int from = this.getIndex(temp[0]);
		int to = this.getIndex(temp[1]);
		long timestamp = Long.parseLong(temp[2]);
		return new TimestampedEdge(from, to, timestamp);
	}

	private int getIndex(String original) {
		if (!this.remapIndex) {
			return Integer.parseInt(original);
		}
		if (this.ids.containsKey(original)) {
			return this.ids.get(original);
		} else {
			int index = this.ids.size();
			this.ids.put(original, index);
			return index;
		}
	}

	/**
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return the smallest timestamp of all edges that is larger than the
	 *         specified one
	 */
	public long getFirstTimestampAfter(long timestamp) {
		for (TimestampedEdge e : this.edges) {
			if (e.getTimestamp() > timestamp) {
				return e.getTimestamp();
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return the number of edges with timestamp less than or equal to the
	 *         specified one
	 */
	public int countEdgesUntil(long timestamp) {
		int counter = 0;
		for (TimestampedEdge e : this.edges) {
			if (e.getTimestamp() > timestamp) {
				break;
			}
			counter++;
		}
		return counter;
	}

	public int countEdgesAfter(long timestamp) {
		return this.edges.size() - this.countEdgesUntil(timestamp);
	}

	public String getName() {
		return this.name;
	}

}