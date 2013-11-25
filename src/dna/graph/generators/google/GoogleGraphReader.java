package dna.graph.generators.google;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;

public class GoogleGraphReader {

	private HashMap<Integer, Long> lastSeen;
	private HashMap<Integer, Integer> count;
	private int deleteAfter;
	private int insertAfter;
	private GraphNodeAdditionType addtype;
	private GraphNodeDeletionType[] deltype;
	private HashMap<Integer, HashSet<Integer>> nodesOutList;
	private HashMap<Integer, HashSet<Integer>> nodesInList;
	private int edgeCount;
	private String name;

	public boolean parseGraph(String name, String indir,
			GraphNodeAdditionType addtype, GraphNodeDeletionType[] deltype,
			int insertAfter, int deleteAfter, String outputDir, String dirName)
			throws IOException {

		this.name = name;
		this.lastSeen = new HashMap<Integer, Long>(65000);
		this.count = new HashMap<Integer, Integer>(65000);
		this.addtype = addtype;
		this.deltype = deltype;
		this.deleteAfter = deleteAfter;
		this.insertAfter = insertAfter;
		this.nodesOutList = new HashMap<Integer, HashSet<Integer>>(100000);
		this.nodesInList = new HashMap<Integer, HashSet<Integer>>(100000);
		this.edgeCount = 0;

		if (addtype.equals(GraphNodeAdditionType.AfterNTimes)) {
			parseInsertAfterN(indir, dirName);
		} else {
			parse(indir, dirName);
		}
		deletions();
		writeGraphToFile(outputDir, "Graph-" + dirName, name);
		writeMappingToFile(outputDir, "Mapping-" + dirName);
		return true;
	}

	private void parseInsertAfterN(String indir, String dirName)
			throws IOException {
		Reader reader = new Reader(indir, dirName);
		String line;

		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_NODES_LIST")))) {
		}

		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_EDGES_LIST")))) {
			int n = Integer.parseInt(line);
			lastSeen.put(n, 0L);
			count.put(n, 1);
		}
		reader.close();
	}

	private void deletions() {
		HashSet<Integer> removals = new HashSet<Integer>();
		if ((deltype.length == 1 && !deltype[0]
				.equals(GraphNodeDeletionType.EmptyNodes))
				|| deltype.length > 1) {
			return;
		}
		for (int n : nodesInList.keySet()) {
			for (int i = 0; i < deltype.length; i++) {
				if (deltype[i].equals(GraphNodeDeletionType.EmptyNodes)) {
					if (nodesOutList.get(n).isEmpty()
							&& nodesInList.get(n).isEmpty()) {
						removals.add(n);
						continue;
					}
				}
			}
		}
		for (Integer i : removals) {
			lastSeen.remove(i);
			count.remove(i);
			nodesOutList.remove(i);
			nodesInList.remove(i);
		}

	}

	private void parse(String indir, String dirName) throws IOException {
		Reader reader = new Reader(indir, dirName);
		String line;

		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_NODES_LIST")))) {
		}

		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_EDGES_LIST")))) {
			int n = Integer.parseInt(line);
			lastSeen.put(n, 0L);
			count.put(n, 1);
			nodesOutList.put(n, new HashSet<Integer>(100));
			nodesInList.put(n, new HashSet<Integer>(100));
		}

		String[] inputs;
		while ((line = reader.readString()) != null) {
			inputs = line.split(Config.get("EDGE_DIRECTED_DELIMITER"));
			int srcIndex = Integer.parseInt(inputs[0]);
			int dstIndex = Integer.parseInt(inputs[1]);
			nodesOutList.get(srcIndex).add(dstIndex);
			nodesInList.get(dstIndex).add(srcIndex);
			edgeCount++;
		}

		reader.close();
	}

	public MappingDto getDto() {
		return new MappingDto(name, count, lastSeen, addtype, deltype,
				insertAfter, deleteAfter);
	}

	public boolean writeMappingToFile(String dir, String filename) {
		return MappingWriter.write(this.getDto(), dir, filename);
	}

	public boolean writeGraphToFile(String dir, String filename,
			String graphName) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_NAME"));
			writer.writeln(graphName);
			GraphDataStructure ds = new GraphDataStructure(DHashMap.class,
					DHashSet.class, DHashSet.class, DirectedNode.class,
					DirectedEdge.class);
			writer.writeKeyword(Config.get("GRAPH_KEYWORD_DATASTRUCTURES"));
			writer.writeln(ds.getDataStructures());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_NODE_COUNT"));
			writer.writeln(nodesOutList.size());

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_EDGE_COUNT"));
			writer.writeln(edgeCount);

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_TIMESTAMP"));
			writer.writeln(0);

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_NODES_LIST"));
			for (int n : nodesOutList.keySet()) {
				writer.writeln(n);
			}

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_EDGES_LIST"));
			for (int n : nodesOutList.keySet()) {
				for (int e : nodesOutList.get(n)) {
					writer.writeln(n + Config.get("EDGE_DIRECTED_DELIMITER")
							+ e);
				}
			}
			System.out.println("Graph (" + nodesOutList.size() + ","
					+ edgeCount + ")");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<Integer, HashSet<Integer>> getNodesOutList() {
		return nodesOutList;
	}

	public HashMap<Integer, HashSet<Integer>> getNodesInList() {
		return nodesInList;
	}
}
