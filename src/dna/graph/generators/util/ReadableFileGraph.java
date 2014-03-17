package dna.graph.generators.util;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.io.GraphReader;

/**
 * 
 * Graph generator that reads a graph from a specified file. Hence, every
 * generated graph is the same. If no GraphDataStructure is given, the one
 * specified in the respective graph is used.
 * 
 * @author benni
 * 
 */
public class ReadableFileGraph extends GraphGenerator {

	private String dir;

	private String filename;
	
	private Graph g;

	/**
	 * 
	 * @param dir
	 *            directory containing the graph file
	 * @param filename
	 *            name of the graph file
	 * @throws IOException
	 */
	public ReadableFileGraph(String dir, String filename) throws IOException {
		this(dir, filename, null);
	}

	/**
	 * 
	 * @param dir
	 *            directory containing the graph file
	 * @param filename
	 *            name of the graph file
	 * @param gds
	 *            GraphDataStructure to be used when reading in the graphs
	 * @throws IOException
	 */
	public ReadableFileGraph(String dir, String filename, GraphDataStructure gds)
			throws IOException {
		super(GraphReader.readName(dir, filename), null, gds, -1, -1, -1);
		this.dir = dir;
		this.filename = filename;
		try {
			if (this.gds == null) {
				this.g = GraphReader.read(this.dir, this.filename);
				this.gds = g.getGraphDatastructures();
			} else {
				this.g = GraphReader.read(this.dir, this.filename, this.gds);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Graph generate() {
		return g;
	}

}
