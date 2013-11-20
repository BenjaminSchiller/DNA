package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.io.Writer;
import dna.util.Config;

public class CopyOfOnlyCrawledParser {

	private static String IN = "# In list:";
	private static String OUT = "# Out list:";

	private String foldername;
	private int nodeLabelCounter;
	private HashMap<String, Integer> mapping;
	private BufferedReader in;
	private Writer graphWriter;
	private Writer mappingWriter;

	public boolean parse(String name, String indir, String outputDir,
			String dirName, int nodeLabelCounter, long timeStamp,
			HashMap<String, Integer> mapping) throws IOException {

		graphWriter = new Writer(outputDir, "Graph-" + dirName);
		// mappingWriter = new Writer(outputDir, "Mapping-" + dirName);

		this.foldername = indir + dirName;
		this.mapping = mapping;
		this.nodeLabelCounter = nodeLabelCounter;
		// mappingWriter.writeln("UserId;;;InternalMapping");
		final File folder = new File(foldername);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_NAME"));
		graphWriter.writeln(name);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_DATASTRUCTURES"));

		GraphDataStructure ds = new GraphDataStructure(DHashMap.class,
				DHashSet.class, DHashSet.class, DirectedNode.class,
				DirectedEdge.class);
		graphWriter.writeln(ds.getDataStructures());

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_NODE_COUNT"));
		graphWriter.writeln(0);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_EDGE_COUNT"));
		graphWriter.writeln(0);

		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_TIMESTAMP"));
		graphWriter.writeln(timeStamp);
		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_NODES_LIST"));
		parseForMapping(folder);
		graphWriter.writeKeyword(Config.get("GRAPH_KEYWORD_EDGES_LIST"));
		parseFolderOnlyCraweled(folder);
		graphWriter.close();
		// mappingWriter.writeln("NODELABELCOUNTER");
		// mappingWriter.writeln(this.nodeLabelCounter);
		// mappingWriter.close();
		return true;
	}

	private void parseForMapping(File folder) throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("Parse Folder " + fileEntry.getName());
				parseForMapping(fileEntry);
			} else {
				getNodeFromFileName(fileEntry.getName());
			}
		}
	}

	private void getNodeFromFileName(String name) throws IOException {
		String nodeID = name.split("-")[1];
		// if (!mapping.containsKey(nodeID)) {
		// mapping.put(nodeID, nodeLabelCounter);
		// // mappingWriter.writeln(nodeID + ";;;" + nodeLabelCounter);
		// nodeLabelCounter++;
		// }
		graphWriter.writeln(mapping.get(nodeID));
	}

	private void parseFolderOnlyCraweled(File folder) throws IOException {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("Parse Folder " + fileEntry.getName());
				parseFolderOnlyCraweled(fileEntry);
			} else {
				System.out.println("Parse File " + fileEntry.getName());
				parseFileOnlyCrawled(fileEntry);
			}
		}
	}

	private void parseFileOnlyCrawled(File file) throws IOException {
		FileReader reader = new FileReader(file);
		in = new BufferedReader(reader);
		String string;

		// /parse User
		string = in.readLine();
		if (string == null || !string.equals("# User:")) {
			in.close();
			reader.close();
			return;
		}
		string = in.readLine();
		int user;
		String nodeID = string.split(";;;")[0];
		user = mapping.get(nodeID);

		while ((string = in.readLine()) != null) {
			if (string.equals(OUT)) {
				break;
			}
		}
		if (string == null) {
			in.close();
			reader.close();
			return;
		}
		while ((string = in.readLine()) != null) {
			if (string.equals(IN)) {
				break;
			}
			int dst;
			String dstID = string.split(";;;")[0];
			if (mapping.containsKey(dstID)) {
				dst = mapping.get(dstID);
			} else {
				continue;
			}
			String e = user + Config.get("EDGE_DIRECTED_DELIMITER") + dst;
			graphWriter.writeln(e);

		}
		if (string == null) {
			in.close();
			reader.close();
			return;
		}
		while ((string = in.readLine()) != null) {
			int src;
			String srcID = string.split(";;;")[0];
			if (mapping.containsKey(srcID)) {
				src = mapping.get(srcID);
			} else {
				continue;
			}
			String e = src + Config.get("EDGE_DIRECTED_DELIMITER") + user;
			graphWriter.writeln(e);

		}
		in.close();
		reader.close();
	}

	public int getNodeLabelCounter() {
		return nodeLabelCounter;
	}
}
