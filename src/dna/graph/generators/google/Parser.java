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

public class Parser implements IDtoForDatabase {
	BufferedReader in;
	GraphDataStructure ds;
	HashMap<String, DirectedNode> nodes = new HashMap<>();
	HashSet<DirectedEdge> edges = new HashSet<DirectedEdge>();
	HashMap<String, Integer> mapping = new HashMap<>();
	HashMap<DirectedNode, Integer> count = new HashMap<>();
	HashMap<DirectedNode, Long> lastSeen = new HashMap<>();
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
			String[] inputs;
			DirectedNode user;
			boolean inNodes = false;
			boolean outNodes = false;
			in.readLine();
			string = in.readLine();
			inputs = string.split(";;;");
			if (mapping.containsKey(inputs[0])) {
				user = nodes.get(inputs[0]);
			} else {
				user = (DirectedNode) ds.newNodeInstance(nodeLabelCounter);
				mapping.put(inputs[0], nodeLabelCounter);
				nodeLabelCounter++;
				nodes.put(inputs[0], user);
			}

			while ((string = in.readLine()) != null) {

				if (!outNodes && string.contains("Out list:")) {
					outNodes = true;
					continue;
				}
				if (!inNodes && outNodes && string.contains("In list:")) {
					inNodes = true;
					outNodes = false;
					continue;
				}

				if (outNodes && !inNodes) {
					inputs = string.split(";;;");
					DirectedNode dst;
					if (mapping.containsKey(inputs[0])) {
						dst = nodes.get(inputs[0]);
					} else {
						dst = (DirectedNode) ds
								.newNodeInstance(nodeLabelCounter);
						mapping.put(inputs[0], nodeLabelCounter);

						nodeLabelCounter++;
						nodes.put(inputs[0], dst);
					}
					DirectedEdge edge = (DirectedEdge) ds.newEdgeInstance(user,
							dst);
					if (!edges.contains(edge)) {
						edges.add(edge);
						edge.getSrc().addEdge(edge);
						edge.getDst().addEdge(edge);
					}
					continue;
				}

				if (!outNodes && inNodes) {
					DirectedNode src;
					if (mapping.containsKey(inputs[0])) {
						src = nodes.get(inputs[0]);
					} else {
						src = (DirectedNode) ds
								.newNodeInstance(nodeLabelCounter);
						mapping.put(inputs[0], nodeLabelCounter);

						nodeLabelCounter++;
						nodes.put(inputs[0], src);
					}
					DirectedEdge edge = (DirectedEdge) ds.newEdgeInstance(src,
							user);
					if (!edges.contains(edge)) {
						edges.add(edge);
						edge.getSrc().addEdge(edge);
						edge.getDst().addEdge(edge);
					}
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

	public Dto parse(final String filename) {
		File folder = new File(filename);
		parseFolder(folder);
		return new Dto(nodes, edges, mapping, count, lastSeen,
				nodeLabelCounter, "");
	}

	@Override
	public Dto getDto() {
		return new Dto(nodes, edges, mapping, count, lastSeen,
				nodeLabelCounter, "");
	}
}
