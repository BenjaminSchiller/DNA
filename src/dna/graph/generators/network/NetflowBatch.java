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
import dna.graph.edges.network.NetworkEdge;
import dna.graph.edges.network.UpdateEvent;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.multi.DoubleMultiWeight;
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

/**
 * A batch-generator which creates batches based on netflow events read by a
 * NetflowEventReader from a netflow-list file. <br>
 * <br>
 * 
 * It extends the NetworkBatch class, therefore its purpose is the modeling of
 * network events as graph updates for a single batch given the updates. All
 * timestamp, queue-checking etc. is already handled by the NetworkBatch class.
 * Therefore, only the given events have to be processed accordingly.<br>
 * <br>
 * 
 * The graph modeling can be specified by the parameters handed over to the
 * NetflowBatch constructor. That is the edges and edgeDirections. <br>
 * <br>
 * 
 * The first dimension of the <b>edges</b> array represents paths induced by a
 * single flow. All fields contained in a single path represent nodes in the
 * graph. <br>
 * <br>
 * 
 * The <b>edgeDirections</b> refer to the same paths defined in the edges array.
 * They define the direction of the respective paths.<br>
 * <br>
 * 
 * Note that the edgeWeights and nodeWeights are left over from previous version
 * and are (for now) kept for backwards compatibility. Furthermore, they are
 * still handed over in all the methods but not actually used. This allows for
 * proper integration of new functionalities in the future. For now the weights
 * of nodes and edges have the following structure:<br>
 * - <b>Nodes</b>: (flows_in, flows_out, packets_in, packets_out, bytes_in,
 * bytes_out) <br>
 * - <b>Edges</b>: (flows, packets, bytes)<br>
 * <br>
 * 
 * @author Rwilmes
 * 
 */
public class NetflowBatch extends NetworkBatch {

	public enum NodeWeightMode {
		none, both, srcOnly, dstOnly
	};

	// definition of the graph model based on the edges, edgeDirections,
	// edgeWEights and nodeWeights
	protected NetflowEventField[][] edges;
	protected NetflowDirection[] edgeDirections;
	protected EdgeWeightValue[] edgeWeights; // no longer used
	protected NodeWeightValue[] nodeWeights; // no longer used

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

	/**
	 * The craftBatch method is the heart of a class extending NetworkBatch. It
	 * operates on the given Graph and the NetworkEvents, which is a list of all
	 * events to be processed and encapsulated in the batch to be crafted.<br>
	 * <br>
	 * 
	 * Note that edges with zero weight and nodes with zero degree are supposed
	 * to be deleted. (If not configured otherwise) Therefore, we keep track of
	 * all changes made to the edges and nodes throughout the event processing.
	 * This allows us to combine the given graph with the new graph updates and
	 * the revert updates in order to determine the resulting weight and degree
	 * of edges and nodes. This information is then being used in order to
	 * remove zero degree nodes and zero weight edges.
	 * 
	 * @param g
	 *            The given graph.
	 * @param timestamp
	 *            The timestamp of the batch to be generated. Is also being used
	 *            to compute the time of reverted graph updates.
	 * @param events
	 *            The events that happened and should be modelled and
	 *            ecnapsulated by the batch to be generated.
	 * @param decrementEvents
	 *            The events which happened before and have to be reverted in
	 *            this batch. This events can either be NetworkEdges,
	 *            representing the revertion of edges (and edge-weights) or
	 *            NodeUpdates, representing the revertion of noddes (and
	 *            node-weights)
	 */
	@Override
	public Batch craftBatch(Graph g, DateTime timestamp,
			ArrayList<NetworkEvent> events,
			ArrayList<UpdateEvent> decrementEvents,
			HashMap<String, Integer> edgeWeightChanges) {
		// init batch
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				TimeUnit.MILLISECONDS.toSeconds(timestamp.getMillis()), 0, 0,
				0, 0, 0, 0);

		// a map used to keep track of the node degrees.
		HashMap<Integer, Integer> nodesDegreeMap = new HashMap<Integer, Integer>();

