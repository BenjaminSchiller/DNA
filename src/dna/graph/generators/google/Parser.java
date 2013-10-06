package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;

public class Parser {
	private static String IN = "# In list:";
	private static String OUT = "# Out list:";

	BufferedReader in;
	GraphDataStructure ds;
	HashMap<String, DirectedNode> nodes = new HashMap<>();
	HashSet<DirectedEdge> edges = new HashSet<DirectedEdge>();
	HashMap<String, Integer> mapping = new HashMap<>();
	int nodeLabelCounter = 0;

	public Parser() {
		this.ds = new GraphDataStructure(DArray.class, DArrayList.class,
				DArrayList.class, DirectedNode.class, DirectedEdge.class);
	}

	public void parseFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				parseFolder(fileEntry);
			} else {
				parseFile(fileEntry);
			}
		}
	}

	private void parseFile(File fileEntry) {
		try {

			FileReader reader = new FileReader(fileEntry);
			in = new BufferedReader(reader);
			String string;

			in.readLine();
			string = in.readLine();
			DirectedNode user = getNodeFromString(string);

			while ((string = in.readLine()).equals(OUT)) {

			}

			while ((string = in.readLine()).equals(IN)) {

				DirectedNode dst = getNodeFromString(string);

				DirectedEdge edge = (DirectedEdge) ds
						.newEdgeInstance(user, dst);
				if (!edges.contains(edge)) {
					edges.add(edge);
					edge.connectToNodes();
				}
			}

			while ((string = in.readLine()) != null) {

				DirectedNode src = getNodeFromString(string);
				DirectedEdge edge = (DirectedEdge) ds
						.newEdgeInstance(src, user);
				if (!edges.contains(edge)) {
					edges.add(edge);
					edge.getSrc().addEdge(edge);
					edge.getDst().addEdge(edge);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private DirectedNode getNodeFromString(String string) {
		DirectedNode node;
		String nodeID = string.split(";;;")[0];
		if (mapping.containsKey(nodeID)) {
			node = nodes.get(mapping.get(nodeID));
		} else {
			node = (DirectedNode) this.ds.newNodeInstance(nodeLabelCounter);
			mapping.put(nodeID, node.getIndex());
			nodes.put(nodeID, node);
			nodeLabelCounter++;
		}
		return node;
	}

	public ParseDto parse(final String filename) {
		File folder = new File(filename);
		parseFolder(folder);
		return new ParseDto(mapping, nodes, edges, nodeLabelCounter, "");
	}

}
