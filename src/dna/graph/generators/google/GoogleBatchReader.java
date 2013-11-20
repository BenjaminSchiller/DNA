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
import dna.updates.update.Update.UpdateType;
import dna.util.Config;

public class GoogleBatchReader {
	private HashMap<Integer, Long> lastSeen;
	private HashMap<Integer, Integer> count;
	private long deleteAfter;
	private int insertAfter;
	private GraphNodeDeletionType deltype[];
	private GraphNodeAdditionType addtype;
	private HashMap<Integer, HashSet<Integer>> nodesOutList;
	private HashMap<Integer, HashSet<Integer>> nodesInList;
	private HashSet<String> edgeAdd;
	private HashSet<String> nodeAdd;
	private HashSet<String> edgeDel;
	private HashSet<String> nodeDel;

	private HashMap<Integer, HashSet<Integer>> checkNodesIn;
	private HashMap<Integer, HashSet<Integer>> checkNodesOut;

	private long timeStamp;
	private String name;

	public void parseBatchFormFile(
			HashMap<Integer, HashSet<Integer>> nodesOutList,
			HashMap<Integer, HashSet<Integer>> nodesInList, long timeStamp,
			MappingDto dto, String indir, String outdir, String filename)
			throws IOException {
		this.name = dto.name;
		this.lastSeen = dto.lastSeen;
		this.count = dto.count;
		this.deltype = dto.del;
		this.addtype = dto.add;
		this.deleteAfter = dto.deleteAfter;
		this.insertAfter = dto.insertAfter;
		this.nodesOutList = nodesOutList;
		this.nodesInList = nodesInList;
		this.checkNodesOut = new HashMap<Integer, HashSet<Integer>>(10000);
		this.checkNodesIn = new HashMap<Integer, HashSet<Integer>>(10000);
		this.timeStamp = timeStamp;
		this.edgeAdd = new HashSet<String>();
		this.edgeDel = new HashSet<String>();
		this.nodeAdd = new HashSet<String>();
		this.nodeDel = new HashSet<String>();

		if (addtype.equals(GraphNodeAdditionType.AfterNTimes)) {
			parseInsertAfterN(indir, filename);
		} else {
			parseFile(indir, filename);
		}
		deletions();
		writeBatchToFile(outdir, "Batch-" + filename);
		writeMappingToFile(outdir, "Mapping-" + filename);
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
			if (!nodesOutList.containsKey(n)) {
				lastSeen.put(n, timeStamp + 1);
				if (!count.containsKey(n)) {
					count.put(n, 1);
					continue;
				}
				count.put(n, count.get(n) + 1);
				if (count.get(n) == insertAfter) {
					checkNodesOut.put(n, new HashSet<Integer>(200));
					checkNodesIn.put(n, new HashSet<Integer>(200));
					nodesOutList.put(n, new HashSet<Integer>(200));
					nodesInList.put(n, new HashSet<Integer>(200));
					nodeAdd.add(UpdateType.NODE_ADDITION
							+ Config.get("UPDATE_DELIMITER1") + n);
				}
			} else {
				lastSeen.put(n, timeStamp + 1);
				count.put(n, count.get(n) + 1);
				checkNodesOut.put(n, nodesOutList.get(n));
				checkNodesIn.put(n, nodesInList.get(n));
				nodesOutList.put(n, new HashSet<Integer>(200));
				nodesInList.put(n, new HashSet<Integer>(200));
			}
		}
		String[] inputs;
		while ((line = reader.readString()) != null) {
			inputs = line.split(Config.get("EDGE_DIRECTED_DELIMITER"));
			int srcIndex = Integer.parseInt(inputs[0]);
			int dstIndex = Integer.parseInt(inputs[1]);
			if (!nodesInList.containsKey(srcIndex)
					|| !nodesInList.containsKey(dstIndex)) {
				continue;
			}

			if (checkNodesOut.containsKey(srcIndex)) {
				if (!checkNodesOut.get(srcIndex).contains(dstIndex)) {
					edgeAdd.add(UpdateType.EDGE_ADDITION
							+ Config.get("UPDATE_DELIMITER1") + line);
					nodesOutList.get(srcIndex).add(dstIndex);
					nodesInList.get(dstIndex).add(srcIndex);
				} else {
					checkNodesOut.get(srcIndex).remove(dstIndex);
					nodesOutList.get(srcIndex).add(dstIndex);
					if (checkNodesIn.containsKey(dstIndex)
							&& checkNodesIn.get(dstIndex).contains(srcIndex)) {
						checkNodesIn.get(dstIndex).remove(srcIndex);
						nodesInList.get(dstIndex).add(srcIndex);
					}
				}
				continue;
			}

			if (checkNodesIn.containsKey(dstIndex)) {
				if (!checkNodesIn.get(dstIndex).contains(srcIndex)) {
					edgeAdd.add(UpdateType.EDGE_ADDITION
							+ Config.get("UPDATE_DELIMITER1") + line);
					nodesOutList.get(srcIndex).add(dstIndex);
					nodesInList.get(dstIndex).add(srcIndex);
				} else {
					checkNodesIn.get(dstIndex).remove(srcIndex);
					nodesInList.get(dstIndex).add(srcIndex);
				}
			}
		}
		if (!checkNodesOut.isEmpty()) {
			for (int i : checkNodesOut.keySet()) {
				for (int s : checkNodesOut.get(i)) {
					edgeDel.add(UpdateType.EDGE_REMOVAL
							+ Config.get("UPDATE_DELIMITER1") + i
							+ Config.get("EDGE_DIRECTED_DELIMITER") + s);
					if (checkNodesIn.containsKey(s)) {
						checkNodesIn.get(s).remove(i);
					} else {
						nodesInList.get(s).remove(i);
					}
				}
			}
		}

