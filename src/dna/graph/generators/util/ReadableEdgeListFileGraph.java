package dna.graph.generators.util;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.io.EdgeListGraphReader;
import dna.util.parameters.Parameter;

public class ReadableEdgeListFileGraph extends GraphGenerator {

	private String dir;

	private String filename;

	private String separator;

	private GraphDataStructure gds;

	public ReadableEdgeListFileGraph(String dir, String filename,
			String separator, GraphDataStructure gds) {
		super(filename, new Parameter[] {}, gds, 0, -1, -1);
		this.dir = dir;
		this.filename = filename;
		this.separator = separator;
		this.gds = gds;
	}

	@Override
	public Graph generate() {
		try {
			Graph g = EdgeListGraphReader.read(this.dir, this.filename,
					this.separator, this.gds);
			return g;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
