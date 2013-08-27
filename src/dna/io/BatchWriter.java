package dna.io;

import java.io.IOException;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.Batch;
import dna.updates.Update;

public class BatchWriter<N extends Node, E extends Edge> {

	public boolean write(Batch<E> b, String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			for (Update<E> u : (Iterable<Update<E>>) b.getAllUpdates()) {
				writer.writeln(u.getStringRepresentation());
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
