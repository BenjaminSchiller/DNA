package dna.graph.generators.network;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.network.UpdateEvent;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.network.NetworkEdgeWeight;
import dna.graph.weights.network.NetworkNodeWeight;
import dna.graph.weights.network.NetworkWeight.ElementType;
import dna.io.network.NetworkEvent;
import dna.io.network.netflow.NetflowEvent;
import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.io.network.netflow.NetflowEventReader;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;

public class NetflowBatch extends NetworkBatch {

	public enum NodeWeightMode {
		none, both, srcOnly, dstOnly
	};

	protected NetflowEventField[][] edges;
	protected NetflowDirection[] edgeDirections;
	protected EdgeWeightValue[] edgeWeights;
	protected NodeWeightValue[] nodeWeights;

	protected boolean substituteMissingPortsAsProtocols = false;

	public NetflowBatch(String name, NetflowEventReader reader,
			NetflowEventField[][] edges, NetflowDirection[] edgeDirections,
			EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights)
			throws FileNotFoundException {
		this(name, reader, edges, edgeDirections, edgeWeights, nodeWeights,
				true);
	}

	public NetflowBatch(String name, NetflowEventReader reader,
			NetflowEventField[][] edges, NetflowDirection[] edgeDirections,
			EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights,
			boolean substituteMissingPortsAsProtocols)
			throws FileNotFoundException {
		super(name, reader, reader.getBatchIntervalSeconds());
		this.edges = edges;
		this.edgeDirections = edgeDirections;
		this.edgeWeights = edgeWeights;
		this.nodeWeights = nodeWeights;
		this.substituteMissingPortsAsProtocols = substituteMissingPortsAsProtocols;
		this.map = new HashMap<String, Integer>();
		this.counter = 0;
	}

	@Override
	public Batch craftBatch(Graph g, DateTime timestamp,
			ArrayList<NetworkEvent> events,
			ArrayList<UpdateEvent> decrementEvents,
			HashMap<String, Integer> edgeWeightChanges) {
		// init batch
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				TimeUnit.MILLISECONDS.toSeconds(timestamp.getMillis()), 0, 0,
				0, 0, 0, 0);

		HashMap<Integer, Integer> nodesDegreeMap = new HashMap<Integer, Integer>();

		HashMap<Integer, Node> addedNodes = new HashMap<Integer, Node>();

		HashMap<Integer, double[]> nodesWeightMap = new HashMap<Integer, double[]>();

		ArrayList<NetworkEdge> addedEdges = new ArrayList<NetworkEdge>();

		ArrayList<NetworkEdge> decrementEdges = new ArrayList<NetworkEdge>();
		ArrayList<NodeUpdate> decrementNodes = new ArrayList<NodeUpdate>();
		for (UpdateEvent ue : decrementEvents) {
			if (ue instanceof NetworkEdge)
				decrementEdges.add((NetworkEdge) ue);
			if (ue instanceof NodeUpdate)
				decrementNodes.add((NodeUpdate) ue);
		}

		for (NetworkEvent networkEvent : events) {
			NetflowEvent event = (NetflowEvent) networkEvent;

			if (event.getSrcAddress().equals(event.getDstAddress())
					|| event.getDirection() == null)
				continue;

			NetflowDirection direction = event.getDirection();

			for (int i = 0; i < this.edgeDirections.length; i++) {
				NetflowDirection edgeDir = this.edgeDirections[i];

				if (edgeDir.equals(direction)
						|| direction.equals(NetflowDirection.bidirectional)) {
					processEvents(event, this.edges[i], this.edgeWeights,
							edgeDir, this.nodeWeights, addedNodes,
							nodesWeightMap, addedEdges, b, g);
				}
			}
		}

		for (Integer nodeId : addedNodes.keySet()) {
			nodesDegreeMap.put(nodeId, 0);
		}

		for (NetworkEdge dne : decrementEdges) {
			boolean present = false;
			for (NetworkEdge ne : addedEdges) {
				if (ne.getSrc() == dne.getSrc() && ne.getDst() == dne.getDst()) {
					present = true;
					break;
				}
			}

			if (!present)
				addedEdges.add(new NetworkEdge(dne.getSrc(), dne.getDst(), 0,
						new double[this.edgeWeights.length]));
		}

		for (NetworkEdge ne : addedEdges) {
			NetworkEdge decrementNe = new NetworkEdge(ne.getSrc(), ne.getDst(),
					0, new double[this.edgeWeights.length]);
			for (NetworkEdge dne : decrementEdges) {
				if (ne.getSrc() == dne.getSrc() && ne.getDst() == dne.getDst()) {
					decrementNe = dne;
					break;
				}
			}

			addEdgeToBatch(b, g, ne, decrementNe, addedNodes, nodesDegreeMap);
		}

