package dna.graph.generators.network;

import java.io.IOException;
import java.text.ParseException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.io.network.NetworkEvent;

/**
 * Abstract class for a graph generator that generates a graph based on network
 * events.
 * 
 * @author Rwilmes
 * 
 */
public abstract class NetworkGraph extends GraphGenerator {

	protected boolean finished;

	protected NetworkEvent bufferedEvent;

	public NetworkGraph(String name, GraphDataStructure gds,
			long timestampInit, String dir, String filename)
			throws IOException, ParseException {
		this(name, gds, timestampInit);
	}

	public NetworkGraph(String name, GraphDataStructure gds, long timestampInit)
			throws IOException, ParseException {
		super(name, null, gds, timestampInit, 0, 0);
	}

	@Override
	public abstract Graph generate();
}
