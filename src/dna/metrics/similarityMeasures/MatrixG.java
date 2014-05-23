package dna.metrics.similarityMeasures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.nodes.Node;

/**
 * A two-dimensional matrix.
 * 
 * @param <U>
 *            The keys of columns and rows (e.g. {@link Integer}).
 * @param <V>
 *            The matrix entries (e.g. {@link Integer}).
 */
public class MatrixG<U, V> {

	// <ROW, <COLUMN, value>
	private Map<U, Map<U, V>> values;

	/**
	 * Creates an empty {@link MatrixG}.
	 */
	public MatrixG() {
		this.values = new HashMap<U, Map<U, V>>();
	}

	public boolean equals(MatrixG<Node, Double> m, double faultTolerance) {
		boolean equals;
		for (U element1 : this.values.keySet()) {
			for (U element2 : this.values.keySet()) {
				if ((this.values.get(element1).get(element2) == null && m.values
						.get(element1).get(element2) == 0.0)
						|| ((Double) this.values.get(element1).get(element2) == 0.0 && m.values
								.get(element1).get(element2) == null))
					equals = true;
				else
					equals = ((m.values.get(element1).get(element2) - (Double) this.values
							.get(element1).get(element2)) <= faultTolerance);
				if (!equals) {
					return false;
				}
			}
		}
		return true;

	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof MatrixG)
			return ((MatrixG<?, ?>) o).values.equals(this.values);

		return false;
	}

	/**
	 * @param row
	 *            The row of the matrix entry to return.
	 * @param column
	 *            The column of the matrix entry to return.
	 * @return The matrix entry at given row and column or {@code null} if
	 *         either given row, given column or entry at this position doesn't
	 *         exist.
	 */
	public V get(U row, U column) {
		if (this.values.containsKey(row))
			if (this.values.get(row).containsKey(column))
				return this.values.get(row).get(column);

		return null;
	}

	/**
	 * @param The
	 *            string from which we get the index.
	 * 
	 * @return The Index of a Node
	 */
	private int getNodeNumber(String positionString) {
		String node = "";
		int iPos = positionString.indexOf(' ');
		node = positionString.substring(0, iPos).trim();
		return Integer.parseInt(node);

	}

	/**
	 * Inserts given value at given row and column. If given row or column does
	 * not exist yet, it will be created.
	 * 
	 * @param row
	 *            The row where to insert the given value.
	 * @param column
	 *            The column where to insert the given value.
	 * @param value
	 *            The value to insert.
	 */
	public void put(U row, U column, V value) {
		if (this.values.containsKey(row))
			this.values.get(row).put(column, value);
		else {
			final Map<U, V> newRow = new HashMap<U, V>();
			newRow.put(column, value);
			this.values.put(row, newRow);
		}
	}

	/**
	 * Removes a column with all its entries.
	 * 
	 * @param column
	 *            The column to remove.
	 */
	public void removeColumn(U column) {
		for (U row : this.values.keySet())
			this.values.get(row).remove(column);
	}

	/**
	 * Removes the entry at given row and column.
	 * 
	 * @param row
	 *            The row of the entry to remove.
	 * @param column
	 *            The column of the entry to remove.
	 */
	public void removeEntry(U row, U column) {
		if (this.values.containsKey(row))
			this.values.get(row).remove(column);
	}

	/**
	 * Removes a row with all its entries.
	 * 
	 * @param row
	 *            The row to remove.
	 */
	public void removeRow(U row) {
		this.values.remove(row);
	}

	/**
	 * Returns a string representation of this {@link MatrixG} for all current
	 * nodes of the graph E.g.:
	 * 
	 * <p>
	 * 0|0:2, 0|1:0, 0|2:2, 0|3:0
	 * </p>
	 * <p>
	 * 1|0:0, 1|1:2, 1|2:0, 1|3:2
	 * </p>
	 * <p>
	 * 2|0:2, 2|1:0, 2|2:2, 2|3:0
	 * </p>
	 * <p>
	 * 3|0:0, 3|1:2, 3|2:0, 3|3:2
	 * </p>
	 * 
	 * 
	 * @return The entries of this {@link MatrixG}.
	 */
	@Override
	public String toString() {
		String oneRow = "";
		String entry = "";
		String output = "";
		HashMap<Integer, String> rowS = new HashMap<Integer, String>();
		HashMap<Integer, String> columnS;

		for (U row : this.values.keySet()) {
			columnS = new HashMap<Integer, String>();
			for (U column : this.values.get(row).keySet()) {
				entry = getNodeNumber(row.toString()) + "|"
						+ getNodeNumber(column.toString()) + ":"
						+ this.values.get(row).get(column).toString();
				columnS.put(this.getNodeNumber(column.toString()), entry);

			}
			oneRow = (columnS.values().toString().substring(0, columnS.values()
					.toString().length() - 1)).substring(1);
			rowS.put(this.getNodeNumber(row.toString()), oneRow);
		}
		for (Entry<Integer, String> element : rowS.entrySet()) {
			output = output + element.getValue() + ";\n";
		}
		return output;
	}

}
