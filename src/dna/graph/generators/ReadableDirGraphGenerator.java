package dna.graph.generators;

import java.io.FilenameFilter;
import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.io.GraphReader;
import dna.util.IOUtils;

/**
 * 
 * Graph generator that reads the graphs from a given directory, i.e., to
 * generate the next graph, the next file from the specified directory is read
 * and returned. In case more graphs are generated than there are files, the
 * generation starts again at the first file. If no GraphDataStructure is given,
 * the one specified in the respective graph is used. Please not that files are
 * sorted using Arrays.sort(...) which influences the order in which graph are
 * generated.
 * 
 * @author benni
 * 
 */
public class ReadableDirGraphGenerator extends GraphGenerator {

	private String dir;

	private String[] filenames;

	private int index;

	/**
	 * 
	 * @param dir
	 *            directory containing the graph files
	 * @throws IOException
	 */
	public ReadableDirGraphGenerator(String dir) throws IOException {
		this(dir, null, null);
	}

	/**
	 * 
	 * @param dir
	 *            directory containing the graph files
	 * @param filter
	 *            filter to specify which files to taks as input
	 * @throws IOException
	 */
	public ReadableDirGraphGenerator(String dir, FilenameFilter filter)
			throws IOException {
		this(dir, filter, null);
	}

	/**
	 * 
	 * @param dir
	 *            directory containing the graph files
	 * @param gds
	 *            GraphDataStructure to be used when reading in the graphs
	 * @throws IOException
	 */
	public ReadableDirGraphGenerator(String dir, GraphDataStructure gds)
			throws IOException {
		this(dir, null, gds);
	}

	/**
	 * 
	 * @param dir
	 *            directory containing the graph files
	 * @param filter
	 *            filter to specify which files to taks as input
	 * @param gds
	 *            GraphDataStructure to be used when reading in the graphs
	 * @throws IOException
	 */
	public ReadableDirGraphGenerator(String dir, FilenameFilter filter,
			GraphDataStructure gds) throws IOException {
		super(GraphReader.readName(dir, IOUtils.getFilenames(dir, filter)[0]),
				null, gds, -1, -1, -1);
		this.dir = dir;
		this.filenames = IOUtils.getFilenames(dir, filter);
		index = -1;
	}

	@Override
	public Graph generate() {
		this.index = (this.index + 1) % this.filenames.length;
		try {
			return GraphReader.read(this.dir, this.filenames[this.index]);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
