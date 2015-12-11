package dna.metrics.streaM_k.rules.motifs;

import dna.metrics.streaM_k.rules.adjacencyMatrix.UndirectedAdjacencyMatrix;

/**
 * 
 * Representation of an undirected motif, i.e., a class of isomorph graphs of a
 * specific size. Each motif is identified by a unique index. In addition
 * 
 * @author benni
 *
 */
public class UndirectedMotif {
	private long index;
	private UndirectedAdjacencyMatrix am;

	public UndirectedMotif(long index, UndirectedAdjacencyMatrix am) {
		this.index = index;
		this.am = am;
	}

	/**
	 * 
	 * @return the unique index of this motif
	 */
	public long getIndex() {
		return this.index;
	}

	/**
	 * 
	 * @return a representative adjacency matrix of all isomorph graphs grouped
	 *         in this motif
	 */
	public UndirectedAdjacencyMatrix getRepresentative() {
		return this.am;
	}

	public String toString() {
		return "UM" + this.index + " (" + this.am.key + "): \n"
				+ this.am.toString("    ", " ", "1", "0");
	}
}
