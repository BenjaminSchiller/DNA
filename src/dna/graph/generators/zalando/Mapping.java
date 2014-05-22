package dna.graph.generators.zalando;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Mapping of arbitrary values to <u>unique</u> {@code int}s >= 0.
 * 
 * @param <V>
 *            The values to be mapped.
 */
class Mapping<V> {

	private Map<V, Integer> map;

	/**
	 * Creates an {@link Mapping} without mapped values.
	 */
	Mapping() {
		this.map = new HashMap<V, Integer>();
	}

	/**
	 * @param value
	 *            The value to check.
	 * @return True if the given value is mapped, else false.
	 */
	boolean contains(V value) {
		return this.map.containsKey(value);
	}

	/**
	 * @param value
	 *            The value for which the mapping should be returned.
	 * @return The mapping of the given value or -1 if the value is not mapped.
	 */
	int getMapping(V value) {
		if (this.contains(value))
			return this.map.get(value);
		else
			return -1;
	}

	/**
	 * Maps the given value to an <u>unique</u> {@code int}s >= 0. If the given
	 * value was already mapped, it is not mapped to a new {@code int}. Two
	 * values are mapped to the same {@link int}, if both are
	 * {@link V#equals(Object)}.
	 * 
	 * @param value
	 *            The value to map.
	 */
	void map(V value) {
		if (!this.contains(value))
			this.map.put(value, this.map.size());
	}

	/**
	 * @return The number of mapped values.
	 */
	int size() {
		return this.map.size();
	}

	/**
	 * Returns a string representation of this {@link Mapping}. E.g.:
	 * <p>
	 * value1 -> 1, value2 -> 2, value3 -> 3
	 * </p>
	 * 
	 * @return A {@link String} with all mapped values and their corresponding
	 *         mapped {@code int}s.
	 */
	@Override
	public String toString() {
		String s = "";

		for (Entry<V, Integer> mapping : this.map.entrySet())
			s += mapping.getValue().toString() + " -> " + mapping.getValue();

		// remove ", " after the last value of the line
		return s.substring(0, s.length() - 2);
	}

}
