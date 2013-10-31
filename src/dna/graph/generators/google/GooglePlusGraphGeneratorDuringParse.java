package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.io.GraphWriter;

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
	private int deleteAfter;
	private int insertAfter;
	private GraphNodeAdditionType addtype;
	private GraphNodeDeletionType[] deltype;
	private GraphDataStructure ds;

	public boolean writeMappingToFile(String dir, String filename) {
		return MappingWriter.write(this.getDto(), dir, filename);
	}

	public Graph parseGraph(String name, String indir,
			GraphNodeAdditionType addtype, GraphNodeDeletionType[] deltype,
			int insertAfter, int deleteAfter, String outputDir, String dirName)
			throws IOException {

		this.ds = new GraphDataStructure(DHashMap.class, DHashSet.class,
				DHashSet.class, DirectedNode.class, DirectedEdge.class);
		this.foldername = indir + dirName;
		this.mapping = new HashMap<String, Integer>();
		this.lastSeen = new HashMap<Integer, Long>();
		this.count = new HashMap<Integer, Integer>();
		this.nodeLabelCounter = 0;
		this.addtype = addtype;
		this.deltype = deltype;
		this.g = ds.newGraphInstance(name, 0, 0, 0);
		this.deleteAfter = deleteAfter;
		this.insertAfter = insertAfter;

		final File folder = new File(foldername);
		if (addtype == GraphNodeAdditionType.AfterNTimesOnlyCrawled
				|| addtype == GraphNodeAdditionType.EverySeenNodeOnlyCrawled) {
			parseForMapping(folder);
			parseFolderOnlyCraweled(folder);
		} else {
			parseFolder(folder);
		}

		for (int i = 0; i < deltype.length; i++) {
			if (deltype[i] == GraphNodeDeletionType.EmptyNodes) {
				deletion();
			}
		}

		writeGraphToFile(outputDir, "Graph-" + dirName);
		writeMappingToFile(outputDir, "Mapping-" + dirName);
		return g;
	}

	private void deletion() {

		if ((addtype == GraphNodeAdditionType.AfterNTimes || addtype == GraphNodeAdditionType.AfterNTimesOnlyCrawled)
				&& insertAfter > 1) {
			return;
		}

		HashSet<String> remove = new HashSet<String>();
		for (String s : mapping.keySet()) {
			DirectedNode n = (DirectedNode) g.getNode(mapping.get(s));
			if (n.getDegree() == 0) {
				remove.add(s);
			}
		}
		for (String s : remove) {
			DirectedNode n = (DirectedNode) g.getNode(mapping.get(s));
			g.removeNode(n);
			this.lastSeen.remove(n.getIndex());
			this.count.remove(n.getIndex());
			this.mapping.remove(s);
		}

	}

	private void parseForMapping(File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				parseForMapping(fileEntry);
			} else {
				getNodeFromFileName(fileEntry.getName());
			}
		}
	}

	private void parseFolderOnlyCraweled(File folder) throws IOException {
		if (addtype == GraphNodeAdditionType.AfterNTimesOnlyCrawled
				&& insertAfter > 1) {
			return;
		}
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.err.println("Parse Dir " + fileEntry.getName());
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
		if (!string.equals("# User:"))
			return;
		string = in.readLine();
		DirectedNode user;
		String nodeID = string.split(";;;")[0];
		if (mapping.containsKey(nodeID)) {
			user = (DirectedNode) g.getNode(mapping.get(nodeID));
		} else {
			System.out.println(nodeID);
			throw new IOException();
		}

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			DirectedNode dst;
			String dstID = string.split(";;;")[0];
			if (mapping.containsKey(dstID)) {
				dst = (DirectedNode) g.getNode(mapping.get(dstID));
			} else {
				continue;
			}
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(user,
					dst);
			if (g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}

		while ((string = in.readLine()) != null) {
			DirectedNode src;
			String srcID = string.split(";;;")[0];
			if (mapping.containsKey(srcID)) {
				src = (DirectedNode) g.getNode(mapping.get(srcID));
			} else {
				continue;
			}
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(src,
					user);
			if (!g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}
		in.close();
		reader.close();
	}

	private void getNodeFromFileName(String name) {
		DirectedNode node;
		String nodeID = name.split("-")[1];
		if (!mapping.containsKey(nodeID)) {
			node = (DirectedNode) this.ds.newNodeInstance(nodeLabelCounter);
			mapping.put(nodeID, node.getIndex());
			lastSeen.put(node.getIndex(), g.getTimestamp());
			count.put(node.getIndex(), 1);
			nodeLabelCounter++;
			if (!(addtype == GraphNodeAdditionType.AfterNTimesOnlyCrawled && insertAfter > 1))
				g.addNode(node);
		}

	}

	private void parseFolder(final File folder) throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("Parse dir " + fileEntry.getName());
				parseFolder(fileEntry);
			} else {
				System.out.println("Parse file " + fileEntry.getName());
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
		DirectedNode user = getNodeFromString(string);

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			DirectedNode dst = getNodeFromString(string);

			if (addtype == GraphNodeAdditionType.AfterNTimes && insertAfter > 1) {
				continue;
			}

			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(user,
					dst);
			if (g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}

		while ((string = in.readLine()) != null) {
			DirectedNode src = getNodeFromString(string);

			if (addtype == GraphNodeAdditionType.AfterNTimes && insertAfter > 1) {
				continue;
			}

			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(src,
					user);
			if (!g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}
		in.close();
		reader.close();

	}

	private DirectedNode getNodeFromString(String string) {
		DirectedNode node;
		String nodeID = string.split(";;;")[0];

		if (mapping.containsKey(nodeID)) {
			if (!(addtype == GraphNodeAdditionType.AfterNTimes && insertAfter > 1)) {

				node = (DirectedNode) g.getNode(mapping.get(nodeID));
			} else {
				node = (DirectedNode) this.ds.newNodeInstance(mapping
						.get(nodeID));
			}

		} else {
			node = (DirectedNode) this.ds.newNodeInstance(nodeLabelCounter);
			mapping.put(nodeID, node.getIndex());
			lastSeen.put(node.getIndex(), g.getTimestamp());
			count.put(node.getIndex(), 1);
			nodeLabelCounter++;
			if (!(addtype == GraphNodeAdditionType.AfterNTimes && insertAfter > 1))
				g.addNode(node);
		}
		return node;
	}

	public MappingDto getDto() {
		return new MappingDto(g.getName(), mapping, count, lastSeen,
				nodeLabelCounter, addtype, deltype, insertAfter, deleteAfter);
	}

	public boolean writeGraphToFile(String dir, String filename) {
		return GraphWriter.write(g, dir, filename);
	}
}
