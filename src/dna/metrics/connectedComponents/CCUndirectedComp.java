package dna.metrics.connectedComponents;

import dna.graph.Graph;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.Update;

public class CCUndirectedComp extends CCUndirected {

	public CCUndirectedComp() {
		super("CCUndirectedDyn", ApplicationType.Recomputation);
	}

	@Override
	public boolean compute() {
		UndirectedGraph g = (UndirectedGraph) this.g;
		for (UndirectedNode n : g.getNodes()) {
			if (!this.visited[n.getIndex()]) {
				bfs(n);
			}
		}
		for (SpanningTreeNode n : nodesTreeElement) {
			if (n.getWeight() == 0) {
				calculateWeights(n);
			}
		}
		return true;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void init_() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
