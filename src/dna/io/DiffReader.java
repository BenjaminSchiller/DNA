package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.diff.Diff;
import dna.graph.Edge;
import dna.graph.Graph;

public class DiffReader {
	public static Diff read(String filename, Graph g) {
		Reader reader = null;
		try {
			reader = new Reader(filename);

			reader.readKeyword(Keywords.diffNodes);
			int nodes = reader.readInt();

			reader.readKeyword(Keywords.diffFrom);
			long from = reader.readLong();

			reader.readKeyword(Keywords.diffTo);
			long to = reader.readLong();

			reader.readKeyword(Keywords.diffAddedEdges);
			int added = reader.readInt();

			reader.readKeyword(Keywords.diffRemovedEdges);
			int removed = reader.readInt();

			Diff d = new Diff(nodes, from, to);

			reader.readKeyword(Keywords.diffListOfAddedEdges);
			String line = null;
			while ((line = reader.readString()) != null
					&& !line.equals(Keywords
							.asLine(Keywords.diffListofRemovedEdges))) {
				d.addAddedEdges(Edge.fromString(line, g));
			}
			if (d.getAddedEdges().size() != added) {
				throw new InvalidFormatException("expected " + added
						+ " added edges, found " + d.getAddedEdges().size());
			}

			while ((line = reader.readString()) != null) {
				d.addRemovedEdge(Edge.fromString(line, g));
			}
			if (d.getRemovedEdges().size() != removed) {
				throw new InvalidFormatException("expected " + removed
						+ " removed edges, found " + d.getRemovedEdges().size());
			}

			return d;
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
