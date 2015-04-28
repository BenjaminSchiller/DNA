package dna.updates.generators.timestamped;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.generators.timestamped.TimestampedEdge;
import dna.graph.generators.timestamped.TimestampedReader;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.util.Log;
import dna.util.parameters.LongParameter;
import dna.util.parameters.StringParameter;

/**
 * Reads a batch from a file using the specified TimestampedReader. Edges are
 * (like for the graph) read in ascending order of the timestamps. Three modes
 * are available to determine how many edges are read for a single batch:
 * TIMESTAMP_INTERVAL, EDGE_COUNT, and BATCH_SIZE. TIMESTAMP_INTERVAL: all edges
 * with timestamps in (t,t+interval] are added to the batch where t is the
 * graph's current timestamp. EDGE_COUNT: for each batch, the specified number
 * of edges is added. BATCH_SIZE: batches of the specified size are returned.
 * Since currently, only edge additions are considered for timestamped edges,
 * this is the same as EDGE_COUNT.
 * 
 * @author benni
 *
 */
public class TimestampedBatch extends BatchGenerator {

	public enum TimestampedBatchType {
		TIMESTAMP_INTERVAL, EDGE_COUNT, BATCH_SIZE
	};

	private TimestampedReader reader;

	private TimestampedBatchType type;

	private long parameter;

	private long maxTimestamp;

	/**
	 * 
	 * @param reader
	 *            timestamped reader to use (use the same as for the graph!)
	 * @param type
	 *            TIMESTAMP_INTERVAL, EDGE_COUNT, or BATCH_SIZE
	 * @param parameter
	 *            interval-size, edge count, or batch size
	 */
	public TimestampedBatch(TimestampedReader reader,
			TimestampedBatchType type, long parameter) {
		this(reader, type, parameter, Long.MAX_VALUE);
	}

	/**
	 * 
	 * @param reader
	 *            timestamped reader to use (use the same as for the graph!)
	 * @param type
	 *            TIMESTAMP_INTERVAL, EDGE_COUNT, or BATCH_SIZE
	 * @param parameter
	 *            interval-size, edge count, or batch size
	 * @param maxTimestamp
	 *            the generation will stop in case all edges have been read or
	 *            in case the current itmestamp is larger than or equal to this
	 *            paramter
	 */
	public TimestampedBatch(TimestampedReader reader,
			TimestampedBatchType type, long parameter, long maxTimestamp) {
		super(reader.getName(), new StringParameter("type", type.toString()),
				new LongParameter("p", parameter));
		this.reader = reader;
		this.type = type;
		this.parameter = parameter;
		this.maxTimestamp = maxTimestamp;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(), -1);
		TimestampedEdge e = null;
		int duplicateEdges = 0;
		int loops = 0;

		HashMap<Integer, Node> newNodes = new HashMap<Integer, Node>();

		while ((e = this.next(b)) != null) {
			if (e.getFrom() == e.getTo()) {
				loops++;
				continue;
			}
			if (g.containsEdge(e.getFrom(), e.getTo())) {
				duplicateEdges++;
				continue;
			}

			Node from = g.getNode(e.getFrom());
			if (from == null) {
				from = newNodes.get(e.getFrom());
				if (from == null) {
					from = g.getGraphDatastructures().newNodeInstance(
							e.getFrom());
					b.add(new NodeAddition(from));
					newNodes.put(e.getFrom(), from);
				}
			}
			Node to = g.getNode(e.getTo());
			if (to == null) {
				to = newNodes.get(e.getTo());
				if (to == null) {
					to = g.getGraphDatastructures().newNodeInstance(e.getTo());
					b.add(new NodeAddition(to));
					newNodes.put(e.getTo(), to);
				}
			}

			Edge edge = g.getGraphDatastructures().newEdgeInstance(from, to);
			b.add(new EdgeAddition(edge));

			b.setTo(e.getTimestamp());
		}
		Log.debug("removed " + duplicateEdges + " duplicate edges and " + loops
				+ " loops");
		if (this.type.equals(TimestampedBatchType.TIMESTAMP_INTERVAL)) {
			b.setTo(b.getFrom() + this.parameter);
		}
		if (b.getFrom() >= b.getTo()) {
			b.setTo(b.getFrom() + 1);
		}
		return b;
	}

	private TimestampedEdge next(Batch b) {
		switch (this.type) {
		case BATCH_SIZE:
			if (b.getSize() >= this.parameter) {
				return null;
			}
			return this.reader.next();
		case EDGE_COUNT:
			if (b.getEdgeAdditionsCount() >= this.parameter) {
				return null;
			}
			return this.reader.next();
		case TIMESTAMP_INTERVAL:
			return this.reader.next(b.getFrom() + this.parameter);
		default:
			return null;
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return this.reader.hasMoreEdges(this.maxTimestamp);
	}

}
