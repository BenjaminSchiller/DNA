package dna.io;

import java.io.IOException;

import com.sun.media.sound.InvalidFormatException;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.util.Config;

/**
 * @author Benedict
 * 
 */
public class SNAPGraphReader {

	private final static String directedGDSSetup = "GlobalNodeList=dna.graph.datastructures.DHashMap;GlobalEdgeList=dna.graph.datastructures.DHashMap;LocalNodeList=dna.graph.datastructures.DHashMap;LocalEdgeList=dna.graph.datastructures.DHashMap;LocalInEdgeList=dna.graph.datastructures.DHashMap;LocalOutEdgeList=dna.graph.datastructures.DHashMap;node=dna.graph.nodes.DirectedNode;edge=dna.graph.edges.DirectedEdge";
	private final static String undirectedGDSSetup = "GlobalNodeList=dna.graph.datastructures.DHashMap;GlobalEdgeList=dna.graph.datastructures.DHashMap;LocalNodeList=dna.graph.datastructures.DHashMap;LocalEdgeList=dna.graph.datastructures.DHashMap;LocalInEdgeList=dna.graph.datastructures.DHashMap;LocalOutEdgeList=dna.graph.datastructures.DHashMap;node=dna.graph.nodes.UndirectedNode;edge=dna.graph.edges.UndirectedEdge";

	public static Graph read(String dir, String filename) throws IOException {
		return read(dir, filename, null);
	}

	public static Graph read(String dir, String filename, GraphDataStructure ds)
			throws IOException {

		// Creates the reader
		Reader reader = new Reader(dir, filename);
		Reader.skipComments = false;

		// Check if the SNAP graph is directed or undirected and creates a GDS
		// if necesarry
		String structure = reader.readString();
		if (ds == null) {
			if (structure.contains(Config.get("SNAP_GRAPH_KEYWORD_DIRECTED"))) {

				ds = new GraphDataStructure(directedGDSSetup);
			} else if (structure.contains(Config
					.get("SNAP_GRAPH_KEYWORD_UNDIRECTED"))) {

				ds = new GraphDataStructure(undirectedGDSSetup);
			} else {

				throw new InvalidFormatException("Expected keyword '"
						+ Config.get("SNAP_GRAPH_KEYWORD_DIRECTED") + "' or '"
						+ Config.get("SNAP_GRAPH_KEYWORD_UNDIRECTED") + "'");
			}
		}

		// Gets the name of the SNAP graph
		String name = reader.readString();
		name = name.substring(name.indexOf(" ") + 1);

		// Gets the node count
		String nodeAndEdgeCount = reader.readString();
		int offset = nodeAndEdgeCount.indexOf(Config
				.get("SNAP_GRAPH_KEYWORD_NODE_COUNT"))
				+ Config.get("SNAP_GRAPH_KEYWORD_NODE_COUNT").length();
		nodeAndEdgeCount = nodeAndEdgeCount.substring(offset);
		int nodeCount = 0;
		while (nodeAndEdgeCount.charAt(nodeCount) != ' ') {
			nodeCount++;
		}
		nodeCount = Integer.parseInt(nodeAndEdgeCount.substring(0, nodeCount));

		// Gets the edge count
		offset = nodeAndEdgeCount.indexOf(Config
				.get("SNAP_GRAPH_KEYWORD_EDGE_COUNT"))
				+ Config.get("SNAP_GRAPH_KEYWORD_EDGE_COUNT").length();
		nodeAndEdgeCount = nodeAndEdgeCount.substring(offset);
		int edgeCount = Integer.parseInt(nodeAndEdgeCount);

		// Creates the graph
		Graph g = ds.newGraphInstance(name, 0, nodeCount, edgeCount);

		// Adds the nodes
		//for (int i = 0; i < nodeCount; i++) {
		//	g.addNode(ds.newNodeInstance(i));
		//}

		// Reads and adds the edges
		String line = reader.readString();
		if (line.contains(Config.get("SNAP_GRAPH_KEYWORD_EDGES_LIST"))) {
			while ((line = reader.readString()) != null) {
				int tabIndex = line.indexOf('\t');
				int srcIndex = Integer.parseInt(line.substring(0, tabIndex));
				int destIndex = Integer.parseInt(line.substring(tabIndex+1, line.length()));
				Node src = g.getNode(srcIndex);
				Node dest = g.getNode(destIndex);
				if(src == null){
					g.addNode(ds.newNodeInstance(srcIndex));
					src = g.getNode(srcIndex);
				}
				if(dest == null){
					g.addNode(ds.newNodeInstance(destIndex));
					dest = g.getNode(destIndex);
				}
				g.addEdge(ds.newEdgeInstance(src, dest));
			}
		}

		// Closes the reader, returns the graph
		reader.close();
		return g;
	}

	public static String readName(String dir, String filename)
			throws IOException {
		Reader reader = new Reader(dir, filename);
		Reader.skipComments = false;

		reader.readString();
		String name = reader.readString();
		name = name.substring(name.indexOf(" ") + 1);

		reader.close();
		return name;
	}

}
