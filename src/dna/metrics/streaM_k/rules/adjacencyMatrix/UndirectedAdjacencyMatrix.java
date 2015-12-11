package dna.metrics.streaM_k.rules.adjacencyMatrix;

import java.util.LinkedList;

/**
 * 
 * An extension of the default adjacency matrix for representing undirected
 * graphs, i.e., (a,b) == (b,a) == {a,b}.
 * 
 * The key describes the content of an adjacency matrix. This integer
 * representation is interpreted as a binary representation of all relevant
 * fields of the matrix.
 * 
 * For example, the adjacency matrix of an undirected 3-vertex graph with
 * vertices a, b, and c looks as follows:<br>
 * (0 x y)<br>
 * (x 0 z)<br>
 * (y z 0)
 * 
 * Here, x, y, and z denote if the edges {a,b}, {a,c}, and {b,c} exist. Hence,
 * the complete information can be represented as xyz where each value is a
 * binary 0 or 1.
 * 
 * Hence, the key 7 = 111 denotes the adjacency matrix (011)(101)(110) while 5 =
 * 101 denotes the matrix (010)(101)(010).
 * 
 * @author benni
 *
 */
public class UndirectedAdjacencyMatrix extends AdjacencyMatrix {

	public UndirectedAdjacencyMatrix(int nodes, int key) {
		super(nodes, key);
	}

	@Override
	protected void fillMatrix() {
		int temp = key;
		for (int i = matrix.length - 1; i >= 0; i--) {
			for (int j = matrix.length - 1; j > i; j--) {
				// System.out.println(i + " / " + j);
				if ((temp % 2) == 1) {
					this.addEdge(i, j);
				}
				temp = temp >> 1;
			}
		}
	}

	/**
	 * 
	 * returns the maximum number of edges possible for an undirected graph with
	 * the specified number of nodes (i.e., nodes * (nodes-1) / 2).
	 * 
	 * @param nodes
	 *            number of nodes
	 * @return maximum number of edges
	 */
	public static int getMaxEdges(int nodes) {
		return (int) nodes * (nodes - 1) / 2;
	}

	/**
	 * 
	 * returns the maximum key possible for the specified number of nodes (i.e.,
	 * 2^{maxEdges} - 1).
	 * 
	 * @param nodes
	 *            number of nodes
	 * @return maximum possible key
	 */
	public static int getMaxKey(int nodes) {
		return (int) Math.pow(2, getMaxEdges(nodes)) - 1;
	}

	/**
	 * 
	 * @return true, if the represented undirected graph is connected
	 */
	public boolean isConnected() {
		boolean[] reached = new boolean[this.matrix.length];
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(0);
		reached[0] = true;
		while (!queue.isEmpty()) {
			int current = queue.poll();
			for (int i = 0; i < this.matrix.length; i++) {
				if (i == current) {
					continue;
				}
				if ((this.matrix[current][i] || this.matrix[i][current])
						&& !reached[i]) {
					queue.add(i);
					reached[i] = true;
				}
			}
		}
		int sum = 0;
		for (boolean r : reached) {
			sum += r ? 1 : 0;
		}
		return sum == this.matrix.length;
	}

	/**
	 * 
	 * tests weather this adjacecny matrix is isomorph to the given one.
	 * 
	 * @param am
	 *            adjacency matrix to compare to
	 * @return true in case both adjacency matrices are isomorph
	 */
	public boolean isIsomorph(UndirectedAdjacencyMatrix am) {
		return Isomorphism.isIsomorph(this, am);
	}

	@Override
	public void addEdge(int n1, int n2) {
		this.matrix[n1][n2] = true;
		this.matrix[n2][n1] = true;
	}

	@Override
	public void removeEdge(int n1, int n2) {
		this.matrix[n1][n2] = false;
		this.matrix[n2][n1] = false;
	}

	@Override
	public boolean hasEdge(int n1, int n2) {
		return this.matrix[n1][n2] || this.matrix[n2][n1];
	}

	@Override
	public int getEdgeCount() {
		int edges = 0;
		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = i + 1; j < this.matrix.length; j++) {
				edges += this.matrix[i][j] ? 1 : 0;
			}
		}
		return edges;
	}
}
