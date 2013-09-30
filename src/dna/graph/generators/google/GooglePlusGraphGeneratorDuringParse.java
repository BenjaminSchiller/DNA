package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.generators.directed.DirectedGraphGenerator;
import dna.graph.nodes.DirectedNode;
import dna.util.parameters.Parameter;

public class GooglePlusGraphGeneratorDuringParse extends DirectedGraphGenerator
		implements IDtoForDatabase {

	private BufferedReader in;
	private String foldername;
	private int nodeLabelCounter;
	private HashMap<String, Integer> mapping;
	private HashMap<DirectedNode, Long> lastSeen;
	private HashMap<DirectedNode, Integer> count;
	private HashMap<String, DirectedNode> nodes;
	private HashSet<DirectedEdge> edges;
	private GraphNodeAdditionType type;

	public GooglePlusGraphGeneratorDuringParse(String name,
			GraphDataStructure d, String foldername,
			GraphNodeAdditionType type, Parameter[] parameters) {
		super(name, parameters, d, 0L, 0, 0);
		this.foldername = foldername;
		this.mapping = new HashMap<>();
		this.lastSeen = new HashMap<>();
		this.count = new HashMap<>();
		this.edges = new HashSet<>();
		this.nodes = new HashMap<>();
		this.nodeLabelCounter = 0;
		this.type = type;
	}

	public int getNodeLabelCounter() {
		return nodeLabelCounter;
	}

	public HashMap<String, Integer> getMapping() {
		return mapping;
	}

	public HashMap<DirectedNode, Integer> getCount() {
		return count;
	}

	public HashMap<DirectedNode, Long> getNodesLastTimeSeen() {
		return lastSeen;
	}

	public void writeMappingToFile() {
		try {

			String content = "UserId;;;\t\t InternalMapping;;;\t\t counter;;; \n";
			for (String s : mapping.keySet()) {
				content += s + ";;;\t\t" + mapping.get(s) + ";;;\t\t "
						+ lastSeen.get(s) + "\n";
			}

			File file = new File(foldername + "\\Mapping");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();
		final File folder = new File(foldername);
		g = parseFolder(g, folder);
		return g;
	}

	public Graph parseFolder(Graph g, final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				g = parseFolder(g, fileEntry);
			} else {
				g = parseFile(g, fileEntry);
			}
		}
		return g;
	}

	private Graph parseFile(Graph g, File file) {
		try {
			FileReader reader = new FileReader(file);
			in = new BufferedReader(reader);
			String string;
			String[] inputs;
			DirectedNode user;
			boolean parseInNodes = false;
			boolean parseOutNodes = false;

			// /parse User
			in.readLine();
			string = in.readLine();
			inputs = string.split(";;;");
			if (mapping.containsKey(inputs[0])) {
				// if (type == GraphNodeAdditionType.AfterNTimes) {
				// return g;
				// }
				user = (DirectedNode) g.getNode(mapping.get(inputs[0]));
			} else {
				user = (DirectedNode) this.gds
						.newNodeInstance(nodeLabelCounter);
				// if (!count.keySet().contains(inputs[0])) {
				// count.put(user, 1);
				// }
				// if (type == GraphNodeAdditionType.AfterNTimes) {
				// return g;
				// }
				mapping.put(inputs[0], nodeLabelCounter);
				nodes.put(inputs[0], user);
				lastSeen.put(user, g.getTimestamp());
				nodeLabelCounter++;
				g.addNode(user);
			}

			while ((string = in.readLine()) != null) {
				if (!parseOutNodes && string.contains("Out list:")) {
					parseOutNodes = true;
					continue;
				}
				if (!parseInNodes && parseOutNodes
						&& string.contains("In list:")) {
					parseInNodes = true;
					parseOutNodes = false;
					continue;
				}

				if (parseOutNodes && !parseInNodes) {
					inputs = string.split(";;;");
					DirectedNode dst;
					if (mapping.containsKey(inputs[0])) {
						dst = (DirectedNode) g.getNode(mapping.get(inputs[0]));
					} else {
						dst = (DirectedNode) this.gds
								.newNodeInstance(nodeLabelCounter);
						mapping.put(inputs[0], nodeLabelCounter);
						lastSeen.put(dst, g.getTimestamp());
						nodes.put(inputs[0], dst);

						nodeLabelCounter++;
						g.addNode(dst);
					}
					DirectedEdge edge = (DirectedEdge) this.gds
							.newEdgeInstance(user, dst);
					if (g.containsEdge(edge)) {
						g.addEdge(edge);
						edges.add(edge);
						edge.getSrc().addEdge(edge);
						edge.getDst().addEdge(edge);
					}
					continue;
				}

				if (!parseOutNodes && parseInNodes) {
					inputs = string.split(";;;");
					DirectedNode src;
					if (mapping.containsKey(inputs[0])) {
						src = (DirectedNode) g.getNode(mapping.get(inputs[0]));
					} else {
						src = (DirectedNode) this.gds
								.newNodeInstance(nodeLabelCounter);
						mapping.put(inputs[0], nodeLabelCounter);
						lastSeen.put(src, g.getTimestamp());
						nodes.put(inputs[0], src);
						nodeLabelCounter++;
						g.addNode(src);
					}
					DirectedEdge edge = (DirectedEdge) this.gds
							.newEdgeInstance(src, user);
					if (!g.containsEdge(edge)) {
						g.addEdge(edge);
						edges.add(edge);
						edge.getSrc().addEdge(edge);
						edge.getDst().addEdge(edge);
					}
					continue;
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
		return g;
	}

	@Override
	public Dto getDto() {
		return new Dto(nodes, edges, mapping, count, lastSeen,
				nodeLabelCounter, this.getName());
	}
}
