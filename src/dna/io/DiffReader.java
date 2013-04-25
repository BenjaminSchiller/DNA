package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.diff.Diff;
import dna.graph.old.OldEdge;
import dna.graph.old.OldGraph;
import dna.io.etc.Keywords;

/**
 * 
 * Allow to read a Diff object from a file that was written using DiffWriter.
 * 
 * @author benni
 * 
 */
public class DiffReader {
	public static Diff read(String dir, String filename, OldGraph g) {
		Reader reader = null;
		try {
			reader = new Reader(dir, filename);

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
				d.addAddedEdges(OldEdge.fromString(line, g));
			}
			if (d.getAddedEdges().size() != added) {
				throw new InvalidFormatException("expected " + added
						+ " added edges, found " + d.getAddedEdges().size());
			}

			while ((line = reader.readString()) != null) {
				d.addRemovedEdge(OldEdge.fromString(line, g));
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
