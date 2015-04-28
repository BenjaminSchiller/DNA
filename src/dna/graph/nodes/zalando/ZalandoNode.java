package dna.graph.nodes.zalando;

import java.util.Arrays;

import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.nodes.Node;
import dna.util.Log;

public class ZalandoNode {

	private ZalandoNode() {
	} // every method is static, no initialization intended

	/**
	 * Transforms the given string to a array of {@link EventColumn}s. This
	 * method is mandatory for writing Zalando nodes to files and parse them
	 * again.
	 * 
	 * <p>
	 * For instance, the string <i>$SKU$</i> represents the type <i>
	 * {@link EventColumn#SKU}</i> and the string <i>$SKU$AKTION$</i> represents
	 * the type <i>[{@link EventColumn#SKU}, {@link EventColumn#AKTION}]</i>.
	 * </p>
	 * 
	 * @param s
	 *            The so called "type" of a Zalando node as string
	 *            concatenation.
	 * @return The type of a Zalando node.
	 * 
	 * @see #eventColumnsArrayToString(EventColumn[]) the reverse operation
	 *      (array to string)
	 */
	protected static EventColumn[] eventColumnsStringToArray(String s) {
		final String[] s1 = s.substring(1, s.length() - 1).split("\\$");
		final EventColumn[] e = new EventColumn[s1.length];
		for (int i = 0; i < s1.length; i++) {
			e[i] = EventColumn.valueOf(s1[i]);
		}
		return e;
	}

	/**
	 * Transforms the given array of {@link EventColumn}s to a string. This
	 * method is mandatory for writing Zalando nodes to files and parse them
	 * again.
	 * 
	 * <p>
	 * For instance, the type <i>{@link EventColumn#SKU}</i> is represented by
	 * the string <i>$SKU$</i> and the type <i>[{@link EventColumn#SKU},
	 * {@link EventColumn#AKTION}]</i> is represented by the string
	 * <i>$SKU$AKTION$</i>.
	 * </p>
	 * 
	 * @param e
	 *            The so called "type" of a Zalando node.
	 * @return The type of a Zalando node as string concatenation.
	 * 
	 * @see #eventColumnsStringToArray(String) the reverse operation (string to
	 *      array)
	 */
	protected static String eventColumnsArrayToString(EventColumn[] e) {
		String s = "$";
		for (int i = 0; i < e.length; i++) {
			s += e[i].toString() + "$";
		}
		return s;
	}

	/**
	 * Checks whether both of the given nodes are Zalando nodes of equal type.
	 * 
	 * @param node1
	 *            Any {@link Node}. Comparison is only possible if {@code node1}
	 *            is a {@link IZalandoNode}.
	 * @param node2
	 *            Any {@link Node}. Comparison is only possible if {@code node2}
	 *            is a {@link IZalandoNode}.
	 * @return True if and only if both nodes are instances of
	 *         {@link IZalandoNode} and {@link IZalandoNode#getType()} of both
	 *         nodes are equal arrays, else false.
	 */
	public static boolean equalType(Node node1, Node node2) {
		if (!(node1 instanceof IZalandoNode)) {
			Log.error("Given node "
					+ node1
					+ " is no node for Zalando graphs. Unable to check equality.");
			return false;
		} else if (!(node2 instanceof IZalandoNode)) {
			Log.error("Given node "
					+ node1
					+ " is no node for Zalando graphs. Unable to check equality.");
			return false;
		} else {
			return Arrays.equals(((IZalandoNode) node1).getType(),
					((IZalandoNode) node2).getType());
		}
	}

	/**
	 * Checks whethe the given node type equals the given type.
	 * 
	 * @param node
	 *            Any {@link Node}.
	 * @return True if and only if the ndoe is an instance of
	 *         {@link IZalandoNode} and {@link IZalandoNode#getType()} equals
	 *         the given type, else false.
	 */
	public static boolean nodeIsOfType(Node node, EventColumn[] type) {
		if (!(node instanceof IZalandoNode)) {
			return false;
		} else {
			return Arrays.equals(((IZalandoNode) node).getType(), type);
		}
	}

}
