package dna.io;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;

public class EdgeListWriter {

	public static boolean write(Graph g, String dir, String filename,
			String separator, boolean addFirstLineNM) {
		return write(g, dir, filename, separator, addFirstLineNM, false, "", "");
	}

	public static boolean write(Graph g, String dir, String filename,
			String separator, boolean addFirstLineNM, boolean incIndex,
			String prefix, String suffix) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			if (addFirstLineNM) {
				writer.writeln(g.getNodeCount() + separator + g.getEdgeCount());
			}

			for (IElement e : g.getEdges()) {
				if (e == null)
					continue;
				if (e instanceof UndirectedEdge) {
					UndirectedEdge edge = (UndirectedEdge) e;
					if (incIndex) {
						writer.writeln(prefix
								+ (edge.getNode1().getIndex() + 1) + separator
								+ (edge.getNode2().getIndex() + 1) + suffix);
					} else {
						writer.writeln(prefix + edge.getNode1().getIndex()
								+ separator + edge.getNode2().getIndex()
								+ suffix);
					}
				} else if (e instanceof DirectedEdge) {
					DirectedEdge edge = (DirectedEdge) e;
					if (incIndex) {
						writer.writeln(prefix + (edge.getSrc().getIndex() + 1)
								+ separator + (edge.getDst().getIndex() + 1)
								+ suffix);
					} else {
						writer.writeln(prefix + edge.getSrc().getIndex()
								+ separator + edge.getDst().getIndex() + suffix);
					}
				}
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
			}
		}
	}
}
