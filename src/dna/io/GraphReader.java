package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.settings.Keywords;

/**
 * 
 * Allows to read a Graph object from a file written with GraphWriter.
 * 
 * @author benni
 * 
 */
public class GraphReader {
	public static Graph read(String dir, String filename) {
		Reader reader = null;
		try {
			reader = new Reader(dir, filename);

			reader.readKeyword(Keywords.graphName);
			String name = reader.readString();

			reader.readKeyword(Keywords.graphNodes);
			int nodes = reader.readInt();

			reader.readKeyword(Keywords.graphEdges);
			int edges = reader.readInt();

			reader.readKeyword(Keywords.graphTimestamp);
			long timestamp = reader.readLong();

			Graph g = new Graph(name, nodes, timestamp);

			reader.readKeyword(Keywords.graphListOfEdges);
			String line = null;
			while ((line = reader.readString()) != null) {
				g.addEdge(Edge.fromString(line, g));
			}

			if (edges != g.getEdges().size()) {
				throw new InvalidFormatException("expected " + edges
						+ " edges, found " + g.getEdges().size());
			}

			return g;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
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
}
