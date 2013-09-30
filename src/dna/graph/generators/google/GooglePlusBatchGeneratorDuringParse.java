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
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.directed.DirectedBatchGenerator;

public class GooglePlusBatchGeneratorDuringParse extends DirectedBatchGenerator
		implements IDtoForDatabase {

	private BufferedReader in;
	Graph g;
	private HashMap<String, Integer> mapping;
	private int nodeLabelCounter;
	private HashMap<String, DirectedNode> newNodes;
	private HashMap<DirectedNode, Long> lastSeen;
	private HashSet<DirectedEdge> newEdges;
	private String foldername;
	private GraphNodeDeletionType deletionType;
	private GraphNodeAdditionType additionType;
	private HashMap<DirectedNode, Integer> count;

	public GooglePlusBatchGeneratorDuringParse(String name,
			GraphDataStructure datastructures, String foldername,
			GraphNodeAdditionType additionType,
			GraphNodeDeletionType deletionType, Dto dto) {
		super(name, datastructures);
		this.newNodes = new HashMap<String, DirectedNode>();
		this.mapping = dto.mapping;
		this.lastSeen = dto.lastSeen;
		this.newEdges = new HashSet<DirectedEdge>();
		this.foldername = foldername;
		this.deletionType = deletionType;
		this.additionType = additionType;
		this.nodeLabelCounter = dto.nodeLabelCounter;
		this.count = dto.count;
	}

	@Override
	public Batch<DirectedEdge> generate(Graph graph) {
		g = graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(this.ds,
				graph.getTimestamp(), graph.getTimestamp() + 1);

		final File folder = new File(this.foldername);
		batch = parseFolder(batch, folder);

		batch = deletion(batch);

		return batch;
	}

	private Batch<DirectedEdge> deletion(Batch<DirectedEdge> batch) {
		if (deletionType == GraphNodeDeletionType.AfterNTimes) {
			for (DirectedNode n : lastSeen.keySet()) {
				if (batch.getTo() - lastSeen.get(n) > 0) {
					batch.add(new NodeRemoval<DirectedEdge>(n));
					mapping.remove(n);
				}
			}
		} else if (deletionType == GraphNodeDeletionType.EmptyNodes) {
			for (DirectedNode n : lastSeen.keySet()) {
				if (n.getDegree() == 0) {
					batch.add(new NodeRemoval<DirectedEdge>(n));
					mapping.remove(n);
				}
			}
		} else if (deletionType == GraphNodeDeletionType.NoDeletions) {
			// nothing to do
		} else if (deletionType == GraphNodeDeletionType.NotSeenInBatch) {
			for (DirectedNode n : lastSeen.keySet()) {
				if (batch.getTo() > lastSeen.get(n)) {
					batch.add(new NodeRemoval<DirectedEdge>(n));
					mapping.remove(n);
				}
			}
		} else {
			System.err.println("wrong deletion type");
			return null;
		}

		return batch;
	}

	public Batch<DirectedEdge> parseFolder(Batch<DirectedEdge> b,
			final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				b = parseFolder(b, fileEntry);
			} else {
				b = parseFile(b, fileEntry);
			}
		}
		return b;
	}

	private Batch<DirectedEdge> parseFile(Batch<DirectedEdge> b, File file) {
		try {

			FileReader reader = new FileReader(file);
			in = new BufferedReader(reader);
			String string;
			String[] inputs;
			DirectedNode user;

			// /parse User
			in.readLine();
			string = in.readLine();
			inputs = string.split(";;;");
			// if (count.keySet().contains(inputs[0])) {
			// count.put(inputs[0], count.get(inputs[0] + 1));
			// } else {
			// count.put(inputs[0], 1);
			// }
			if (additionType == GraphNodeAdditionType.AfterNTimes
					&& count.get(inputs[0]) < 1) {
				return b;
			}

			if (mapping.containsKey(inputs[0])) {
				if (newNodes.containsKey(inputs[0])) {
					user = newNodes.get(inputs[0]);
				} else {
					user = (DirectedNode) g.getNode(mapping.get(inputs[0]));
				}
				lastSeen.put(user, b.getTo());
				b = nodeUpdate(b, user);
			} else {
				user = (DirectedNode) this.ds.newNodeInstance(nodeLabelCounter);
				mapping.put(inputs[0], nodeLabelCounter);
				nodeLabelCounter++;
				newNodes.put(inputs[0], user);
				lastSeen.put(user, b.getTo());
				b.add(new NodeAddition<DirectedEdge>(user));
				b = nodeAddition(b, user);
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
		return b;
	}

	private Batch<DirectedEdge> nodeUpdate(Batch<DirectedEdge> b,
			DirectedNode user) throws IOException {
		String string;
		String[] inputs;
		boolean parseInNodes = false;
		boolean parseOutNodes = false;
		while ((string = in.readLine()) != null) {
			if (!parseOutNodes && string.contains("Out list:")) {
				parseOutNodes = true;
				continue;
			}
			if (!parseInNodes && parseOutNodes && string.contains("In list:")) {
				parseInNodes = true;
				parseOutNodes = false;
				continue;
			}

			if (parseOutNodes && !parseInNodes) {
				inputs = string.split(";;;");
				DirectedNode dst;
				if (mapping.containsKey(inputs[0])) {
					if (newNodes.containsKey(inputs[0])) {
						dst = newNodes.get(inputs[0]);
					} else {
						dst = (DirectedNode) g.getNode(mapping.get(inputs[0]));
					}
				} else {
					dst = (DirectedNode) this.ds
							.newNodeInstance(nodeLabelCounter);
					mapping.put(inputs[0], nodeLabelCounter);
					nodeLabelCounter++;
					newNodes.put(inputs[0], dst);
					b.add(new NodeAddition<DirectedEdge>(dst));
				}
				lastSeen.put(dst, b.getTo());
				DirectedEdge e = (DirectedEdge) this.ds.newEdgeInstance(user,
						dst);
				if (!newEdges.contains(e)) {
					if (!g.containsEdge(e)) {
						b.add(new EdgeAddition<DirectedEdge>(e));
					}
					newEdges.add(e);
				}
				continue;
			}

			if (!parseOutNodes && parseInNodes) {
				inputs = string.split(";;;");
				DirectedNode src;
				if (mapping.containsKey(inputs[0])) {
					if (newNodes.containsKey(inputs[0])) {
						src = newNodes.get(inputs[0]);
					} else {
						src = (DirectedNode) g.getNode(mapping.get(inputs[0]));
					}
				} else {
					src = (DirectedNode) this.ds
							.newNodeInstance(nodeLabelCounter);
					mapping.put(inputs[0], nodeLabelCounter);
					nodeLabelCounter++;
					newNodes.put(inputs[0], src);
					b.add(new NodeAddition<DirectedEdge>(src));
				}
				lastSeen.put(src, b.getTo());
				DirectedEdge e = (DirectedEdge) this.ds.newEdgeInstance(src,
						user);
				if (!newEdges.contains(e)) {
					if (!g.containsEdge(e)) {
						b.add(new EdgeAddition<DirectedEdge>(e));
					}
					newEdges.add(e);
				}
				continue;
			}
		}
		for (IElement ie : user.getEdges()) {
			DirectedEdge edge = (DirectedEdge) ie;
			if (!newEdges.contains(edge)) {
				b.add(new EdgeRemoval<DirectedEdge>(edge));
			}
		}
		return b;
	}

	private Batch<DirectedEdge> nodeAddition(Batch<DirectedEdge> b,
			DirectedNode user) throws IOException {

		String string;
		String[] inputs;
		boolean parseInNodes = false;
		boolean parseOutNodes = false;
		while ((string = in.readLine()) != null) {
			if (!parseOutNodes && string.contains("Out list:")) {
				parseOutNodes = true;
				continue;
			}
			if (!parseInNodes && parseOutNodes && string.contains("In list:")) {
				parseInNodes = true;
				parseOutNodes = false;
				continue;
			}

			if (parseOutNodes && !parseInNodes) {
				inputs = string.split(";;;");
				DirectedNode dst;
				if (mapping.containsKey(inputs[0])) {
					if (newNodes.containsKey(inputs[0])) {
						dst = newNodes.get(inputs[0]);
					} else {
						dst = (DirectedNode) g.getNode(mapping.get(inputs[0]));
					}
				} else {
					dst = (DirectedNode) this.ds
							.newNodeInstance(nodeLabelCounter);
					mapping.put(inputs[0], nodeLabelCounter);
					nodeLabelCounter++;
					newNodes.put(inputs[0], dst);
					b.add(new NodeAddition<DirectedEdge>(dst));
				}
				lastSeen.put(dst, b.getTo());
				DirectedEdge e = (DirectedEdge) this.ds.newEdgeInstance(user,
						dst);
				if (!newEdges.contains(e)) {
					if (!g.containsEdge(e)) {
						b.add(new EdgeAddition<DirectedEdge>(e));
					}
					newEdges.add(e);
				}
				continue;
			}

			if (!parseOutNodes && parseInNodes) {
				inputs = string.split(";;;");
				DirectedNode src;
				if (mapping.containsKey(inputs[0])) {
					if (newNodes.containsKey(inputs[0])) {
						src = newNodes.get(inputs[0]);
					} else {
						src = (DirectedNode) g.getNode(mapping.get(inputs[0]));
					}
				} else {
					src = (DirectedNode) this.ds
							.newNodeInstance(nodeLabelCounter);
					mapping.put(inputs[0], nodeLabelCounter);
					nodeLabelCounter++;
					newNodes.put(inputs[0], src);
					b.add(new NodeAddition<DirectedEdge>(src));
				}
				lastSeen.put(src, b.getTo());
				DirectedEdge e = (DirectedEdge) this.ds.newEdgeInstance(src,
						user);
				if (!newEdges.contains(e)) {
					if (!g.containsEdge(e)) {
						b.add(new EdgeAddition<DirectedEdge>(e));
					}
					newEdges.add(e);
				}
				continue;
			}
		}
		return b;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public Dto getDto() {

		return new Dto(newNodes, newEdges, mapping, count, lastSeen,
				nodeLabelCounter, this.getName());
	}

}
