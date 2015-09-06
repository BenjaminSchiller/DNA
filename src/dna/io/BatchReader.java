package dna.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;

/**
 * A batch reader to read in a written batch
 * 
 * @author benni
 * 
 */
public class BatchReader {

	public static Batch read(String dir, String filename, IGraph g) {
		Reader reader = null;

		try {
			reader = new Reader(dir, filename);

			reader.readKeyword(BatchWriter.fromKeyword);
			long from = reader.readLong();

			reader.readKeyword(BatchWriter.toKeyword);
			long to = reader.readLong();

			Batch b = new Batch(g.getGraphDatastructures(), from, to);

			reader.readKeyword(BatchWriter.updatesKeyword);

			HashMap<Integer, Node> addedNodes = new HashMap<Integer, Node>();
			HashSet<Edge> addedEdges = new HashSet<Edge>();

			String line = null;
			while ((line = reader.readString()) != null) {
				Update u = parseLine(line, g, addedNodes);

				b.add(u);
				if (u instanceof NodeAddition) {
					Node n = (Node) ((NodeAddition) u).getNode();
					addedNodes.put(n.getIndex(), n);
				} else if (u instanceof EdgeAddition) {
					Edge e = (Edge) ((EdgeAddition) u).getEdge();
					addedEdges.add(e);
				}
			}

			return b;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	protected static Update parseLine(String line, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		return Update.fromString(g.getGraphDatastructures(), g, line,
				addedNodes);
	}

	public static long[] readTimestamps(String dir, String filename)
			throws IOException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(BatchWriter.fromKeyword);
		long from = reader.readLong();

		reader.readKeyword(BatchWriter.toKeyword);
		long to = reader.readLong();

		reader.close();
		return new long[] { from, to };
	}
}
