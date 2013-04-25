package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.graph.old.OldEdge;
import dna.graph.old.OldGraph;
import dna.io.etc.Keywords;

/**
 * 
 * Allows to read a Graph object from a file written with GraphWriter.
 * 
 * @author benni
 * 
 */
public class GraphReader {
	public static OldGraph read(String dir, String filename) {
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

			OldGraph g = new OldGraph(name, nodes, timestamp);

			reader.readKeyword(Keywords.graphListOfEdges);
			String line = null;
			while ((line = reader.readString()) != null) {
				g.addEdge(OldEdge.fromString(line, g));
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
