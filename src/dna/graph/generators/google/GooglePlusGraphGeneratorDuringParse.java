package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.io.GraphWriter;
import dna.util.parameters.Parameter;

public class GooglePlusGraphGeneratorDuringParse {

	private static String IN = "# In list:";
	private static String OUT = "# Out list:";

	private Graph g;
	private BufferedReader in;
	private String foldername;
	private int nodeLabelCounter;
	private HashMap<String, Integer> mapping;
	private HashMap<Integer, Long> lastSeen;
	private HashMap<Integer, Integer> count;
	private GraphNodeAdditionType type;
	private GraphDataStructure ds;

	public GooglePlusGraphGeneratorDuringParse(String name,
			GraphDataStructure ds, String foldername,
			GraphNodeAdditionType type, Parameter[] parameters) {
		this.ds = ds;
		this.foldername = foldername;
		this.mapping = new HashMap<>();
		this.lastSeen = new HashMap<>();
		this.count = new HashMap<>();
		this.nodeLabelCounter = 0;
		this.type = type;
		this.g = ds.newGraphInstance(name, 0, 1000000, 1000000);
	}

	public boolean writeMappingToFile(String dir, String filename) {
		return MappingWriter.write(this.getDto(), dir, filename);
	}

	public Graph parseGraph() throws IOException {
		final File folder = new File(foldername);
		parseFolder(folder);
		return g;
	}

	private void parseFolder(final File folder) throws IOException {
		int count = 0;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				count = 0;
				System.out.println(fileEntry.getName());
				parseFolder(fileEntry);
			} else {
				System.out.println(count++);
				parseFile(fileEntry);
			}
		}

	}

	private void parseFile(File file) throws IOException {

		FileReader reader = new FileReader(file);
		in = new BufferedReader(reader);
		String string;

		// /parse User
		in.readLine();
		string = in.readLine();
		DirectedNode user = getNodeFromString(string);

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			DirectedNode dst = getNodeFromString(string);
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(user,
					dst);
			if (g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}

		while ((string = in.readLine()) != null) {
			DirectedNode src = getNodeFromString(string);
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(src,
					user);
			if (!g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}
	}

	private DirectedNode getNodeFromString(String string) {
		DirectedNode node;
		String nodeID = string.split(";;;")[0];
		if (mapping.containsKey(nodeID)) {
			node = (DirectedNode) g.getNode(mapping.get(nodeID));
		} else {
			node = (DirectedNode) this.ds.newNodeInstance(nodeLabelCounter);
			mapping.put(nodeID, node.getIndex());
			lastSeen.put(node.getIndex(), g.getTimestamp());
			count.put(node.getIndex(), 1);
			nodeLabelCounter++;
			g.addNode(node);
		}
		return node;
	}

	public MappingDto getDto() {
		return new MappingDto(mapping, count, lastSeen, nodeLabelCounter,
				g.getName());
	}

	public boolean writeGraphToFile(String dir, String filename) {
		return GraphWriter.write(g, dir, filename);
	}
}
