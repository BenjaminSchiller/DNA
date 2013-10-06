package dna.graph.generators.google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

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
	private int nodeLabelCounter;
	private HashMap<Integer, Long> lastSeen;
	private String foldername;
	private GraphNodeDeletionType deletionType;
	private GraphNodeAdditionType additionType;
	private HashMap<Integer, Integer> count;
	private GraphDataStructure ds;

	public GooglePlusBatchGeneratorDuringParse(String name, String foldername,
			GraphNodeAdditionType additionType,
			GraphNodeDeletionType deletionType, MappingDto dto) {
		this.mapping = dto.mapping;
		this.lastSeen = dto.lastSeen;
		this.nodeLabelCounter = dto.nodeLabelCounter;
		this.count = dto.count;
		this.foldername = foldername;
		this.deletionType = deletionType;
		this.additionType = additionType;
	}

	public Batch parseBatchFormFile(Graph graph) throws IOException {
		g = graph;
		this.ds = g.getGraphDatastructures();
		b = new Batch(this.ds, graph.getTimestamp(), graph.getTimestamp() + 1);
		final File folder = new File(this.foldername);
		parseFolder(folder);
		deletion();
		return b;
	}

	private void deletion() {
		if (deletionType == GraphNodeDeletionType.AfterNTimes) {
			for (String s : mapping.keySet()) {
				if (b.getTo() - lastSeen.get(mapping.get(s)) > 0) {
					b.add(new NodeRemoval(g.getNode(mapping.get(s))));
				}
			}
		} else if (deletionType == GraphNodeDeletionType.EmptyNodes) {
			for (IElement iE : g.getNodes()) {
				DirectedNode n = (DirectedNode) iE;
				if (n.getDegree() == 0) {
					b.add(new NodeRemoval(n));
				}
			}
		} else if (deletionType == GraphNodeDeletionType.NoDeletions) {
			// nothing to do
		} else if (deletionType == GraphNodeDeletionType.NotSeenInBatch) {
			for (String s : mapping.keySet()) {
				if (b.getTo() > lastSeen.get(mapping.get(s))) {
					b.add(new NodeRemoval(g.getNode(mapping.get(s))));
				}
			}
		} else {
			System.err.println("wrong deletion type");
		}
	}

	private void parseFolder(final File folder) throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				parseFolder(fileEntry);
			} else {
				parseFile(fileEntry);
			}
		}
	}

	private void parseFile(File file) throws IOException {
		HashSet<DirectedEdge> seenEdges = new HashSet<DirectedEdge>();
		FileReader reader = new FileReader(file);
		in = new BufferedReader(reader);
		String string;

		// /parse User
		in.readLine();
		string = in.readLine();
		DirectedNode user = this.getNodeFromString(string);

		while (!(string = in.readLine()).equals(OUT)) {
		}

		while (!(string = in.readLine()).equals(IN)) {
			DirectedNode dst = getNodeFromString(string);
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
	}

	private DirectedNode getNodeFromString(String string) {
		DirectedNode node;
		String nodeID = string.split(";;;")[0];
		if (this.mapping.containsKey(nodeID)) {
			node = (DirectedNode) this.g.getNode(this.mapping.get(nodeID));
		} else {
			node = (DirectedNode) this.ds
					.newNodeInstance(this.nodeLabelCounter);
			this.mapping.put(nodeID, node.getIndex());
			this.lastSeen.put(node.getIndex(), this.b.getTo());
			this.nodeLabelCounter++;
			count.put(node.getIndex(), 1);
			this.g.addNode(node);
			b.add(new NodeAddition(node));
		}
		return node;
	}

	public MappingDto getDto() {
		return new MappingDto(this.mapping, this.count, this.lastSeen,
				this.nodeLabelCounter, this.g.getName());
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