		// compute node degrees and weights and delete zero degree nodes
		computeNodeDegreesAndWeights(addedNodes, nodesDegreeMap,
				nodesWeightMap, decrementNodes, g, b);

		return b;
	}

	protected double[] getZeroArray(int length) {
		double[] array = new double[length];
		for (int i = 0; i < array.length; i++)
			array[i] = 0.0;
		return array;
	}

	protected void computeNodeDegreesAndWeights(
			HashMap<Integer, Node> addedNodes,
			HashMap<Integer, Integer> nodeDegreeMap,
			HashMap<Integer, double[]> nodesWeightMap,
			ArrayList<NodeUpdate> decrementNodeUpdates, Graph g, Batch b) {
		// lit of all nodes to be updated
		ArrayList<Integer> nodes = new ArrayList<Integer>();

		for (Integer index : addedNodes.keySet()) {
			if (!nodes.contains(index))
				nodes.add(index);
		}

		for (Integer index : nodesWeightMap.keySet()) {
			if (!nodes.contains(index))
				nodes.add(index);
		}

		for (NodeUpdate nu : decrementNodeUpdates) {
			if (!nodes.contains(nu.getIndex()))
				nodes.add(nu.getIndex());
		}

		Iterator<IElement> ite = g.getNodes().iterator();
		while (ite.hasNext()) {
			Node n = (Node) ite.next();
			int index = n.getIndex();
			incrementNodeDegree(nodeDegreeMap, index, n.getDegree());

			int degree = nodeDegreeMap.get(index);

			if (degree == 0) {
				if (reader instanceof NetflowEventReader
						&& ((NetflowEventReader) reader)
								.isRemoveZeroDegreeNodes()) {
					// remove node from graph
					b.add(new NodeRemoval(n));

					// remove node index from considered nodes
					if (nodes.contains(index))
						nodes.remove(new Integer(index));
				}
			} else {
				if (!nodes.contains(index))
					nodes.add(index);
			}
		}

		for (Integer index : nodes) {
			boolean added = addedNodes.containsKey(index);
			boolean weightChanged = nodesWeightMap.containsKey(index);
			double[] weightChanges = nodesWeightMap.get(index);
			boolean updated = false;
			double[] decrementWeights = new double[0];
			for (NodeUpdate nu : decrementNodeUpdates) {
				if (nu.getIndex() == index) {
					updated = true;
					decrementWeights = nu.getUpdates();
					break;
				}
			}

			// get node from graph
			Node n = g.getNode(index);

			// if not in graph it must be newly added
			if (n == null)
				n = addedNodes.get(index);

			IWeightedNode wn = (IWeightedNode) n;

			// current nodes weight (all = 0 if just initialized)
			NetworkNodeWeight oldW = (NetworkNodeWeight) wn.getWeight();

			// add decremental to queue
			if (weightChanged)
				addNodeWeightDecrementalToQueue((NetflowEventReader) reader,
						new NodeUpdate(index, b.getTo() * 1000, weightChanges));

			// if weight changed on newly added node
			if (weightChanged && !updated && added) {
				wn.setWeight(new NetworkNodeWeight(oldW.getType(), addition(
						oldW.getWeights(), weightChanges)));
			}
			// if weight changed on existing node
			if (weightChanged && !added) {
				double[] newWeights = addition(oldW.getWeights(), weightChanges);
				if (updated)
					newWeights = addition(newWeights, decrementWeights);
				b.add(new NodeWeight(wn, new NetworkNodeWeight(oldW.getType(),
						newWeights)));
			}
			// if no changes but decrment update
			if (!weightChanged && updated && !added) {
				b.add(new NodeWeight(wn, new NetworkNodeWeight(oldW.getType(),
						addition(oldW.getWeights(), decrementWeights))));
			}
		}
	}

	public String printe(double[] input) {
		String buff = "";
		if (input.length > 0)
			buff += input[0];
		for (int i = 1; i < input.length; i++)
			buff += "\t" + input[i];
		return buff;
	}

	/**
	 * This method is called for each NetflowEvent of a batch and creates the
	 * respective edges and nodes and updates their weights in case they already
	 * got created.
	 **/
	protected void processEvents(NetflowEvent event,
			NetflowEventField[] eventFields, EdgeWeightValue[] edgeWeights,
			NetflowDirection edgeDir, NodeWeightValue[] nodeWeights,
			HashMap<Integer, Node> addedNodes,
			HashMap<Integer, double[]> nodeWeightMap,
			ArrayList<NetworkEdge> addedEdges, Batch b, Graph g) {
		if (eventFields == null || eventFields.length < 2)
			return;

		for (int i = 0; i < eventFields.length - 1; i++) {
			String string0 = event.get(eventFields[i]);
			String string1 = event.get(eventFields[i + 1]);

			if (string0 == null || string0.equals("null")) {
				if (substituteMissingPortsAsProtocols
						&& (eventFields[i].equals(NetflowEventField.DstPort) || eventFields[i]
								.equals(NetflowEventField.SrcPort))) {
					string0 = event.get(NetflowEventField.Protocol);
				}
			}
			if (string1 == null || string1.equals("null")) {
				if (substituteMissingPortsAsProtocols
						&& (eventFields[i + 1]
								.equals(NetflowEventField.DstPort) || eventFields[i + 1]
								.equals(NetflowEventField.SrcPort))) {
					string1 = event.get(NetflowEventField.Protocol);
				}
			}

			int mapping0 = map(string0);
			int mapping1 = map(string1);

			// mappign is identical continue --> no self-edges
			if (mapping0 == mapping1)
				continue;

			// get node weights
			double[] srcNw = new double[nodeWeights.length];
			double[] dstNw = new double[nodeWeights.length];

			for (int j = 0; j < nodeWeights.length; j++) {
				srcNw[j] = event.getSrcNodeWeight2(nodeWeights[j], edgeDir);
				dstNw[j] = event.getDstNodeWeight2(nodeWeights[j], edgeDir);
			}

			// add node i and i+1
			addNode(addedNodes, nodeWeightMap, b, g, mapping0, eventFields[i],
					srcNw);
			addNode(addedNodes, nodeWeightMap, b, g, mapping1,
					eventFields[i + 1], dstNw);

			// get edge weights
			double[] ew = new double[edgeWeights.length];
			for (int j = 0; j < ew.length; j++)
				ew[j] = event.getEdgeWeight(edgeWeights[j], edgeDir);

			// add edge node i --> node i+1
			addEdge(addedEdges, mapping0, mapping1, b.getTo() * 1000, ew);
		}
	}

	protected void addEdgeToBatch(Batch b, Graph g, NetworkEdge ne,
			NetworkEdge dne, HashMap<Integer, Node> addedNodes,
			HashMap<Integer, Integer> nodeDegreeMap) {
		Node srcNode = g.getNode(ne.getSrc());
		if (srcNode == null)
			srcNode = addedNodes.get(ne.getSrc());

		Node dstNode = g.getNode(ne.getDst());
		if (dstNode == null)
			dstNode = addedNodes.get(ne.getDst());

		IWeightedEdge e = (IWeightedEdge) g.getEdge(srcNode, dstNode);

		// check if weight changed
		boolean weightChange = !ne.isZero();
		boolean decrement = !dne.isZero();

		if (reader instanceof NetflowEventReader && weightChange)
			addEdgeWeightDecrementalToQueue((NetflowEventReader) reader, ne);

		// check if edge exists, if not create new edge instance
		if (e == null) {
			e = (IWeightedEdge) g.getGraphDatastructures().newEdgeInstance(
					srcNode, dstNode);

			e.setWeight(new NetworkEdgeWeight(addition(ne.getEdgeWeights(),
					dne.getEdgeWeights())));
			b.add(new EdgeAddition(e));
			incrementNodeDegree(nodeDegreeMap, ne.getSrc());
			incrementNodeDegree(nodeDegreeMap, ne.getDst());
		} else {
			// edge exists --> update weights and degrees
			NetworkEdgeWeight wOld = (NetworkEdgeWeight) ((IWeightedEdge) e)
					.getWeight();
			double[] weightsNew = addition(wOld.getWeights(),
					ne.getEdgeWeights());
			if (decrement)
				weightsNew = addition(weightsNew, dne.getEdgeWeights());

			NetworkEdgeWeight wNew = new NetworkEdgeWeight(weightsNew);

			if (wNew.getWeight(0) == 0) {
				b.add(new EdgeRemoval(e));
				decrementNodeDegree(nodeDegreeMap, ne.getSrc());
				decrementNodeDegree(nodeDegreeMap, ne.getDst());
			} else {
				b.add(new EdgeWeight(e, wNew));
			}
		}
	}

	protected void decrementNodeDegree(HashMap<Integer, Integer> nodeDegreeMap,
			int nodeId) {
		incrementNodeDegree(nodeDegreeMap, nodeId, -1);
	}

	protected void incrementNodeDegree(HashMap<Integer, Integer> nodeDegreeMap,
			int nodeId) {
		incrementNodeDegree(nodeDegreeMap, nodeId, 1);
	}

	protected void incrementNodeDegree(HashMap<Integer, Integer> nodeDegreeMap,
			int nodeId, int count) {
		if (nodeDegreeMap.containsKey(nodeId)) {
			nodeDegreeMap.put(nodeId, nodeDegreeMap.get(nodeId) + count);
		} else {
			nodeDegreeMap.put(nodeId, count);
		}
	}

	protected void addNodeWeightDecrementalToQueue(NetflowEventReader r,
			NodeUpdate update) {
		double[] nodeWeightsArray = new double[update.getUpdates().length];
		for (int i = 0; i < nodeWeightsArray.length; i++) {
			nodeWeightsArray[i] = (-1) * update.getUpdates()[i];
		}

		r.addUpdateEventToQueue(new NodeUpdate(update.getIndex(), update
				.getTime() + r.getEdgeLifeTimeSeconds() * 1000,
				nodeWeightsArray));
	}

	protected void addEdgeWeightDecrementalToQueue(NetflowEventReader r,
			NetworkEdge e) {
		double[] edgeWeightsArray = new double[e.getEdgeWeights().length];
		for (int i = 0; i < edgeWeightsArray.length; i++) {
			edgeWeightsArray[i] = (-1) * e.getEdgeWeights()[i];
		}

		r.addUpdateEventToQueue(new NetworkEdge(e.getSrc(), e.getDst(), (e
				.getTime() + r.getEdgeLifeTimeSeconds() * 1000),
				edgeWeightsArray));
	}

	protected void addEdge(ArrayList<NetworkEdge> addedEdges, int src, int dst,
			long time, double[] edgeWeights) {
		boolean alreadyAdded = false;
		for (NetworkEdge ne : addedEdges) {
			if (ne.getSrc() == src && ne.getDst() == dst) {
				alreadyAdded = true;

				// compute new edge weights
				ne.setEdgeWeights(addition(edgeWeights, ne.getEdgeWeights()));
			}
		}

		if (!alreadyAdded) {
			addedEdges.add(new NetworkEdge(src, dst, time, edgeWeights));
		}
	}

	protected Node addNode(HashMap<Integer, Node> addedNodes,
			HashMap<Integer, double[]> nodeWeightsMap, Batch b, Graph g,
			int nodeToAdd, NetflowEventField type, double[] nodeWeights) {
		ElementType eType = ElementType.UNKNOWN;

		switch (type) {
		case DstAddress:
			eType = ElementType.HOST;
			break;
		case SrcAddress:
			eType = ElementType.HOST;
			break;
		case DstPort:
			eType = ElementType.PORT;
			break;
		case SrcPort:
			eType = ElementType.PORT;
			break;
		case Protocol:
			eType = ElementType.PROT;
			break;
		}

		// update weights
		if (nodeWeightsMap.containsKey(nodeToAdd)) {
			double[] weights = nodeWeightsMap.get(nodeToAdd);
			nodeWeightsMap.put(nodeToAdd, addition(weights, nodeWeights));
		} else {
			nodeWeightsMap.put(nodeToAdd, nodeWeights);
		}

		// add node
		return addNode(addedNodes, nodeWeightsMap, b, g, nodeToAdd, eType,
				nodeWeights);
	}

	protected Node addNode(HashMap<Integer, Node> addedNodes,
			HashMap<Integer, double[]> nodeWeightsMap, Batch b, Graph g,
			int nodeToAdd, ElementType type, double[] nodeWeights) {
		if (addedNodes.containsKey(nodeToAdd)) {
			return addedNodes.get(nodeToAdd);
		} else {
			Node n = g.getNode(nodeToAdd);
			if (n != null) {
				return n;
			} else {
				// init node
				n = g.getGraphDatastructures().newNodeInstance(nodeToAdd);

				// set type-weight with 0-weights as init
				NetworkNodeWeight initWeight = new NetworkNodeWeight(
						type.toString(), new double[nodeWeights.length]);
				((IWeightedNode) n).setWeight(initWeight);
				addedNodes.put(nodeToAdd, n);
				b.add(new NodeAddition(n));
				return n;
			}
		}
	}

	public double[] addition(double[] d1, double[] d2) {
		double[] d3 = new double[Math.max(d1.length, d2.length)];
		for (int i = 0; i < d3.length; i++) {
			double do1 = (i < d1.length ? d1[i] : 0.0);
			double do2 = (i < d2.length ? d2[i] : 0.0);
			d3[i] = do1 + do2;
		}
		return d3;
	}

	protected int map(String key) {
		if (this.map.keySet().contains(key))
			return this.map.get(key);
		else {
			this.map.put(key, this.counter);
			this.counter++;
			return (this.counter - 1);
		}
	}

	protected int counter;

	protected HashMap<String, Integer> map;

	public String getKey(Integer mapping) {
		Set<String> keys = map.keySet();

		for (String key : keys) {
			if (map.get(key) == mapping)
				return key;
		}

		return "unknown";
	}
}
