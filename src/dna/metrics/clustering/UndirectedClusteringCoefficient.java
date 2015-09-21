package dna.metrics.clustering;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.series.data.lists.LongList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class UndirectedClusteringCoefficient extends
		ClusteringCoefficient {

	public UndirectedClusteringCoefficient(String name, Parameter... p) {
		super(name, p);
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	protected boolean compute() {
		this.globalCC = 0;
		this.averageCC = 0;
		this.localCC = new NodeValueList("localCC",
				this.g.getMaxNodeIndex() + 1);
		this.triangleCount = 0;
		this.potentialCount = 0;
		// this.nodeTriangleCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
		// Long.MIN_VALUE);
		// this.nodePotentialCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
		// Long.MIN_VALUE);
		this.nodePotentialCount = new LongList(g.getMaxNodeIndex() + 1);
		this.nodeTriangleCount = new LongList(g.getMaxNodeIndex() + 1);

		if (g.isDirected()) {
			return this.computeDirected();
		} else if (!g.isDirected()) {
			return this.computeUndirected();
		}
		return false;
	}

	protected boolean computeUndirected() {

		for (IElement aUncasted : g.getNodes()) {
			UndirectedNode a = (UndirectedNode) aUncasted;

			for (IElement e1Uncasted : a.getEdges()) {
				UndirectedEdge e1 = (UndirectedEdge) e1Uncasted;
				UndirectedNode b = (UndirectedNode) e1.getDifferingNode(a);
				for (IElement e2Uncasted : a.getEdges()) {
					UndirectedEdge e2 = (UndirectedEdge) e2Uncasted;
					UndirectedNode c = (UndirectedNode) e2.getDifferingNode(a);
					if (b.equals(c)) {
						continue;
					}
					this.nodePotentialCount.incr(a.getIndex());
					if (b.hasEdge(b, c)) {
						this.nodeTriangleCount.incr(a.getIndex());
					}
				}
			}
			this.nodeTriangleCount.div(a.getIndex(), 2);
			this.nodePotentialCount.div(a.getIndex(), 2);

			this.update(a.getIndex());
		}

		this.update();
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	protected boolean computeDirected() {

		for (IElement aUncasted : g.getNodes()) {
			DirectedNode a = (DirectedNode) aUncasted;

			for (IElement bUncasted : a.getNeighbors()) {
				DirectedNode b = (DirectedNode) bUncasted;
				for (IElement cUncasted : a.getNeighbors()) {
					DirectedNode c = (DirectedNode) cUncasted;
					if (b.equals(c)) {
						continue;
					}
					this.nodePotentialCount.incr(a.getIndex());
					if (b.hasNeighbor(c)) {
						this.nodeTriangleCount.incr(a.getIndex());
					}
				}
			}
			this.nodeTriangleCount.div(a.getIndex(), 2);
			this.nodePotentialCount.div(a.getIndex(), 2);

			this.update(a.getIndex());
		}

		this.update();

		return true;
	}

	public void update(int index) {
		this.triangleCount += this.nodeTriangleCount.getValue(index);
		this.potentialCount += this.nodePotentialCount.getValue(index);
		if (this.nodePotentialCount.getValue(index) == 0) {
			this.localCC.setValue(index, 0);
		} else {
			this.localCC.setValue(index,
					(double) this.nodeTriangleCount.getValue(index)
							/ (double) this.nodePotentialCount.getValue(index));
		}
	}

	public void update() {
		if (this.potentialCount == 0) {
			this.globalCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
		}
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());
	}

}
