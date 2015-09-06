package dna.updates.generators.konect;

import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.generators.konect.KonectEdge;
import dna.graph.generators.konect.KonectReader;
import dna.graph.generators.konect.KonectReader.KonectBatchType;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.intW.IntWeight;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.Log;
import dna.util.parameters.BooleanParameter;
import dna.util.parameters.StringParameter;

public class KonectBatch extends BatchGenerator {

	protected KonectReader r;

	protected KonectBatchType batchType;
	protected String batchParameter;

	public static final String separator = ";";
	protected int[] timestamps;
	protected int timestampsIndex;

	public KonectBatch(KonectReader r, KonectBatchType batchType,
			String batchParameter) {
		super("KonectBatch", new StringParameter("Name", r.name),
				new StringParameter("EdgeType", r.edgeType.toString()),
				new BooleanParameter("RemoveZeroDegreeNodes",
						r.removeZeroDegreeNodes));
		this.r = r;
		this.batchType = batchType;
		this.batchParameter = batchParameter;

		if (batchType.equals(KonectBatchType.TIMESTAMPS)) {
			String[] temp = batchParameter.split(separator);
			timestamps = new int[temp.length];
			for (int i = 0; i < temp.length; i++) {
				timestamps[i] = Integer.parseInt(temp[i]);
			}
			timestampsIndex = 0;
		}
	}

	@Override
	public Batch generate(IGraph g) {

		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1);

		if (r.removeZeroDegreeNodes) {
			for (IElement n_ : g.getNodes()) {
				Node n = (Node) n_;
				if (n.getDegree() == 0) {
					b.add(new NodeRemoval(n));
				}
			}
		}

		int processed = 0;
		while (r.peek() != null) {
			if (KonectBatchType.TIMESTAMP.equals(this.batchType)) {
				if (r.peek() != null
						&& r.peek().timestamp > g.getTimestamp()
								+ Integer.parseInt(this.batchParameter)) {
					b.setTo(b.getFrom() + Integer.parseInt(this.batchParameter));
					break;
				}
			} else if (KonectBatchType.TIMESTAMPS.equals(this.batchType)) {
				if (r.peek() != null
						&& r.peek().timestamp > timestamps[timestampsIndex]) {
					b.setTo(timestamps[timestampsIndex]);
					timestampsIndex++;
					break;
				}
			} else if (KonectBatchType.PROCESSED_EDGES.equals(this.batchType)) {
				if (processed >= Integer.parseInt(this.batchParameter))
					break;
			} else if (KonectBatchType.BATCH_SIZE.equals(this.batchType)) {
				if (b.getSize() >= Integer.parseInt(this.batchParameter))
					break;
			} else if (KonectBatchType.EDGE_GROWTH.equals(this.batchType)) {
				if (b.getEdgeAdditionsCount() - b.getEdgeRemovalsCount() >= Integer
						.parseInt(this.batchParameter))
					break;
			} else if (KonectBatchType.NODE_GROWTH.equals(this.batchType)) {
				if (b.getNodeAdditionsCount() - b.getNodeRemovalsCount() >= Integer
						.parseInt(this.batchParameter))
					break;
			}

			KonectEdge e = r.readEdge();
			if (e != null) {
				this.process(g, b, e);
			} else {
				return b;
			}
			processed++;
		}