		// a map used to keep track of newly added nodes
		HashMap<Integer, Node> addedNodes = new HashMap<Integer, Node>();

		// a map used to keep track of nodes weights
		HashMap<Integer, double[]> nodesWeightMap = new HashMap<Integer, double[]>();

		// a list used to keep track of newly added edges
		ArrayList<NetworkEdge> addedEdges = new ArrayList<NetworkEdge>();

		// a list containing the decrement edges (or edge revertions)
		ArrayList<NetworkEdge> decrementEdges = new ArrayList<NetworkEdge>();

		// a list containing the decrmeent nodes (or node revertions)
		ArrayList<NodeUpdate> decrementNodes = new ArrayList<NodeUpdate>();

		// fill lists based on the given update events
		for (UpdateEvent ue : decrementEvents) {
			if (ue instanceof NetworkEdge)
				decrementEdges.add((NetworkEdge) ue);
			if (ue instanceof NodeUpdate)
				decrementNodes.add((NodeUpdate) ue);
		}

		// iterate over each network event and process them based on the model
		for (NetworkEvent networkEvent : events) {
			NetflowEvent event = (NetflowEvent) networkEvent;

			// if direction is null (i.e. who-has lookups) or src == dst -->
			// skip this event
			if (event.getSrcAddress().equals(event.getDstAddress())
					|| event.getDirection() == null)
				continue;

			NetflowDirection direction = event.getDirection();

			// iterate over edges specified in the model
			for (int i = 0; i < this.edgeDirections.length; i++) {
				NetflowDirection edgeDir = this.edgeDirections[i];

				// only apply events to edges with fitting direction
				if (edgeDir.equals(direction)
						|| direction.equals(NetflowDirection.bidirectional)) {
					// actual processing method
					processEvents(event, this.edges[i], this.edgeWeights,
							edgeDir, this.nodeWeights, addedNodes,
							nodesWeightMap, addedEdges, b, g);
				}
			}
		}

		// add newly added nodes to nodes degree map
		for (Integer nodeId : addedNodes.keySet()) {
			nodesDegreeMap.put(nodeId, 0);
		}

		// iterate over edge revertions. For each edge present in the graph but
		// not in the revertions: add a dummy revert edge with weight-change 0.
		// This
		// allows for easier processing later
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

		// Iterate over all newly added edges and call edge to batch, handing
		// the new added edge as well as the respective revertion edge
		for (NetworkEdge ne : addedEdges) {
			// get revertion edge
			NetworkEdge decrementNe = new NetworkEdge(ne.getSrc(), ne.getDst(),
					0, new double[this.edgeWeights.length]);
			for (NetworkEdge dne : decrementEdges) {
				if (ne.getSrc() == dne.getSrc() && ne.getDst() == dne.getDst()) {
					decrementNe = dne;
					break;
				}
			}

			// actual addition method creating the graph updates and adding them
			// to the batch
			addEdgeToBatch(b, g, ne, decrementNe, addedNodes, nodesDegreeMap);
		}

		// compute node degrees and weights and delete zero degree nodes
		computeNodeDegreesAndWeights(addedNodes, nodesDegreeMap,
				nodesWeightMap, decrementNodes, g, b);