		if (!checkNodesIn.isEmpty()) {
			for (int i : checkNodesIn.keySet()) {
				for (int s : checkNodesIn.get(i)) {
					edgeDel.add(UpdateType.EDGE_REMOVAL
							+ Config.get("UPDATE_DELIMITER1") + s
							+ Config.get("EDGE_DIRECTED_DELIMITER") + i);
					nodesOutList.get(s).remove(i);
				}
			}
		}
		reader.close();
	}

	private void parseFile(String indir, String dirName) throws IOException {
		Reader reader = new Reader(indir, dirName);
		String line;

		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_NODES_LIST")))) {
		}

		while (!(line = reader.readString()).equals(Writer
				.getKeywordAsLine(Config.get("GRAPH_KEYWORD_EDGES_LIST")))) {
			int n = Integer.parseInt(line);
			if (!nodesOutList.containsKey(n)) {
				lastSeen.put(n, timeStamp + 1);
				count.put(n, 1);
				checkNodesOut.put(n, new HashSet<Integer>(200));
				checkNodesIn.put(n, new HashSet<Integer>(200));
				nodesOutList.put(n, new HashSet<Integer>(200));
				nodesInList.put(n, new HashSet<Integer>(200));
				nodeAdd.add(UpdateType.NODE_ADDITION
						+ Config.get("UPDATE_DELIMITER1") + n);
			} else {
				lastSeen.put(n, timeStamp + 1);
				count.put(n, count.get(n) + 1);
				checkNodesOut.put(n, nodesOutList.get(n));
				checkNodesIn.put(n, nodesInList.get(n));
				nodesOutList.put(n, new HashSet<Integer>(200));
				nodesInList.put(n, new HashSet<Integer>(200));
			}
		}

		String[] inputs;
		while ((line = reader.readString()) != null) {
			inputs = line.split(Config.get("EDGE_DIRECTED_DELIMITER"));
			int srcIndex = Integer.parseInt(inputs[0]);
			int dstIndex = Integer.parseInt(inputs[1]);
			if (!nodesInList.containsKey(srcIndex)
					|| !nodesInList.containsKey(dstIndex)) {
				continue;
			}
			if (checkNodesOut.containsKey(srcIndex)) {
				if (!checkNodesOut.get(srcIndex).contains(dstIndex)) {
					edgeAdd.add(UpdateType.EDGE_ADDITION
							+ Config.get("UPDATE_DELIMITER1") + line);
					nodesOutList.get(srcIndex).add(dstIndex);
					nodesInList.get(dstIndex).add(srcIndex);
				} else {
					checkNodesOut.get(srcIndex).remove(dstIndex);
					nodesOutList.get(srcIndex).add(dstIndex);
					if (checkNodesIn.containsKey(dstIndex)
							&& checkNodesIn.get(dstIndex).contains(srcIndex)) {
						checkNodesIn.get(dstIndex).remove(srcIndex);
					}
					nodesInList.get(dstIndex).add(srcIndex);

				}
				continue;
			}

			if (checkNodesIn.containsKey(dstIndex)) {
				if (!checkNodesIn.get(dstIndex).contains(srcIndex)) {
					edgeAdd.add(UpdateType.EDGE_ADDITION
							+ Config.get("UPDATE_DELIMITER1") + line);
					nodesOutList.get(srcIndex).add(dstIndex);
					nodesInList.get(dstIndex).add(srcIndex);
				} else {
					checkNodesIn.get(dstIndex).remove(srcIndex);
					nodesInList.get(dstIndex).add(srcIndex);
				}
			}
		}

		if (!checkNodesOut.isEmpty()) {
			for (int i : checkNodesOut.keySet()) {
				for (int s : checkNodesOut.get(i)) {
					edgeDel.add(UpdateType.EDGE_REMOVAL
							+ Config.get("UPDATE_DELIMITER1") + i
							+ Config.get("EDGE_DIRECTED_DELIMITER") + s);
					if (checkNodesIn.containsKey(s)) {
						checkNodesIn.get(s).remove(i);
					} else {
						nodesInList.get(s).remove(i);
					}
				}
			}
		}

		if (!checkNodesIn.isEmpty()) {
			for (int i : checkNodesIn.keySet()) {
				for (int s : checkNodesIn.get(i)) {
					edgeDel.add(UpdateType.EDGE_REMOVAL
							+ Config.get("UPDATE_DELIMITER1") + s
							+ Config.get("EDGE_DIRECTED_DELIMITER") + i);
					nodesOutList.get(s).remove(i);
				}
			}
		}
		reader.close();
	}

	private void deletions() {
		HashSet<Integer> removals = new HashSet<Integer>();
		if (deltype.length == 1
				&& deltype[0].equals(GraphNodeDeletionType.NoDeletions)) {
			return;
		}
		for (int n : nodesOutList.keySet()) {
			for (int i = 0; i < deltype.length; i++) {
				if (deltype[i].equals(GraphNodeDeletionType.AfterNTimes)) {
					if (timeStamp + 1L - lastSeen.get(n) > deleteAfter) {
						nodeDel.add(UpdateType.NODE_REMOVAL
								+ Config.get("UPDATE_DELIMITER1") + n);
						removals.add(n);
						continue;
					}
				}
				if (deltype[i].equals(GraphNodeDeletionType.EmptyNodes)) {
					if (nodesOutList.get(n).isEmpty()
							&& nodesInList.get(n).isEmpty()) {
						String s = UpdateType.NODE_ADDITION
								+ Config.get("UPDATE_DELIMITER1") + n;
						if (nodeAdd.contains(s)) {
							nodeAdd.remove(s);
						} else {
							nodeDel.add(UpdateType.NODE_REMOVAL
									+ Config.get("UPDATE_DELIMITER1") + n);
						}
						removals.add(n);
						continue;
					}
				}
			}
		}
		for (Integer i : removals) {
			lastSeen.remove(i);
			count.remove(i);
			for (int j : nodesOutList.get(i)) {
				nodesInList.get(j).remove(i);
			}
			for (int j : nodesInList.get(i)) {
				nodesOutList.get(j).remove(i);
			}
			nodesOutList.remove(i);
			nodesInList.remove(i);
		}
	}

	public boolean writeMappingToFile(String dir, String filename) {
		return MappingWriter.write(this.getDto(), dir, filename);
	}

	public boolean writeBatchToFile(String dir, String filename) {
		Writer writer = null;
		try {
			writer = new Writer(dir, filename);
			writer.writeKeyword(Config.get("BATCH_KEYWORD_FROM"));
			writer.writeln(timeStamp);

			writer.writeKeyword(Config.get("BATCH_KEYWORD_TO"));
			writer.writeln(timeStamp + 1);

			writer.writeKeyword(Config.get("BATCH_KEYWORD_UPDATES"));

			for (String s : nodeAdd) {
				writer.writeln(s);
			}
			for (String s : nodeDel) {
				writer.writeln(s);
			}
			for (String s : edgeAdd) {
				writer.writeln(s);
			}
			for (String s : edgeDel) {
				writer.writeln(s);
			}

			System.out.println("Batch from " + timeStamp + " to "
					+ (timeStamp + 1) + " (" + nodeAdd.size() + ","
					+ nodeDel.size() + "/" + edgeAdd.size() + ","
					+ edgeDel.size() + ")");

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean writeGraph(String dir, String filename, String graphName) {
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
			writer.writeln(nodesOutList.size() * 300);

			writer.writeKeyword(Config.get("GRAPH_KEYWORD_TIMESTAMP"));
			writer.writeln(timeStamp + 1);

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
			System.out.println("Graph (" + nodesOutList.size() + ")");
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

	public MappingDto getDto() {
		return new MappingDto(name, count, lastSeen, addtype, deltype,
				insertAfter, deleteAfter);
	}

	public HashMap<Integer, HashSet<Integer>> getNodesOutList() {
		return nodesOutList;
	}

	public HashMap<Integer, HashSet<Integer>> getNodesInList() {
		return nodesInList;
	}
}
