package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.io.Writer;
import dna.util.Config;

public class EveryNodeParser {

	private static String IN = "# In list:";
	private static String OUT = "# Out list:";

	private String foldername;
	private int nodeLabelCounter;
	private HashMap<String, Integer> mapping;
	private HashSet<String> edges;
	private BufferedReader in;
	private Writer mappingWriter;
	private Writer graphWriter;
	private HashSet<Integer> seen;

	public boolean parse(String name, String indir, String outputDir,
			String dirName, int nodeLabelCounter, long timeStamp,
			HashMap<String, Integer> mapping) throws IOException {

		mappingWriter = new Writer(outputDir, "Mapping-" + dirName);

		this.foldername = indir + dirName;
		this.mapping = mapping;
		this.nodeLabelCounter = nodeLabelCounter;
		this.edges = new HashSet<String>(10000000);
		this.seen = new HashSet<>(10000);
		mappingWriter.writeln("UserId;;;InternalMapping");
		final File folder = new File(foldername);

		graphWriter = new Writer(outputDir, "Graph-" + dirName);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_NAME"));
		graphWriter.writeln(name);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_DATASTRUCTURES"));

		EnumMap<ListType, Class<? extends IDataStructure>> listTypes = GraphDataStructure
				.getList(ListType.GlobalNodeList, DHashMap.class,
						ListType.GlobalEdgeList, DHashSet.class,
						ListType.LocalEdgeList, DHashSet.class);
		GraphDataStructure ds = new GraphDataStructure(listTypes,
				DirectedNode.class, DirectedEdge.class);
		graphWriter.writeln(ds.getDataStructures());

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_NODE_COUNT"));
		graphWriter.writeln(mapping.size());

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_EDGE_COUNT"));
		graphWriter.writeln(edges.size());

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_TIMESTAMP"));
		graphWriter.writeln(timeStamp);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_NODES_LIST"));

		parseFolder(folder);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_EDGES_LIST"));
		for (String s : edges) {
			graphWriter.writeln(s);
		}
		graphWriter.close();

		mappingWriter.writeln("NODELABELCOUNTER");
		mappingWriter.writeln(this.nodeLabelCounter);
		mappingWriter.close();

		return true;
	}

	private void parseFolder(File folder) throws IOException {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("Parse Folder " + fileEntry.getName());
				parseFolder(fileEntry);
			} else {
				System.out.println("Parse File " + fileEntry.getName());
				parseFile(fileEntry);
			}
		}
	}

	private void parseFile(File file) throws IOException {
		FileReader reader = new FileReader(file);
		in = new BufferedReader(reader);
		String string;

		// /parse User
		string = in.readLine();
		if (!string.equals("# User:"))
			return;
		string = in.readLine();
		int user = getNodeFromString(string);

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			int dst = getNodeFromString(string);
			String e = user + Config.get("EDGE_DIRECTED_DELIMITER") + dst;
			if (!edges.contains(e)) {
				edges.add(e);
			}
		}

		while ((string = in.readLine()) != null) {
			int src = getNodeFromString(string);
			String e = src + Config.get("EDGE_DIRECTED_DELIMITER") + user;
			if (!edges.contains(e)) {
				edges.add(e);
			}
		}
		in.close();
		reader.close();
	}

	private int getNodeFromString(String string) throws IOException {
		String nodeID = string.split(";;;")[0];
		int index;
		if (mapping.containsKey(nodeID)) {
			index = mapping.get(nodeID);
		} else {
			mapping.put(nodeID, nodeLabelCounter);
			index = nodeLabelCounter;
			mappingWriter.writeln(nodeID + ";;;" + nodeLabelCounter);
			nodeLabelCounter++;

		}
		if (!seen.contains(index)) {
			graphWriter.writeln(index);
			seen.add(index);
		}
		return index;
	}

	public int getNodeLabelCounter() {
		return nodeLabelCounter;
	}
}
