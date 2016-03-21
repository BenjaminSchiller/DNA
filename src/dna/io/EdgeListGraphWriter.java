package dna.io;

import java.io.IOException;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;

public class EdgeListGraphWriter {

	public enum InfoType {
		NONE, N, NM
	}

	public static boolean write(IGraph g, String dir, String filename,
			String separator, InfoType info, boolean addInverseEdge,
			boolean incIndex, String prefix, String suffix) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			switch (info) {
			case N:
				writer.writeln(g.getNodeCount());
				break;
			case NM:
				if (addInverseEdge
						&& g.getGraphDatastructures().getEdgeType()
								.isAssignableFrom(UndirectedEdge.class)) {
					writer.writeln(g.getNodeCount() + separator
							+ (g.getEdgeCount() * 2));
				} else {
					writer.writeln(g.getNodeCount() + separator
							+ g.getEdgeCount());
				}
				break;
			case NONE:
				break;
			default:
				break;
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
						if (addInverseEdge) {
							writer.writeln(prefix
									+ (edge.getNode2().getIndex() + 1)
									+ separator
									+ (edge.getNode1().getIndex() + 1) + suffix);
						}
					} else {
						writer.writeln(prefix + edge.getNode1().getIndex()
								+ separator + edge.getNode2().getIndex()
								+ suffix);
						if (addInverseEdge) {
							writer.writeln(prefix + edge.getNode2().getIndex()
									+ separator + edge.getNode1().getIndex()
									+ suffix);
						}
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
