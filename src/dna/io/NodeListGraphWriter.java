package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;

public class NodeListGraphWriter {
	public static boolean write(Graph g, String dir, String filename,
			boolean incIndex) throws IOException {
		Writer w = new Writer(dir, filename);
		for (IElement n_ : g.getNodes()) {
			if (incIndex) {
				w.writeln(((Node) n_).getIndex() + 1);
			} else {
				w.writeln(((Node) n_).getIndex());
			}
		}
		w.close();
		return true;
	}
}
