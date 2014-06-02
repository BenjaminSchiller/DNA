package dna.graph.generators.zalando;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link HashMap} where the values are {@link HashSet}s.
 * 
 * @param <K>
 *            The keys of the {@code HashMap}.
 * @param <V>
 *            The values of each {@code HashSet} in the {@code HashMap}.
 */
class HashSetMap<K, V> {

	private HashMap<K, HashSet<V>> map;

	/**
	 * Creates an empty {@link HashSetMap}.
	 */
	HashSetMap() {
		this.map = new HashMap<K, HashSet<V>>();
	}

	/**
	 * Adds given value to the set of values for given key. If given key is a
	 * new one, a new set with only the given value will be created.
	 * 
	 * @param key
	 *            The key to identify the set to which the given value should be
	 *            added.
	 * @param value
	 *            The value which should be added to the set identified by given
	 *            key.
	 */
	void add(K key, V value) {
		if (this.map.containsKey(key))
			this.map.get(key).add(value);
		else {
			final HashSet<V> set = new HashSet<V>();
			set.add(value);
			this.map.put(key, set);
		}
	}

	/**
	 * @param key
	 *            The key to check.
	 * @return True if the given key is one of the keys in this
	 *         {@code HashSetMap}, else false.
	 */
	boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	/**
	 * @param key
	 *            The key to check.
	 * @param value
	 *            The value to check.
	 * @return True if the given key exists and the given value is in it's set,
	 *         else false.
	 */
	boolean containsValueForKey(K key, V value) {
		if (this.containsKey(key))
			return this.get(key).contains(value);

		return false;
	}

	/**
	 * @param key
	 *            The key to identify the set which should be returned.
	 * @return The set for given key or {@code null} if key does not exist.
	 */
	Set<V> get(K key) {
		return this.map.get(key);
	}

	/**
	 * This method is just like {@link #get(Object)} but the result set will not
	 * contain given value.
	 * 
	 * @param key
	 *            The key to identify the set which should be returned.
	 * @param exception
	 *            The returned set will not contain this value.
	 */
	Set<V> get(K key, V exception) {
		final Set<V> set = new HashSet<V>(this.get(key));
		set.remove(exception);
		return set;
	}

	/**
	 * Removes the set for given key. Nothing will happen, if given key is not
	 * in this {@link HashSetMap}. {@link #containsKey(Object)} will return
	 * false after this call.
	 * 
	 * @param key
	 *            The key of the set to remove.
	 */
	void remove(K key) {
		this.map.remove(key);
	}

	/**
	 * Removes the given value of the set identified by given key. If given key
	 * is not in this {@link HashSetMap} or given value is not in the set for
	 * given key, nothing will happen.
	 * 
	 * @param key
	 *            The key to identify the set where to remove the value.
	 * @param value
	 *            The value to remove from set identified by given key.
	 * @param removeEmptyKey
	 *            If and only if this is true, {@link #remove(Object)} is called
	 *            after the last value of the set.
	 */
	void remove(K key, V value, boolean removeEmptyKey) {
		if (this.map.containsKey(key)) {
			this.map.get(key).remove(value);

			if (removeEmptyKey && this.map.get(key).size() == 0)
				this.remove(key);
		}
	}

	/**
	 * @return The number of sets in the map.
	 */
	int size() {
		return this.map.size();
	}

	/**
	 * @param key
	 *            The key to identify the set which size should be returned.
	 * @return The number of elements in set identified by given key.
	 */
	int size(K key) {
		return this.get(key).size();
	}

	/**
	 * Returns a string representation of this {@link HashSetMap}. E.g.:
	 * <p>
	 * key1: value11, value12<br>
	 * key2: value21, value22, value23, value24<br>
	 * key3:
	 * </p>
	 * 
	 * @return A {@link String} with multiple lines (separated by {@code \n}).
	 *         Each line contains the key of one {@link HashSet} followed by a
	 *         colon and the values of the {@link HashSet} separated by ", ".
	 */
	@Override
	public String toString() {
		String s = "";

		for (K key : this.map.keySet()) {
			s += key.toString() + ": ";

			for (V value : this.map.get(key)) {
				s += value.toString() + ", ";
			}
			// remove ", " after the last value of the line
			s = s.substring(0, s.length() - 2);

			s += "\n";
		}

		return s.trim();
	}

}
