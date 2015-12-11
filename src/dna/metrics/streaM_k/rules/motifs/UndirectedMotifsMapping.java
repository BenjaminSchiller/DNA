package dna.metrics.streaM_k.rules.motifs;

import java.io.IOException;

import dna.io.Reader;
import dna.io.Writer;
import dna.metrics.streaM_k.rules.adjacencyMatrix.UndirectedAdjacencyMatrix;
import dna.util.ArrayUtils;

/**
 * 
 * this class provides a light-weight mapping from adjacency matrix keys to
 * motif index. in contrast to UndirectedMotifs, no instances of
 * UndirectedMotif, UndirectedMotifs, or UndirectedAdjacencyMatrix are created.
 * 
 * the mapping consists of an int array. its index is considered as the key of
 * adjacency matrices and the value stored as the respective motif index. the
 * value of adjacency matrix keys that represent unconnected graphs is 0, i.e.,
 * an invalid motif index.
 * 
 * this class provides functionalities similar to UndirectedMotifs but decreases
 * memory consumption and mapping lookup.
 * 
 * the mapping can be stored info a file and later read from it which removes
 * the runtime complexity of performing isomorphism checks before obtaining a
 * mapping.
 * 
 * @author benni
 *
 */
public class UndirectedMotifsMapping {

	public static final String nodesPre = "nodes: ";
	public static final String maxKeyPre = "maxKey: ";
	public static final String motifsPre = "motifs: ";
	public static final String sep = " ";

	private int nodes;
	private int[] mapping;
	private int motifs;

	public int getNodes() {
		return this.nodes;
	}

	public int[] getMapping() {
		return this.mapping;
	}

	public int getMotifsCount() {
		return this.motifs;
	}

	public int getAmsCount() {
		int counter = 0;
		for (int key : this.mapping) {
			if (key > 0) {
				counter++;
			}
		}
		return counter;
	}

	public int getMotif(int key) {
		return this.mapping[key];
	}

	/**
	 * for each motif, identified by its index, the smallest key of all
	 * adjacency matrices of that isomorphism class is returned.
	 * 
	 * @return representative adjacency matrix key for each motif
	 */
	public int[] getRepresentatives() {
		int[] reps = new int[mapping[mapping.length - 1] + 1];
		for (int i = 0; i < mapping.length; i++) {
			if (reps[mapping[i]] == 0) {
				reps[mapping[i]] = i;
			}
		}
		reps[0] = 0;
		return reps;
	}

	/**
	 * 
	 * initialization of all parameters, no further processing is reuired (in
	 * contrast to the related representation in UndirectedMotifs).
	 * 
	 * @param nodes
	 *            size of the graphs
	 * @param motifs
	 *            total number of motifs
	 * @param mapping
	 *            mapping of adjacency matrix keys to motif index
	 */
	public UndirectedMotifsMapping(int nodes, int motifs, int[] mapping) {
		this.nodes = nodes;
		this.motifs = motifs;
		this.mapping = mapping;
	}

	/**
	 * 
	 * writes this mapping to the filesystem
	 * 
	 * @param dir
	 *            directory where to store the file
	 * @param filename
	 *            filename of the file to be written
	 * @throws IOException
	 */
	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		w.writeln(nodesPre + this.nodes);
		w.writeln(maxKeyPre + (this.mapping.length - 1));
		w.writeln(motifsPre + this.motifs);
		for (int i = 0; i < this.mapping.length; i++) {
			if (this.mapping[i] > 0) {
				w.writeln(i + sep + this.mapping[i]);
			}
		}
		w.close();
	}

	/**
	 * 
	 * reads a mapping from the filesystem
	 * 
	 * @param dir
	 *            directory where the file is stored
	 * @param filename
	 *            filename of the file to be read
	 * @return mapping read from the file
	 * @throws IOException
	 */
	public static UndirectedMotifsMapping read(String dir, String filename)
			throws IOException {
		Reader r = new Reader(dir, filename);
		int nodes = Integer.parseInt(r.readString().replace(nodesPre, ""));
		int maxKey = Integer.parseInt(r.readString().replace(maxKeyPre, ""));
		int motifs = Integer.parseInt(r.readString().replace(motifsPre, ""));
		int[] mapping = new int[maxKey + 1];
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(sep);
			int key = Integer.parseInt(temp[0]);
			int motifIndex = Integer.parseInt(temp[1]);
			mapping[key] = motifIndex;
		}
		r.close();
		return new UndirectedMotifsMapping(nodes, motifs, mapping);
	}

	/**
	 * 
	 * generates a mapping (from scratch) for the specified graph / motif size.
	 * 
	 * @param nodes
	 *            graph size
	 * @return generated mapping
	 */
	public static UndirectedMotifsMapping generate(int nodes) {
		UndirectedMotifs ums = new UndirectedMotifs(nodes);
		long maxKey = UndirectedAdjacencyMatrix.getMaxKey(nodes);
		int[] mapping = new int[(int) maxKey + 1];

		for (int key = 0; key <= maxKey; key++) {
			UndirectedAdjacencyMatrix am = new UndirectedAdjacencyMatrix(nodes,
					key);
			if (am.isConnected()) {
				mapping[key] = (int) ums.add(am).getIndex();
			}
		}

		return new UndirectedMotifsMapping(nodes, ArrayUtils.max(mapping),
				mapping);
	}
}
