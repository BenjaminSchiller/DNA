package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.io.BatchWriter;
import dna.io.GraphWriter;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class GooglePlusBatchGeneratorDuringParse implements IDtoForDatabase {

	private static String IN = "# In list:";
	private static String OUT = "# Out list:";

	private BufferedReader in;
	private Graph g;
	private Batch b;
	private HashMap<String, Integer> mapping;
	private HashMap<Integer, Long> lastSeen;
	private HashMap<Integer, Integer> count;
	private int nodeLabelCounter;
	private int deleteAfter;
	private int insertAfter;
	private String foldername;
	private GraphNodeDeletionType deletionType[];
	private GraphNodeAdditionType additionType;
	private GraphDataStructure ds;
	private HashMap<Integer, NodeAddition> nodeAdds;

	public Batch parseBatchFormFile(Graph graph, MappingDto dto, String indir,
			String outdir, String dirName) throws IOException {

		this.mapping = dto.mapping;
		this.lastSeen = dto.lastSeen;
		this.nodeLabelCounter = dto.nodeLabelCounter;
		this.count = dto.count;
		this.foldername = indir + dirName;
		this.deletionType = dto.del;
		this.additionType = dto.add;
		this.deleteAfter = dto.deleteAfter;
		this.insertAfter = dto.insertAfter;

		nodeAdds = new HashMap<Integer, NodeAddition>();

		g = graph;
		this.ds = g.getGraphDatastructures();
		b = new Batch(this.ds, graph.getTimestamp(), graph.getTimestamp() + 1);
		g.setTimestamp(g.getTimestamp() + 1);
		final File folder = new File(this.foldername);
		if (additionType == GraphNodeAdditionType.AfterNTimesOnlyCrawled
				|| additionType == GraphNodeAdditionType.EverySeenNodeOnlyCrawled) {
			parseForMapping(folder);
			parseFolderOnlyCraweled(folder);
		} else {
			parseFolder(folder);
		}
		deletion();

		writeBatchToFile(outdir, "Batch-" + dirName);
		writeMappingToFile(outdir, "Mapping-" + dirName);

		return b;
	}

	private void deletion() {
		HashSet<String> removals = new HashSet<String>();
		for (int i = 0; i < deletionType.length; i++) {
			if (deletionType[i] == GraphNodeDeletionType.AfterNTimes) {
				for (String s : mapping.keySet()) {
					if (!existingNode(s)) {
						continue;
					}
					if (b.getTo() - lastSeen.get(mapping.get(s)) > deleteAfter) {
						if (!removals.contains(s)) {
							removals.add(s);
						}
					}
				}
			} else if (deletionType[i] == GraphNodeDeletionType.EmptyNodes) {
				for (Iterator it = mapping.keySet().iterator(); it.hasNext();) {
					String s = (String) it.next();

					if (!existingNode(s)) {
						continue;
					}

					DirectedNode n = (DirectedNode) g.getNode(this.mapping
							.get(s));
					if (n.getDegree() == 0) {
						if (!removals.contains(s)) {
							if (!nodeAdds.containsKey(this.mapping.get(s))) {
								removals.add(s);
							} else {
								nodeAdds.remove(this.mapping.get(s));
								g.removeNode(g.getNode(this.mapping.get(s)));
							}
							if (!(additionType == GraphNodeAdditionType.AfterNTimes || additionType == GraphNodeAdditionType.AfterNTimesOnlyCrawled)) {
								this.count.remove(this.mapping.get(s));
								this.lastSeen.remove(this.mapping.get(s));
								it.remove();
								System.out.println(mapping.get(s));
							}
						}
					}
				}
			} else if (deletionType[i] == GraphNodeDeletionType.NoDeletions) {
				// nothing to do
			} else if (deletionType[i] == GraphNodeDeletionType.NotSeenInBatch) {
				for (String s : mapping.keySet()) {
					if (!existingNode(s)) {
						continue;
					}
					if (b.getTo() > lastSeen.get(mapping.get(s))) {
						if (!removals.contains(s)) {
							removals.add(s);
						}
					}
				}
			} else {
				System.err.println("wrong deletion type");
			}

		}
		for (String s : removals) {
			int nodeID = this.mapping.get(s);
			if (!((additionType == GraphNodeAdditionType.AfterNTimes || additionType == GraphNodeAdditionType.AfterNTimesOnlyCrawled) && count
					.get(nodeID) < insertAfter)) {
				b.add(new NodeRemoval(g.getNode(mapping.get(s))));
				g.removeNode(g.getNode(nodeID));
				this.mapping.remove(s);
				this.count.remove(nodeID);
				this.lastSeen.remove(nodeID);
			}
		}
		b.addAll(nodeAdds.values());
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

	private void getNodeFromFileName(String name) {
		DirectedNode node;
		String nodeID = name.split("-")[1];
		if (nodeID.equals("106015471845065739185")) {
			System.out.println();
		}
		if (mapping.containsKey(nodeID)) {
			int nodeIndex = mapping.get(nodeID);
			count.put(nodeIndex, count.get(nodeIndex) + 1);
			lastSeen.put(nodeIndex, b.getTo());
			if (additionType == GraphNodeAdditionType.AfterNTimesOnlyCrawled
					&& insertAfter == count.get(nodeIndex)) {
				node = (DirectedNode) ds.newNodeInstance(nodeIndex);
				g.addNode(node);
				nodeAdds.put(node.getIndex(), new NodeAddition(node));
			}
		} else {
			node = (DirectedNode) this.ds.newNodeInstance(nodeLabelCounter);
			mapping.put(nodeID, node.getIndex());
			lastSeen.put(node.getIndex(), b.getTo());
			count.put(node.getIndex(), 1);
			nodeLabelCounter++;
			if (!(additionType == GraphNodeAdditionType.AfterNTimesOnlyCrawled && insertAfter > 1)) {
				g.addNode(node);
				nodeAdds.put(node.getIndex(), new NodeAddition(node));
			}
		}
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
		HashSet<DirectedEdge> seenEdges = new HashSet<DirectedEdge>();
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
		if (existingNode(nodeID)) {
			user = (DirectedNode) g.getNode(mapping.get(nodeID));
		} else {
			return;
		}

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			DirectedNode dst;
			String dstID = string.split(";;;")[0];
			if (existingNode(dstID)) {
				dst = (DirectedNode) g.getNode(mapping.get(dstID));
			} else {
				continue;
			}
			if (dst == null || user == null)
				System.out.println();
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(user,
					dst);
			seenEdges.add(edge);
			if (g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
				b.add(new EdgeAddition(edge));
			}
		}

		while ((string = in.readLine()) != null) {
			DirectedNode src;
			String srcID = string.split(";;;")[0];
			if (existingNode(srcID)) {
				src = (DirectedNode) g.getNode(mapping.get(srcID));
			} else {
				continue;
			}
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(src,
					user);
			seenEdges.add(edge);
			if (!g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
				b.add(new EdgeAddition(edge));
			}
		}

		for (IElement ie : user.getEdges()) {
			DirectedEdge edge = (DirectedEdge) ie;
			if (!seenEdges.contains(edge)) {
				b.add(new EdgeRemoval(edge));
			}
		}
		in.close();
		reader.close();

	}

	private boolean existingNode(String srcID) {
		return mapping.containsKey(srcID)
				&& !(additionType == GraphNodeAdditionType.AfterNTimesOnlyCrawled && count
						.get(mapping.get(srcID)).intValue() < insertAfter);
	}

	private void parseFolder(final File folder) throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("Parse Folder " + fileEntry.getName());
				parseFolder(fileEntry);
			} else {
				parseFile(fileEntry);
				System.out.println("Parse File " + fileEntry.getName());
			}
		}
	}

	private void parseFile(File file) throws IOException {
		HashSet<DirectedEdge> seenEdges = new HashSet<DirectedEdge>();
		FileReader reader = new FileReader(file);
		in = new BufferedReader(reader);
		String string;

		// /parse User
		string = in.readLine();
		if (!string.equals("# User:"))
			return;
		string = in.readLine();

		DirectedNode user = this.getNodeFromString(string);

		if (count.get(user.getIndex()) < insertAfter
				&& additionType == GraphNodeAdditionType.AfterNTimes) {
			return;
		}

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			DirectedNode dst = getNodeFromString(string);
			if (count.get(dst.getIndex()) < insertAfter
					&& additionType == GraphNodeAdditionType.AfterNTimes) {
				continue;
			}
			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(user,
					dst);
			seenEdges.add(edge);
			if (g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
				b.add(new EdgeAddition(edge));
			}
		}

		while ((string = in.readLine()) != null) {
			DirectedNode src = getNodeFromString(string);

			if (count.get(user.getIndex()) < insertAfter
					&& additionType == GraphNodeAdditionType.AfterNTimes) {
				continue;
			}

			DirectedEdge edge = (DirectedEdge) this.ds.newEdgeInstance(src,
					user);
			seenEdges.add(edge);
			if (!g.containsEdge(edge)) {
				g.addEdge(edge);
				edge.connectToNodes();
				b.add(new EdgeAddition(edge));
			}
		}

		for (IElement ie : user.getEdges()) {
			DirectedEdge edge = (DirectedEdge) ie;
			if (!seenEdges.contains(edge)) {
				b.add(new EdgeRemoval(edge));
			}
		}
		in.close();
		reader.close();

	}

	private DirectedNode getNodeFromString(String string) {
		DirectedNode node;
		String nodeID = string.split(";;;")[0];
		if (this.mapping.containsKey(nodeID)) {
			int nodeIndex = this.mapping.get(nodeID);
			if (lastSeen.get(nodeIndex).equals(this.b.getTo())) {
				count.put(nodeIndex, count.get(nodeIndex) + 1);
			}
			this.lastSeen.put(nodeIndex, this.b.getTo());
			if (additionType == GraphNodeAdditionType.AfterNTimes
					&& insertAfter == count.get(nodeIndex)) {
				node = (DirectedNode) this.ds.newNodeInstance(nodeIndex);
				g.addNode(node);
				nodeAdds.put(node.getIndex(), new NodeAddition(node));
			} else if ((additionType == GraphNodeAdditionType.AfterNTimes && insertAfter < count
					.get(nodeIndex))
					|| additionType != GraphNodeAdditionType.AfterNTimes) {
				node = (DirectedNode) this.g.getNode(nodeIndex);
			} else {
				node = (DirectedNode) this.ds.newNodeInstance(nodeIndex);
			}

		} else {
			node = (DirectedNode) this.ds
					.newNodeInstance(this.nodeLabelCounter);
			this.mapping.put(nodeID, node.getIndex());
			this.lastSeen.put(node.getIndex(), this.b.getTo());
			this.nodeLabelCounter++;
			count.put(node.getIndex(), 1);
			if (!(additionType == GraphNodeAdditionType.AfterNTimes && insertAfter > 1)) {
				g.addNode(node);
				nodeAdds.put(node.getIndex(), new NodeAddition(node));
			}
		}
		return node;
	}

	public MappingDto getDto() {
		return new MappingDto(this.g.getName(), this.mapping, this.count,
				this.lastSeen, this.nodeLabelCounter, additionType,
				deletionType, insertAfter, deleteAfter);
	}

	public boolean writeGraphToFile(String dir, String filename) {
		return GraphWriter.write(this.g, dir, filename);
	}

	public boolean writeBatchToFile(String dir, String filename) {
		return BatchWriter.write(this.b, dir, filename);
	}

	public Graph getGraph() {
		return this.g;
	}

	public void writeMappingToFile(String dir, String filename) {
		MappingWriter.write(this.getDto(), dir, filename);
	}
}
