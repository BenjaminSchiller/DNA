package dna.graph.generators.network;

import java.io.IOException;
import java.text.ParseException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;

/**
 * Creates an empty network graph.
 * 
 * @author Rwilmes
 * 
 */
public class EmptyNetwork extends NetworkGraph {

	public EmptyNetwork(String name, GraphDataStructure gds, long timestampInit)
			throws IOException, ParseException {
		super(name, gds, timestampInit);
	}

	public EmptyNetwork(GraphDataStructure gds, long timestampInit)
			throws IOException, ParseException {
		this("EmptyNetwork", gds, timestampInit);
	}

	public EmptyNetwork(GraphDataStructure gds) throws IOException,
			ParseException {
		this("EmptyNetwork", gds, 0);
	}

	@Override
	public Graph generate() {
		return this.newGraphInstance();
	}

}
