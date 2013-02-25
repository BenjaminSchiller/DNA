package dna.metrics.triangles.open;

import dna.graph.Graph;
import dna.graph.Node;

public class OpenTriangle {
	public OpenTriangle(Node origin, Node from, Node to)
			throws InvalidOpenTriangleException {
		this.origin = origin;
		this.from = from;
		this.to = to;
		if (origin == from || origin == to || from == to) {
			throw new InvalidOpenTriangleException(this);
		}
	}

	private Node origin;

	private Node from;

	private Node to;

	public Node getOrigin() {
		return this.origin;
	}

	public Node getFrom() {
		return this.from;
	}

	public Node getTo() {
		return this.to;
	}

	public String toString() {
		return "T(" + this.origin.getIndex() + "," + this.from.getIndex() + ","
				+ this.to.getIndex() + ")";
	}

	public String getStringRepresentation() {
		return this.origin.getIndex() + "|" + this.from.getIndex() + "->"
				+ this.to.getIndex();
	}

	public static OpenTriangle fromString(String s, Graph g)
			throws NumberFormatException, InvalidOpenTriangleException {
		String[] temp1 = s.split("|");
		String[] temp2 = temp1[1].split("->");
		return new OpenTriangle(g.getNode(Integer.parseInt(temp1[0])),
				g.getNode(Integer.parseInt(temp2[0])), g.getNode(Integer
						.parseInt(temp2[1])));
	}

	public boolean equals(Object o) {
		return o != null
				&& o instanceof OpenTriangle
				&& this.origin.getIndex() == ((OpenTriangle) o).getOrigin()
						.getIndex()
				&& this.from.getIndex() == ((OpenTriangle) o).getFrom()
						.getIndex()
				&& this.to.getIndex() == ((OpenTriangle) o).getTo().getIndex();
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}

	public static boolean isValid(int origin, int from, int to) {
		return origin != from && origin != to && from != to;
	}
}
