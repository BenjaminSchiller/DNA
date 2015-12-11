package dna.metrics.streaM_k.rules.adjacencyMatrix;

/**
 * 
 * Representation of an adjacency matrix, i.e., an |V| x |V| matrix of boolean
 * values. It represents a directred graph since edges are differentiated, i.e.,
 * (a,b) != (b,a).
 * 
 * The main purpose of the class is to provide the storage of the values and
 * allow their manipulation by adding or removing edges. Further capabilities
 * like, e.g., determining if the graph is weakly or strongly connected are then
 * added by the extensions.
 * 
 * @author benni
 *
 */
public abstract class AdjacencyMatrix {
	public boolean[][] matrix;

	public int key;

	public AdjacencyMatrix(int nodes, int key) {
		this.matrix = new boolean[nodes][nodes];
		this.key = key;
		this.fillMatrix();
	}

	protected abstract void fillMatrix();

	public abstract void addEdge(int n1, int n2);

	public abstract void removeEdge(int n1, int n2);

	public abstract boolean hasEdge(int n1, int n2);

	public int getNodeCount() {
		return this.matrix.length;
	}

	public abstract int getEdgeCount();

	public String toString() {
		return this.toString("", " ", "1", "0");
	}

	public String toString(String pre, String sep, String edge, String noEdge) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < this.matrix.length; i++) {
			if (i == 0) {
				buff.append(pre);
			} else {
				buff.append("\n" + pre);
			}
			for (int j = 0; j < this.matrix[i].length; j++) {
				if (j > 0) {
					buff.append(sep);
				}
				buff.append((this.matrix[i][j] ? edge : noEdge));
			}
		}
		return buff.toString();
	}
}
