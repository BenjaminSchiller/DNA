package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.updates.batch.Batch;
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

			String line = null;
			while ((line = reader.readString()) != null) {
				System.out.println(line);
				b.add(Update.fromString(g.getGraphDatastructures(), g, line));
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
