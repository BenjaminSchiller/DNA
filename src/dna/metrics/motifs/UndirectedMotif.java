package dna.metrics.motifs;

import java.util.Arrays;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.motifs.exceptions.UndirectedMotifDeletingOnlyEdgeException;
import dna.metrics.motifs.exceptions.UndirectedMotifInvalidEdgeAdditionException;
import dna.metrics.motifs.exceptions.UndirectedMotifInvalidEdgeRemovalException;
import dna.metrics.motifs.exceptions.UndirectedMotifSplittingException;

public class UndirectedMotif {
	public static enum UndirectedMotifType {
		PRE1, PRE2, PRE3, UM1, UM2, UM3, UM4, UM5, UM6
	};

	private UndirectedNode a;

	private UndirectedNode b;

	private UndirectedNode c;

	private UndirectedNode d;

	private UndirectedMotifType type;

	public UndirectedNode getA() {
		return a;
	}

	public UndirectedNode getB() {
		return b;
	}

	public UndirectedNode getC() {
		return c;
	}

	public UndirectedNode getD() {
		return d;
	}

	public UndirectedMotifType getType() {
		return this.type;
	}

	public boolean has2Nodes() {
		return this.c == null && this.d == null;
	}

	public boolean has3Nodes() {
		return this.c != null && this.d == null;
	}

	public boolean has4Nodes() {
		return this.c != null && this.d != null;
	}

	public int getIndex() {
		switch (this.type) {
		case PRE1:
			return 8;
		case PRE2:
			return 9;
		case PRE3:
			return 10;
		case UM1:
			return 1;
		case UM2:
			return 2;
		case UM3:
			return 3;
		case UM4:
			return 4;
		case UM5:
			return 5;
		case UM6:
			return 6;
		default:
			return 0;
		}
	}

	public int getEdgeCount() {
		switch (this.type) {
		case PRE1:
			return 1;
		case PRE2:
			return 2;
		case PRE3:
			return 3;
		case UM1:
			return 3;
		case UM2:
			return 3;
		case UM3:
			return 4;
		case UM4:
			return 4;
		case UM5:
			return 5;
		case UM6:
			return 6;
		default:
			return -1;
		}
	}

	public UndirectedMotif(UndirectedNode a, UndirectedNode b) {
		this.a = a;
		this.b = b;
		this.c = null;
		this.d = null;
		this.type = UndirectedMotifType.PRE1;
	}

