package dna.io;

import java.io.IOException;
import java.util.HashMap;

import com.sun.media.sound.InvalidFormatException;

import dna.graph.Graph;
import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.edges.Edge;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Config;

/**
 * A reader for graphs in the SNAP format
 * 
 * @author Benedict Jahn
 * 
 */
public class SNAPGraphReader {

	private static GraphDataStructure directedGDSSetup = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList, DArray.class,
					ListType.GlobalEdgeList, DHashMap.class,
					ListType.LocalEdgeList, DArrayList.class),
			DirectedNode.class, DirectedEdge.class);

	private static GraphDataStructure undirectedGDSSetup = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList, DArray.class,
					ListType.GlobalEdgeList, DHashMap.class,
					ListType.LocalEdgeList, DArrayList.class),
			UndirectedNode.class, UndirectedEdge.class);

	/**
	 * Creates a graph out of an file with a SNAP based graph
	 * 
	 * @param dir
	 *            directory where the SNAP graph file lays
	 * @param filename
	 *            the name of the file containing the SNAP graph
	 * @return the graph
	 * @throws IOException
	 *             in case the file does not exist there
	 */
	public static Graph read(String dir, String filename) throws IOException {
		return read(dir, filename, null);
	}

	/**
	 * 
	 * @param dir
	 *            directory where the SNAP graph file lays
	 * @param filename
	 *            the name of the file containing the SNAP graph
	 * @param ds
	 *            the data structure the constructed graph shall have
	 * @return the name as a string
	 * @throws IOException
	 *             in case the file does not exist there
	 */
	public static Graph read(String dir, String filename, GraphDataStructure ds)
			throws IOException {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		// For the case that the IDs are higher than 2.147.483.648, use long
		// instead
		// HashMap<Long, Integer> map = new HashMap<Long, Integer>();

		// Creates the reader
		Reader reader = new Reader(dir, filename);
		Reader.skipComments = false;

		// Check if the SNAP graph is directed or undirected and creates a GDS
		// if necessary
		String structure = reader.readString();
		if (ds == null) {
			if (structure.contains(Config.get("SNAP_GRAPH_KEYWORD_DIRECTED"))) {

				ds = directedGDSSetup;
			} else if (structure.contains(Config
					.get("SNAP_GRAPH_KEYWORD_UNDIRECTED"))) {

				ds = undirectedGDSSetup;
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
		System.out.println(name);
		System.out.println(nodeCount);
		System.out.println(edgeCount);
		// Reads and adds the edges
		String line = reader.readString();
		int nodeID = 0;
		double percentage = 0.05;
		if (line.contains(Config.get("SNAP_GRAPH_KEYWORD_EDGES_LIST"))) {
			while ((line = reader.readString()) != null) {
				// Notification system
				if (((double) nodeID / (double) nodeCount) >= percentage) {
					System.out.println("Reading: "
							+ Math.round(percentage * 100) + "% finished.");
					percentage += 0.05;
				}

				int tabIndex = line.indexOf('\t');
				int srcIndex = Integer.parseInt(line.substring(0, tabIndex));
				int destIndex = Integer.parseInt(line.substring(tabIndex + 1,
						line.length()));

				// For the case the IDs are too long to be seperated through a
				// tab use this instead
				// int tabIndex = line.indexOf(' ');

				// For the case you use the <long, int> HashMap
				// long srcIndex = Long.parseLong(line.substring(0, tabIndex));
				// long destIndex = Long.parseLong(line.substring(tabIndex + 1,
				// line.length()));

				Node src;
				Node dest;
				if (map.containsKey(srcIndex)) {
					src = g.getNode(map.get(srcIndex));
				} else {
					map.put(srcIndex, nodeID);
					src = ds.newNodeInstance(nodeID);
					g.addNode(src);
					nodeID++;
				}
				if (map.containsKey(destIndex)) {
					dest = g.getNode(map.get(destIndex));
				} else {
					map.put(destIndex, nodeID);
					dest = ds.newNodeInstance(nodeID);
					g.addNode(dest);
					nodeID++;
				}
				Edge e = ds.newEdgeInstance(src, dest);
				g.addEdge(e);
				e.connectToNodes();

			}
		}

		// Closes the reader, returns the graph
		reader.close();
		Reader.skipComments = true;
		return g;
	}

	/**
	 * Reads the name of the SNAP graph
	 * 
	 * @param dir
	 *            directory where the SNAP graph file lays
	 * @param filename
	 *            the name of the file containing the SNAP graph
	 * @return the name as a string
	 * @throws IOException
	 *             in case the file does not exist there
	 */
	public static String readName(String dir, String filename)
			throws IOException {
		Reader reader = new Reader(dir, filename);
		Reader.skipComments = false;

		reader.readString();
		String name = reader.readString();
		name = name.substring(name.indexOf(" ") + 1);

		reader.close();
		Reader.skipComments = true;
		return name;
	}

}
