package dna.metrics.streaM_k.rules.motifs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.metrics.streaM_k.rules.adjacencyMatrix.UndirectedAdjacencyMatrix;

/**
 * 
 * This class represents all undirected motifs of a specific size. It provides a
 * mapping of all adjacency matrix keys to the respective motif. Hence, the
 * motif of a given key can be obtained.
 * 
 * In addition, it allows the retrieval of all motifs as well as the total count
 * of connected adjacency matrices (ams).
 * 
 * Note that the key of unconnected adjacency matrices is not mapped as it does
 * not represent a valid motif. Hence, the mapping for such a key return null.
 * 
 * @author benni
 *
 */
public class UndirectedMotifs {
	private HashMap<Integer, UndirectedMotif> map;

	private Set<UndirectedMotif> motifs;

	private long ams;

	private int nodes;

	/**
	 * 
	 * @return the set of all stored motifs
	 */
	public Set<UndirectedMotif> getMotifs() {
		return this.motifs;
	}

	/**
	 * 
	 * @param key
	 *            key representing a specific adjacency matrix
	 * @return motif of the references adjency matrix
	 */
	public UndirectedMotif getMotif(int key) {
		return this.map.get(key);
	}

	/**
	 * 
	 * @return set of all mapped adjacency matrix keys, i.e., the set of all
	 *         connected graphs of size nodes
	 */
	public Set<Integer> getAMKeys() {
		return this.map.keySet();
	}

	/**
	 * this initializes the representation of all motifs of the specified size.
	 * note that mapping and sets are not filled during initialization. this has
	 * to be done by adding motifs / keys using the add method.
	 * 
	 * @param nodes
	 *            size of the motifs to be stored
	 */
	public UndirectedMotifs(int nodes) {
		this.map = new HashMap<Integer, UndirectedMotif>();
		this.motifs = new HashSet<UndirectedMotif>();
		this.ams = 0;
		this.nodes = nodes;
	}

	/**
	 * 
	 * @return total number of motifs
	 */
	public long getMotifCount() {
		return this.motifs.size();
	}

	/**
	 * 
	 * @return total number of connected graphs with the specified number of
	 *         vertices
	 */
	public long getAMCount() {
		return this.ams;
	}

	/**
	 * in case the given adjacency matrix is isomorph to an existing motif (that
	 * has already be added) this motif is returned and the adjacenc matrix's
	 * key is added to the mapping.
	 * 
	 * in case the adjacenc matrix is not isomorph to any existing motif, a new
	 * motif (with mapping) is added and returned.
	 * 
	 * @param am
	 *            adjacency matrix
	 * @return motif of the given adjacency matrix
	 */
	public UndirectedMotif add(UndirectedAdjacencyMatrix am) {
		if (this.map.containsKey(am.key)) {
			return this.map.get(am.key);
		}
		for (UndirectedMotif m : this.motifs) {
			if (m.getRepresentative().isIsomorph(am)) {
				this.map.put(am.key, m);
				this.ams++;
				return m;
			}
		}
		UndirectedMotif m = new UndirectedMotif(this.motifs.size() + 1, am);
		this.map.put(am.key, m);
		this.motifs.add(m);
		this.ams++;
		return m;
	}

	public String toString() {
		return this.nodes + "-vertex motifs (" + this.map.keySet().size()
				+ " / " + this.map.values().size() + ")";
	}

	/**
	 * 
	 * generates an instance of this class for the specified graph / motif size
	 * 
	 * @param nodes
	 *            graph size
	 * @return instance for the specified motif size
	 */
	public static UndirectedMotifs generate(int nodes) {
		UndirectedMotifs ums = new UndirectedMotifs(nodes);
		long maxKey = UndirectedAdjacencyMatrix.getMaxKey(nodes);
		for (int key = 0; key <= maxKey; key++) {
			UndirectedAdjacencyMatrix am = new UndirectedAdjacencyMatrix(nodes,
					key);
			if (am.isConnected()) {
				ums.add(am);
			}
		}
		return ums;
	}
}
