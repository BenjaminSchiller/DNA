package dna.io;

import java.io.IOException;

import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.Config;

public class BatchWriter {

	public static boolean write(Batch b, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(Config.get("BATCH_KEYWORD_FROM"));
			writer.writeln(b.getFrom());

			writer.writeKeyword(Config.get("BATCH_KEYWORD_TO"));
			writer.writeln(b.getTo());

			writer.writeKeyword(Config.get("BATCH_KEYWORD_UPDATES"));

			for (Update u : b.getAllUpdates()) {
				writer.writeln(u.asString());
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}