		return b;
	}

	/**
	 * This method is the last step of the batch generation process. It computes
	 * the nodes resulting degrees and weights. If a node degree turns out to be
	 * 0, the node will be removed. If its degree remains > 0 only its weight
	 * will be changed.
	 */
	protected void computeNodeDegreesAndWeights(
			HashMap<Integer, Node> addedNodes,
			HashMap<Integer, Integer> nodeDegreeMap,
			HashMap<Integer, double[]> nodesWeightMap,
			ArrayList<NodeUpdate> decrementNodeUpdates, Graph g, Batch b) {
		// gather a list of nodes considered for weight updates
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

		// iterate over all nodes in the graph and compute resulting degree
		// based on current degree and the degrees contained in the map
		// if the degree results in zero, remove the node
		Iterator<IElement> ite = g.getNodes().iterator();
		while (ite.hasNext()) {
			Node n = (Node) ite.next();
			int index = n.getIndex();

			// increment degree based on current degree
			incrementNodeDegree(nodeDegreeMap, index, n.getDegree());

			// get degree after batch application
			int degree = nodeDegreeMap.get(index);

			if (degree == 0) {
				// if zero and removeZeroDegreeNodes flag is set, remove node
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
				// if degree is > 0, add node to list of considered nodes
				if (!nodes.contains(index))
					nodes.add(index);
			}
		}

		// iterate over considered nodes and update weights
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

	/**
	 * This method is called for each NetflowEvent of a batch and creates the
	 * respective edges and nodes and updates their weights in case they have
	 * already been created.
	 **/
	protected void processEvents(NetflowEvent event,
			NetflowEventField[] eventFields, EdgeWeightValue[] edgeWeights,
			NetflowDirection edgeDir, NodeWeightValue[] nodeWeights,
			HashMap<Integer, Node> addedNodes,
			HashMap<Integer, double[]> nodeWeightMap,
			ArrayList<NetworkEdge> addedEdges, Batch b, Graph g) {
		if (eventFields == null || eventFields.length < 2)
			return;

		// iterate over eventFields, each represents a node in the resulting
		// graph
		for (int i = 0; i < eventFields.length - 1; i++) {
			String string0 = event.get(eventFields[i]);
			String string1 = event.get(eventFields[i + 1]);

			// if either of the fields is not present (== null) try to appply
			// defined substitution rules
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

			// get mapping of nodes
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

	/**
	 * This method creates actual graph updates based on the processed events
	 * and adds them to the batch.<br>
	 * <br>
	 * 
	 * The maps of added nodes and the node degrees will be updated accordingly,
	 * i.e. when an edge will be removed based on the weight decrement updates,
	 * the degree of the nodes the edge is attached to will be decremented
	 * accordingly. This is necessary to be able to remove zero degree nodes
	 * later.
	 * 
	 * @param b
	 *            Batch the updates will be added to.
	 * @param g
	 *            Current graph.
	 * @param ne
	 *            The network edge to be added or changed
	 * @param dne
	 *            The corresponding revertion edge.
	 * @param addedNodes
	 *            Map of added nodes.
	 * @param nodeDegreeMap
	 *            Map of node degrees.
	 */
	protected void addEdgeToBatch(Batch b, Graph g, NetworkEdge ne,
			NetworkEdge dne, HashMap<Integer, Node> addedNodes,
			HashMap<Integer, Integer> nodeDegreeMap) {
		// get src and dst nodes from graph or added nodes list
		Node srcNode = g.getNode(ne.getSrc());
		if (srcNode == null)
			srcNode = addedNodes.get(ne.getSrc());

		Node dstNode = g.getNode(ne.getDst());
		if (dstNode == null)
			dstNode = addedNodes.get(ne.getDst());

		// get edge
		IWeightedEdge e = (IWeightedEdge) g.getEdge(srcNode, dstNode);

		// check if weight changed
		boolean weightChange = !ne.isZero();
		boolean decrement = !dne.isZero();

		// if weightChange flag is true, that is when a netflow event actually
		// induced a positive weight changed on the given edge, we will add this
		// as a reverted edge update (aka decrementEdge) to the event queue for
		// later revertion.
		if (reader instanceof NetflowEventReader && weightChange)
			addEdgeWeightDecrementalToQueue((NetflowEventReader) reader, ne);

		// check if edge exists, if not create new edge instance
		if (e == null) {
			// init edge instance
			e = (IWeightedEdge) g.getGraphDatastructures().newEdgeInstance(
					srcNode, dstNode);

			// set weight as addition of positive weight change and potential
			// weight revertion
			e.setWeight(new DoubleMultiWeight(addition(ne.getEdgeWeights(),
					dne.getEdgeWeights())));

			// add edge to batch
			b.add(new EdgeAddition(e));

			// update node degree map accordingly
			incrementNodeDegree(nodeDegreeMap, ne.getSrc());
			incrementNodeDegree(nodeDegreeMap, ne.getDst());
		} else {
			// edge exists --> update weights and degrees

			// get old weight from present edge
			DoubleMultiWeight wOld = (DoubleMultiWeight) ((IWeightedEdge) e)
					.getWeight();

			// compute new weight by addition
			double[] weightsNew = addition(wOld.getWeights(),
					ne.getEdgeWeights());

			// if decrement update available: add it to the new weight (note
			// that decrement updates contain negative weights)
			if (decrement)
				weightsNew = addition(weightsNew, dne.getEdgeWeights());

			// init new weight object
			DoubleMultiWeight wNew = new DoubleMultiWeight(weightsNew);

			// check if first weight (containing the number of flows) is zero
			if (wNew.getWeight(0) == 0) {
				// if wieght is zero, remove edge from batch
				b.add(new EdgeRemoval(e));

				// decrement node degrees accordingly
				decrementNodeDegree(nodeDegreeMap, ne.getSrc());
				decrementNodeDegree(nodeDegreeMap, ne.getDst());
			} else {
				// weight > 0: add edge weight update to batch
				b.add(new EdgeWeight(e, wNew));
			}
		}
	}

	/** Decrements the nodes degree in the map. **/
	protected void decrementNodeDegree(HashMap<Integer, Integer> nodeDegreeMap,
			int nodeId) {
		incrementNodeDegree(nodeDegreeMap, nodeId, -1);
	}

	/** Increments the nodes degree in the map. **/
	protected void incrementNodeDegree(HashMap<Integer, Integer> nodeDegreeMap,
			int nodeId) {
		incrementNodeDegree(nodeDegreeMap, nodeId, 1);
	}

	/**
	 * Increments the degree of the given node in the map by the given count. If
	 * node is not present in the map it will be added with the given count as
	 * its degree.
	 **/
	protected void incrementNodeDegree(HashMap<Integer, Integer> nodeDegreeMap,
			int nodeId, int count) {
		if (nodeDegreeMap.containsKey(nodeId)) {
			nodeDegreeMap.put(nodeId, nodeDegreeMap.get(nodeId) + count);
		} else {
			nodeDegreeMap.put(nodeId, count);
		}
	}

	/**
	 * Decrements the degree of the given node in the map by the given count. If
	 * node is not present in the map it will be added with the given count as
	 * its degree.
	 **/
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

	/**
	 * Adds an edge decremental event to the queue at time t + edgeLifetime.
	 * Weights will be multiplied by (-1).
	 **/
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

	/**
	 * Adds a new edge to the list of network edges. If the edge is already
	 * present the weights will be cumulated.
	 */
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

	/** Adds a node to the given batch and node map. **/
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

	/** Adds a node to the given batch and node map. **/
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

	/** Convenience method for addition of double arrays of same length. **/
	public double[] addition(double[] d1, double[] d2) {
		double[] d3 = new double[Math.max(d1.length, d2.length)];
		for (int i = 0; i < d3.length; i++) {
			double do1 = (i < d1.length ? d1[i] : 0.0);
			double do2 = (i < d2.length ? d2[i] : 0.0);
			d3[i] = do1 + do2;
		}
		return d3;
	}

	// convenience method for double array printing
	public String printe(double[] input) {
		String buff = "";
		if (input.length > 0)
			buff += input[0];
		for (int i = 1; i < input.length; i++)
			buff += "\t" + input[i];
		return buff;
	}

	/** Maps the given node string to an int. **/
	protected int map(String key) {
		if (this.map.keySet().contains(key))
			return this.map.get(key);
		else {
			this.map.put(key, this.counter);
			this.counter++;
			return (this.counter - 1);
		}
	}

	protected int counter; // int counter for new mappings

	// map containing the actual mapping from string --> int
	protected HashMap<String, Integer> map;

	/** Get key for the given mapping. **/
	public String getKey(Integer mapping) {
		Set<String> keys = map.keySet();

		for (String key : keys) {
			if (map.get(key) == mapping)
				return key;
		}

		return "unknown";
	}
}
