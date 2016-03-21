package dna.graph.generators.util;

import java.io.IOException;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;

/**
 *
 * Graph generator that reads a graph from a specified file. Hence, every
 * generated graph is the same. If no GraphDataStructure is given, the one
 * specified in the respective graph is used.
 * Once the graph has been generated for the first time, the generate
 * function will always return the same value.
 *
 * @author Matthias
 *
 */
public class RepeatableReadableFileGraph extends ReadableFileGraph {

	private IGraph currentGraph = null;

	/**
	 *
	 * @param dir
	 *            directory containing the graph file
	 * @param filename
	 *            name of the graph file
	 * @throws IOException
	 */
	public RepeatableReadableFileGraph(String dir, String filename) throws IOException {
		super(dir, filename, null);
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
	public RepeatableReadableFileGraph(String dir, String filename, GraphDataStructure gds)
			throws IOException {
		super(dir, filename, gds);
	}

	@Override
	public IGraph generate() {
		if (this.currentGraph == null)
			this.currentGraph = super.generate();

		return this.currentGraph;
	}

}