	public UndirectedMotif(UndirectedNode a, UndirectedNode b,
			UndirectedNode c, UndirectedMotifType type) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = null;
		this.type = type;
	}

	public UndirectedMotif(UndirectedNode a, UndirectedNode b,
			UndirectedNode c, UndirectedNode d, UndirectedMotifType type) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.type = type;
	}

	public UndirectedMotif addEdge(UndirectedEdge e)
			throws UndirectedMotifInvalidEdgeAdditionException {
		switch (this.type) {
		case PRE1:
			if (e.isConnectedTo(a) && !e.isConnectedTo(b)) {
				// this.type = UndirectedMotifType.PRE2;
				// this.c = e.getDifferingNode(this.a);
				return new UndirectedMotif(a, b, e.getDifferingNode(a), null,
						UndirectedMotifType.PRE2);
			} else if (e.isConnectedTo(this.b) && !e.isConnectedTo(this.a)) {
				// this.type = UndirectedMotifType.PRE2;
				// this.c = e.getDifferingNode(this.b);
				// this.changeAB();
				return new UndirectedMotif(b, a, e.getDifferingNode(b), null,
						UndirectedMotifType.PRE2);
			}
		case PRE2:
			if (e.isConnectedTo(b, c)) {
				// this.type = UndirectedMotifType.PRE3;
				// return true;
				return new UndirectedMotif(a, b, c, null,
						UndirectedMotifType.PRE3);
			} else if (e.isConnectedTo(a) && !e.isConnectedTo(b)
					&& !e.isConnectedTo(c)) {
				// this.d = e.getDifferingNode(this.a);
				// this.type = UndirectedMotifType.UM2;
				return new UndirectedMotif(a, b, c, e.getDifferingNode(this.a),
						UndirectedMotifType.UM2);
			} else if (e.isConnectedTo(this.b) && !e.isConnectedTo(this.a)
					&& !e.isConnectedTo(this.c)) {
				// this.d = e.getDifferingNode(this.b);
				// this.type = UndirectedMotifType.UM1;
				return new UndirectedMotif(a, b, c, e.getDifferingNode(this.b),
						UndirectedMotifType.UM1);
			} else if (e.isConnectedTo(this.c) && !e.isConnectedTo(this.a)
					&& !e.isConnectedTo(this.b)) {
				// this.d = e.getDifferingNode(this.c);
				// UndirectedNode a, b, c, d;
				// a = this.a;
				// b = this.b;
				// c = this.c;
				// d = this.d;
				// this.a = c;
				// this.b = a;
				// this.c = d;
				// this.d = b;
				// this.type = UndirectedMotifType.UM1;
				return new UndirectedMotif(c, a, e.getDifferingNode(c), b,
						UndirectedMotifType.UM1);
			}
		case PRE3:
			if (e.isConnectedTo(this.a) && !e.isConnectedTo(b)
					&& !e.isConnectedTo(c)) {
				// this.type = UndirectedMotifType.UM4;
				// this.d = e.getDifferingNode(this.a);
				// this.changeCD();
				return new UndirectedMotif(a, b, e.getDifferingNode(a), c,
						UndirectedMotifType.UM4);
			} else if (e.isConnectedTo(this.b) && !e.isConnectedTo(a)
					&& !e.isConnectedTo(c)) {
				// this.type = UndirectedMotifType.UM4;
				// this.d = e.getDifferingNode(this.b);
				// this.changeAB();
				// this.changeCD();
				return new UndirectedMotif(b, a, e.getDifferingNode(b), c,
						UndirectedMotifType.UM4);
			} else if (e.isConnectedTo(this.c) && !e.isConnectedTo(a)
					&& !e.isConnectedTo(b)) {
				// this.type = UndirectedMotifType.UM4;
				// this.d = e.getDifferingNode(this.c);
				// this.changeCD();
				// this.changeAD();
				return new UndirectedMotif(c, b, e.getDifferingNode(this.c), a,
						UndirectedMotifType.UM4);
			}
		case UM1:
			if (e.isConnectedTo(this.c, this.d)) {
				this.type = UndirectedMotifType.UM3;
				return this;
				// return new UndirectedMotif(a, b, c, d,
				// UndirectedMotifType.UM3);
			} else if (e.isConnectedTo(this.a, this.d)) {
				this.type = UndirectedMotifType.UM4;
				return this;
				// return new UndirectedMotif(a, b, c, d,
				// UndirectedMotifType.UM4);
			} else if (e.isConnectedTo(this.b, this.c)) {
				this.type = UndirectedMotifType.UM4;
				this.changeAB();
				this.changeCD();
				return this;
				// return new UndirectedMotif(b, a, d, c,
				// UndirectedMotifType.UM4);
			}
		case UM2:
			if (e.isConnectedTo(this.b, this.d)) {
				this.type = UndirectedMotifType.UM4;
				return this;
				// return new UndirectedMotif(a, b, c, d,
				// UndirectedMotifType.UM4);
			} else if (e.isConnectedTo(this.b, this.c)) {
				this.type = UndirectedMotifType.UM4;
				this.changeCD();
				return this;
				// return new UndirectedMotif(a, b, d, c,
				// UndirectedMotifType.UM4);
			} else if (e.isConnectedTo(this.c, this.d)) {
				this.type = UndirectedMotifType.UM4;
				this.changeBC();
				return this;
				// return new UndirectedMotif(a, c, b, d,
				// UndirectedMotifType.UM4);
			}
		case UM3:
			if (e.isConnectedTo(this.b, this.c)) {
				this.type = UndirectedMotifType.UM5;
				this.changeAB();
				this.changeCD();
				return this;
				// return new UndirectedMotif(b, a, d, c,
				// UndirectedMotifType.UM5);
			} else if (e.isConnectedTo(this.a, this.d)) {
				this.type = UndirectedMotifType.UM5;
				return this;
				// return new UndirectedMotif(a, b, c, d,
				// UndirectedMotifType.UM5);
			}
		case UM4:
			if (e.isConnectedTo(this.c, this.d)) {
				this.type = UndirectedMotifType.UM5;
				return this;
				// return new UndirectedMotif(a, b, c, d,
				// UndirectedMotifType.UM5);
			} else if (e.isConnectedTo(this.b, this.c)) {
				this.type = UndirectedMotifType.UM5;
				this.changeBD();
				return this;
				// return new UndirectedMotif(a, d, c, b,
				// UndirectedMotifType.UM5);
			}
		case UM5:
			if (e.isConnectedTo(b, c)) {
				this.type = UndirectedMotifType.UM6;
				return this;
				// return new UndirectedMotif(a, b, c, d,
				// UndirectedMotifType.UM6);
			}
		case UM6:
		default:
		}
		throw new UndirectedMotifInvalidEdgeAdditionException(this, e);
	}

	public boolean removeEdge(UndirectedEdge e)
			throws UndirectedMotifInvalidEdgeRemovalException,
			UndirectedMotifDeletingOnlyEdgeException,
			UndirectedMotifSplittingException {
		switch (this.type) {
		case PRE1:
			if (e.isConnectedTo(this.a, this.b)) {
				throw new UndirectedMotifDeletingOnlyEdgeException(this, e);
			}
		case PRE2:
			if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.PRE1;
				this.b = this.c;
				this.c = null;
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.PRE1;
				this.c = null;
				return true;
			}
		case PRE3:
			if (e.isConnectedTo(this.b, this.c)) {
				this.type = UndirectedMotifType.PRE2;
				return true;
			} else if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.PRE2;
				this.changeAC();
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.PRE2;
				this.changeAB();
				return true;
			}
		case UM1:
			if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.PRE2;
				this.c = this.a;
				this.a = this.b;
				this.b = this.d;
				this.d = null;
				return true;
			} else if (e.isConnectedTo(this.a, this.b)) {
				throw new UndirectedMotifSplittingException(this, e);
			} else if (e.isConnectedTo(this.b, this.d)) {
				this.type = UndirectedMotifType.PRE2;
				this.d = null;
				return true;
			}
		case UM2:
			if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.PRE2;
				this.changeBD();
				this.d = null;
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.PRE2;
				this.changeCD();
				this.d = null;
				return true;
			} else if (e.isConnectedTo(this.a, this.d)) {
				this.type = UndirectedMotifType.PRE2;
				this.d = null;
				return true;
			}
		case UM3:
			if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.UM1;
				this.changeAC();
				this.changeBD();
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.UM1;
				this.changeAD();
				return true;
			} else if (e.isConnectedTo(this.b, this.d)) {
				this.type = UndirectedMotifType.UM1;
				this.changeBC();
				return true;
			} else if (e.isConnectedTo(this.c, this.d)) {
				this.type = UndirectedMotifType.UM1;
				return true;
			}
		case UM4:
			if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.UM1;
				this.changeBD();
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.PRE3;
				this.changeCD();
				this.d = null;
				return true;
			} else if (e.isConnectedTo(this.a, this.d)) {
				this.type = UndirectedMotifType.UM1;
				return true;
			} else if (e.isConnectedTo(this.b, this.d)) {
				this.type = UndirectedMotifType.UM2;
				return true;
			}
		case UM5:
			if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.UM4;
				this.changeBC();
				this.changeAD();
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.UM4;
				this.changeAD();
				return true;
			} else if (e.isConnectedTo(this.a, this.d)) {
				this.type = UndirectedMotifType.UM3;
				return true;
			} else if (e.isConnectedTo(this.b, this.d)) {
				this.type = UndirectedMotifType.UM4;
				this.changeBC();
				return true;
			} else if (e.isConnectedTo(this.c, this.d)) {
				this.type = UndirectedMotifType.UM4;
				return true;
			}
		case UM6:
			if (e.isConnectedTo(this.a, this.b)) {
				this.type = UndirectedMotifType.UM5;
				this.changeAC();
				return true;
			} else if (e.isConnectedTo(this.a, this.c)) {
				this.type = UndirectedMotifType.UM5;
				this.changeAB();
				return true;
			} else if (e.isConnectedTo(this.a, this.d)) {
				this.type = UndirectedMotifType.UM5;
				this.changeAC();
				this.changeBD();
				return true;
			} else if (e.isConnectedTo(this.b, this.c)) {
				this.type = UndirectedMotifType.UM5;
				return true;
			} else if (e.isConnectedTo(this.b, this.d)) {
				this.type = UndirectedMotifType.UM5;
				this.changeCD();
				return true;
			} else if (e.isConnectedTo(this.c, this.d)) {
				this.type = UndirectedMotifType.UM5;
				this.changeBD();
				return true;
			}
		default:
		}
		throw new UndirectedMotifInvalidEdgeRemovalException(this, e);
	}

	private void changeAB() {
		UndirectedNode a, b;
		a = this.a;
		b = this.b;
		this.a = b;
		this.b = a;
	}

	private void changeAC() {
		UndirectedNode a, c;
		a = this.a;
		c = this.c;
		this.a = c;
		this.c = a;
	}

	private void changeAD() {
		UndirectedNode a, d;
		a = this.a;
		d = this.d;
		this.a = d;
		this.d = a;
	}

	private void changeBC() {
		UndirectedNode b, c;
		b = this.b;
		c = this.c;
		this.b = c;
		this.c = b;
	}

	private void changeBD() {
		UndirectedNode b, d;
		b = this.b;
		d = this.d;
		this.b = d;
		this.d = b;
	}

	private void changeCD() {
		UndirectedNode c, d;
		c = this.c;
		d = this.d;
		this.c = d;
		this.d = c;
	}

	public String toString() {
		return this.type + "." + this.getNodes();
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public String getNodes() {
		StringBuffer buff = new StringBuffer(this.a.getIndex() + "."
				+ this.b.getIndex());
		if (this.c != null) {
			buff.append("." + this.c.getIndex());
		}
		if (this.d != null) {
			buff.append("." + this.d.getIndex());
		}
		return buff.toString();
	}

	public int getNodesHashCode() {
		return getHashCode(a, b, c, d);
	}

	public static int getHashCode(UndirectedNode a, UndirectedNode b,
			UndirectedNode c, UndirectedNode d) {
		return getHashCode(a.getIndex(), b.getIndex(), c != null ? c.getIndex()
				: -1, d != null ? d.getIndex() : -2);
	}

	public static int getHashCode(int a, int b, int c, int d) {
		int[] values = new int[] { a, b, c, d };
		Arrays.sort(values);
		return (values[0] + "." + values[1] + "." + values[2] + "." + values[3])
				.hashCode();
	}

	public String asString() {
		switch (this.type) {
		case PRE1:
			return "(-1) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n          \n          ";
		case PRE2:
			return "(-2) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n     |    \n     " + this.c.getIndex() + "    ";
		case PRE3:
			return "(-3) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n     | /  \n     " + this.c.getIndex() + "    ";
		case UM1:
			return "(1) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n    |   |\n    " + this.c.getIndex() + "   "
					+ this.d.getIndex();
		case UM2:
			return "(2) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n    | \\  \n    " + this.c.getIndex() + "   "
					+ this.d.getIndex();
		case UM3:
			return "(3) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n    |   |\n    " + this.c.getIndex() + " - "
					+ this.d.getIndex();
		case UM4:
			return "(4) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n    | \\ |\n    " + this.c.getIndex() + "   "
					+ this.d.getIndex();
		case UM5:
			return "(5) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n    | \\ |\n    " + this.c.getIndex() + " - "
					+ this.d.getIndex();
		case UM6:
			return "(6) " + this.a.getIndex() + " - " + this.b.getIndex()
					+ "\n    | X |\n    " + this.c.getIndex() + " - "
					+ this.d.getIndex();
		default:
			return null;

		}
	}

	public static String getTransformationString(String s1, String s2,
			boolean success) {
		String[] t1 = s1.split("\n");
		String[] t2 = s2.split("\n");
		StringBuffer buff = new StringBuffer();
		buff.append(t1[0] + "           " + t2[0] + "\n");
		if (success) {
			buff.append(t1[1] + "   ====>   " + t2[1] + "\n");
		} else {
			buff.append(t1[1] + "   =/=>    " + t2[1] + "\n");
		}
		buff.append(t1[2] + "           " + t2[2]);
		return buff.toString();
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof UndirectedMotif)) {
			return false;
		}
		UndirectedMotif um = (UndirectedMotif) obj;
		if (um.getType() != this.type) {
			return false;
		}
		return a.equals(um.a) && b.equals(um.b)
				&& (c == null || c.equals(um.c))
				&& (d == null || d.equals(um.d));
	}
}
