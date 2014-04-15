package dna.io;

import java.io.IOException;

import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class BatchWriter {

	public static final String fromKeyword = "From";
	public static final String toKeyword = "To";
	public static final String updatesKeyword = "List of Updates";

	public static boolean write(Batch b, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(BatchWriter.fromKeyword);
			writer.writeln(b.getFrom());

			writer.writeKeyword(BatchWriter.toKeyword);
			writer.writeln(b.getTo());

			writer.writeKeyword(BatchWriter.updatesKeyword);

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
