package dna.parallel.util;

import java.io.File;
import java.io.IOException;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.io.GraphReader;

/**
 * 
 * Graph generator that reads a graph from a specified file. Hence, every
 * generated graph is the same. If no GraphDataStructure is given, the one
 * specified in the respective graph is used.
 * 
 * In contrast to the regular ReadableFileGraph, the generator waits (as
 * specified by the parameters) until the file is actually written.
 * 
 * @author benni
 * 
 */
public class ReadableFileWaitingGraph extends GraphGenerator {

	private String dir;

	private String filename;

	protected Sleeper sleeper;

	/**
	 * 
	 * @param dir
	 *            directory containing the graph file
	 * @param filename
	 *            name of the graph file
	 * @throws IOException
	 */
	public ReadableFileWaitingGraph(String dir, String filename, Sleeper sleeper)
			throws IOException {
		this(dir, filename, sleeper, null);
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
	public ReadableFileWaitingGraph(String dir, String filename,
			Sleeper sleeper, GraphDataStructure gds) throws IOException {
		super(null, null, gds, -1, -1, -1);
		this.dir = dir;
		this.filename = filename;
		this.sleeper = sleeper;
	}

	@Override
	public IGraph generate() {
		this.sleeper.reset();
		while (!this.sleeper.isTimedOut()) {
			if (!(new File(this.dir + this.filename)).exists()) {
				this.sleeper.sleep();
				continue;
			}
			try {
				if (this.gds == null) {
					return GraphReader.read(this.dir, this.filename);
				} else {
					return GraphReader.read(this.dir, this.filename, this.gds);
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		throw new IllegalStateException("could not read graph from " + this.dir
				+ this.filename);
	}

}