		return b;
	}

	/**
	 * 
	 * processed the given edge (depending on the edge type specified) and adds
	 * update to the batch accordingly.
	 * 
	 * @param b
	 * @param edge
	 */
	protected void process(IGraph graph, Batch b, KonectEdge edge) {

		if (edge.n1 == edge.n2) {
			return;
		}

		Node n1 = r.getNode(edge.n1);
		Node n2 = r.getNode(edge.n2);

		switch (r.edgeType) {
		case ADD:
			if (edge.weight == 1) {
				this.processNode(graph, b, n1);
				this.processNode(graph, b, n2);
				Edge e = r.gds.newEdgeInstance(n1, n2);
				EdgeAddition ea = b.getEdgeAddition(e);
				if (ea == null) {
					b.add(new EdgeAddition(e));
				}
			} else {
				Log.error("invalid weight for ADD: " + edge);
			}
			break;
		case ADD_REMOVE:
			if (edge.weight == -1) {
				if (!graph.containsEdge(n1, n2)) {
					break;
				}
				Edge e = graph.getEdge(n1, n2);
				EdgeAddition ea = b.getEdgeAddition(e);
				if (ea == null) {
					if (graph.containsEdge(e)) {
						b.add(new EdgeRemoval(e));
					}
				} else {
					b.remove(ea);
				}
			} else if (edge.weight == 1) {
				Edge e = r.gds.newEdgeInstance(n1, n2);
				EdgeRemoval er = b.getEdgeRemoval(e);
				if (er == null) {
					if (!graph.containsEdge(e)) {
						this.processNode(graph, b, n1);
						this.processNode(graph, b, n2);
						b.add(new EdgeAddition(e));
					}
				} else {
					b.remove(er);
				}
			} else {
				Log.error("invalid weight for ADD_REMOVE: " + edge);
			}
			break;
		case MULTI:
			// System.out.println("processing: " + edge);
			if (edge.weight == 1) {
				if (graph.containsEdge(n1, n2)) {
					IWeightedEdge e = (IWeightedEdge) graph.getEdge(n1, n2);
					int current = ((IntWeight) e.getWeight()).getWeight();
					EdgeWeight ew = b.getEdgeWeight(e);
					EdgeRemoval er = b.getEdgeRemoval((Edge) e);
					if (ew != null) {
						if (((IntWeight) ew.getWeight()).getWeight() == current - 1) {
							b.remove(ew);
						} else {
							((IntWeight) ew.getWeight()).increaseWeight(1);
						}
					} else if (er != null) {
						if (current == 1) {
							b.remove(er);
						} else {
							b.remove(er);
							b.add(new EdgeWeight(e, new IntWeight(1)));
						}
					} else {
						b.add(new EdgeWeight(e, new IntWeight(current + 1)));
					}
				} else {
					this.processNode(graph, b, n1);
					this.processNode(graph, b, n2);
					IWeightedEdge e = (IWeightedEdge) r.gds.newEdgeInstance(n1,
							n2);
					EdgeAddition ea = b.getEdgeAddition((Edge) e);
					if (ea == null) {
						((IntWeight) e.getWeight()).setWeight(1);
						b.add(new EdgeAddition(e));
					} else {
						((IntWeight) ((IWeightedEdge) ea.getEdge()).getWeight())
								.increaseWeight(1);
					}
				}
			} else if (edge.weight == -1) {
				if (graph.containsEdge(n1, n2)) {
					IWeightedEdge e = (IWeightedEdge) graph.getEdge(n1, n2);
					int current = ((IntWeight) e.getWeight()).getWeight();
					EdgeWeight ew = b.getEdgeWeight(e);
					EdgeRemoval er = b.getEdgeRemoval((Edge) e);
					if (ew != null) {
						if (current > 1) {
							((IntWeight) ew.getWeight()).setWeight(current - 1);
						} else if (current == 1) {
							b.remove(ew);
							b.add(new EdgeRemoval(e));
						} else {
							Log.error("invalid EW: " + ew);
						}
					} else if (er != null) {
						Log.error("removing edge with invalid weight: " + er);
					} else {
						if (current > 1) {
							b.add(new EdgeWeight(e, new IntWeight(current - 1)));
						} else if (current == 1) {
							b.add(new EdgeRemoval(e));
						} else {
							Log.error("invalid weight: " + e);
						}
					}
				} else {
					this.processNode(graph, b, n1);
					this.processNode(graph, b, n2);
					IWeightedEdge e = (IWeightedEdge) r.gds.newEdgeInstance(n1,
							n2);
					EdgeAddition ea = b.getEdgeAddition((Edge) e);
					if (ea != null) {
						((IntWeight) ((IWeightedEdge) ea.getEdge()).getWeight())
								.decreaseWeight(1);
					} else {
						Log.error("cannot decrease weight for non-existing edge: "
								+ e);
					}
				}
			} else {
				Log.error("invalid weight for MULTI: " + edge);
			}
			break;
		case WEIGHTED:
			if (!graph.containsEdge(n1, n2)) {
				this.processNode(graph, b, n1);
				this.processNode(graph, b, n2);
				IWeightedEdge e = (IWeightedEdge) r.gds.newEdgeInstance(n1, n2);
				((IntWeight) e.getWeight()).setWeight((int) edge.weight);
				EdgeAddition ea = b.getEdgeAddition((Edge) e);
				if (ea == null) {
					b.add(new EdgeAddition(e));
				} else {
					((IntWeight) ((IWeightedEdge) ea.getEdge()))
							.setWeight((int) edge.weight);
				}
			} else {
				IWeightedEdge e = (IWeightedEdge) graph.getEdge(n1, n2);
				EdgeWeight ew = b.getEdgeWeight(e);
				if (ew == null) {
					ew = new EdgeWeight(e, new IntWeight((int) edge.weight));
					b.add(ew);
				} else {
					((IntWeight) ew.getWeight()).setWeight((int) edge.weight);
				}
				if (((IntWeight) ew.getWeight()).getWeight() == ((IntWeight) ((IWeightedEdge) ew
						.getEdge()).getWeight()).getWeight()) {
					b.remove(ew);
				}
			}
			break;
		default:
			break;
		}
	}

	protected void processNode(IGraph g, Batch b, Node n) {
		if (g.containsNode(n)) {
			NodeRemoval nr = b.getNodeRemoval(n);
			if (nr != null) {
				b.remove(nr);
			}
		} else {
			NodeAddition na = b.getNodeAddition(n);
			if (na == null) {
				b.add(new NodeAddition(n));
			}
		}
	}

	@Override
	public void reset() {
		timestampsIndex = 0;
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return r.peek() != null;
	}

}
