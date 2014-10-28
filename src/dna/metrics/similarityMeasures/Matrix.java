package dna.metrics.similarityMeasures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.nodes.Node;

/**
 * A two-dimensional matrix.
 * 
 * @param <Node>
 *            The keys of columns and rows (e.g. {@link Integer}).
 * @param <Double>
 *            The matrix entries (e.g. {@link Integer}).
 */
public class Matrix {

	// <ROW, <COLUMN, value>
	private Map<Node, Map<Node, Double>> values;

	/**
	 * Creates an empty {@link Matrix}.
	 */
	public Matrix() {
		this.values = new HashMap<Node, Map<Node, Double>>();
	}

	public boolean equals(Matrix m, double faultTolerance) {
		boolean equals;
		for (Node element1 : this.values.keySet()) {
			for (Node element2 : this.values.keySet()) {
				if ((this.values.get(element1).get(element2) == null && m.values
						.get(element1).get(element2) == null)
						|| (this.values.get(element1).get(element2) == null && m.values
								.get(element1).get(element2) == 0.0)
						|| ((Double) this.values.get(element1).get(element2) == 0.0 && m.values
								.get(element1).get(element2) == null))
					equals = true;
				else
					equals = (Math
							.abs((m.values.get(element1).get(element2) - (Double) this.values
									.get(element1).get(element2))) <= faultTolerance);
				if (!equals) {
					return false;
				}
			}
		}
		return true;

	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Matrix)
			return ((Matrix) o).values.equals(this.values);

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
	public Double get(Node row, Node column) {
		// triangular matrix
		if (row.getIndex() < column.getIndex()) {
			Node tmp = row;
			row = column;
			column = tmp;
		}
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

	public double getRowSum(Node row) {
		double sum = 0.0;
		for (Node node : this.values.keySet())
			sum = get(row, node) == null ? sum : sum + get(row, node);
		return sum;
	}

	/**
	 * Inserts given value at given row and column(triangular matrix). If given row or column does
	 * not exist yet, it will be created.
	 * 
	 * @param row
	 *            The row where to insert the given value.
	 * @param column
	 *            The column where to insert the given value.
	 * @param value
	 *            The value to insert.
	 */
	public void put(Node row, Node column, Double value) {
		if (row.getIndex() < column.getIndex()) {
			Node tmp = row;
			row = column;
			column = tmp;
		}

		if (this.values.containsKey(row))
			this.values.get(row).put(column, value);
		else {
			final Map<Node, Double> newRow = new HashMap<Node, Double>();
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
	public void removeColumn(Node column) {
		for (Node row : this.values.keySet())
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
	public void removeEntry(Node row, Node column) {
		if (this.values.containsKey(row))
			this.values.get(row).remove(column);
	}

	/**
	 * Removes a row with all its entries.
	 * 
	 * @param row
	 *            The row to remove.
	 */
	public void removeRow(Node row) {
		this.values.remove(row);
	}

	/**
	 * Returns a string representation of this {@link Matrix} for all current
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
	 * @return The entries of this {@link Matrix}.
	 */
	@Override
	public String toString() {
		String oneRow = "";
		String entry = "";
		String output = "";
		HashMap<Integer, String> rowS = new HashMap<Integer, String>();
		HashMap<Integer, String> columnS;

		for (Node row : this.values.keySet()) {
			columnS = new HashMap<Integer, String>();
			for (Node column : this.values.get(row).keySet()) {
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
