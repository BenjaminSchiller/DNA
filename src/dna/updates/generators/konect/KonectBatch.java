package dna.updates.generators.konect;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.generators.konect.KonectEdge;
import dna.graph.generators.konect.KonectReader;
import dna.graph.generators.konect.KonectReader.KonectBatchType;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IntWeight;
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
	protected int batchParameter;

	protected boolean removeZeroDegreeNodes;

	public KonectBatch(KonectReader r, KonectBatchType batchType,
			int batchParameter) {
		this(r, batchType, batchParameter, true);
	}

	public KonectBatch(KonectReader r, KonectBatchType batchType,
			int batchParameter, boolean removeZeroDegreeNodes) {
		super("KonectBatch", new StringParameter("Name", r.name),
				new StringParameter("EdgeType", r.edgeType.toString()),
				new BooleanParameter("RemoveZeroDegreeNodes",
						removeZeroDegreeNodes));
		this.r = r;
		this.batchType = batchType;
		this.batchParameter = batchParameter;
		this.removeZeroDegreeNodes = removeZeroDegreeNodes;
	}

	@Override
	public Batch generate(Graph g) {

		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1);

		if (this.removeZeroDegreeNodes) {
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
								+ this.batchParameter) {
					b.setTo(b.getFrom() + this.batchParameter);
					break;
				}
			} else if (KonectBatchType.PROCESSED_EDGES.equals(this.batchType)) {
				if (processed >= this.batchParameter)
					break;
			} else if (KonectBatchType.BATCH_SIZE.equals(this.batchType)) {
				if (b.getSize() >= this.batchParameter)
					break;
			} else if (KonectBatchType.EDGE_GROWTH.equals(this.batchType)) {
				if (b.getEdgeAdditionsCount() - b.getEdgeRemovalsCount() >= this.batchParameter)
					break;
			} else if (KonectBatchType.NODE_GROWTH.equals(this.batchType)) {
				if (b.getNodeAdditionsCount() - b.getNodeRemovalsCount() >= this.batchParameter)
					break;
			}

			try {
				this.process(g, b, r.readEdge());
			} catch (IOException e) {
				e.printStackTrace();
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
	protected void process(Graph graph, Batch b, KonectEdge edge) {

		if (edge.n1 == edge.n2) {
			return;
		}

		Node n1 = r.getNode(edge.n1);
		Node n2 = r.getNode(edge.n2);

		switch (r.edgeType) {
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
		case MULTI_UNWEIGHTED:
			if (edge.weight == 1) {
				if (graph.containsEdge(n1, n2)) {
					IWeightedEdge e = (IWeightedEdge) graph.getEdge(n1, n2);
					EdgeWeight ew = b.getEdgeWeight(e);
					if (ew == null) {
						IntWeight w = new IntWeight(
								1 + ((IntWeight) e.getWeight()).getWeight());
						b.add(new EdgeWeight(e, w));
					} else {
						((IntWeight) ew.getWeight()).increaseWeight(1);
					}
				} else {
					this.processNode(graph, b, n1);
					this.processNode(graph, b, n2);
					IWeightedEdge e = (IWeightedEdge) r.gds.newEdgeInstance(n1,
							n2);
					EdgeAddition ea = b.getEdgeAddition((Edge) e);
					if (ea == null) {
						((IntWeight) e.getWeight()).setWeight(1);
						// System.out.println("adding edge: " + e);
						// System.out.println("  before: @@@ "
						// + b.getEdgeAdditions());
						// System.out.println("  before: --- "
						// + b.getEdgeAddition((Edge) e));
						b.add(new EdgeAddition(e));
						// System.out.println("  after: @@@ "
						// + b.getEdgeAdditions());
						// System.out.println("  after: --- "
						// + b.getEdgeAddition((Edge) e));
					} else {
						((IntWeight) ((IWeightedEdge) ea.getEdge()).getWeight())
								.increaseWeight(1);
					}
				}
			} else {
				Log.error("invalid weight for MULTI_UNWEIGHTED: " + edge);
			}
			break;
		case MULTI_RATING:
			break;
		case UNWEIGHTED:
			if (edge.weight == 1) {
				this.processNode(graph, b, n1);
				this.processNode(graph, b, n2);
				Edge e = r.gds.newEdgeInstance(n1, n2);
				EdgeAddition ea = b.getEdgeAddition(e);
				if (ea == null) {
					b.add(new EdgeAddition(e));
				}
			} else {
				Log.error("invalid weight for UNWEIGHTED: " + edge);
			}
			break;
		default:
			break;
		}
	}

	protected void processNode(Graph g, Batch b, Node n) {
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
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return r.peek() != null;
	}

}
