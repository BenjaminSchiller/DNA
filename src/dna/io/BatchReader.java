package dna.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;
import dna.util.Config;

/**
 * A batch reader to read in a written batch
 * 
 * @author benni
 * 
 */
public class BatchReader {

	public static Batch read(String dir, String filename, Graph g) {
		Reader reader = null;

		try {
			reader = new Reader(dir, filename);

			reader.readKeyword(Config.get("BATCH_KEYWORD_FROM"));
			long from = reader.readLong();

			reader.readKeyword(Config.get("BATCH_KEYWORD_TO"));
			long to = reader.readLong();

			Batch b = new Batch(g.getGraphDatastructures(), from, to);

			reader.readKeyword(Config.get("BATCH_KEYWORD_UPDATES"));

			HashMap<Integer, Node> addedNodes = new HashMap<Integer, Node>();
			HashSet<Edge> addedEdges = new HashSet<Edge>();

			String line = null;
			while ((line = reader.readString()) != null) {
				Update u = Update.fromString(g.getGraphDatastructures(), g,
						line, addedNodes, addedEdges);

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

	public static long[] readTimestamps(String dir, String filename)
			throws IOException {
		Reader reader = new Reader(dir, filename);

		reader.readKeyword(Config.get("BATCH_KEYWORD_FROM"));
		long from = reader.readLong();

		reader.readKeyword(Config.get("BATCH_KEYWORD_TO"));
		long to = reader.readLong();

		reader.close();
		return new long[] { from, to };
	}
}
