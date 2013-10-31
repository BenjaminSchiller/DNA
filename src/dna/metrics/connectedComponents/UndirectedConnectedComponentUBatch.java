package dna.metrics.connectedComponents;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.Update;

public class UndirectedConnectedComponentUBatch extends
		UndirectedConnectedComponent {

	public UndirectedConnectedComponentUBatch() {
		super("CCUndirectedComp", ApplicationType.AfterBatch);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		int r = 0;

		for (EdgeRemoval re : b.getEdgeRemovals()) {
			UndirectedEdge e = (UndirectedEdge) re.getEdge();
			UndirectedNode n1 = e.getNode1();
			UndirectedNode n2 = e.getNode2();
			boolean neighbourFound = false;

			HashSet<UndirectedNode> reachableNodes = new HashSet<>();
			for (IElement ie : n1.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode node = ed.getDifferingNode(n1);
				reachableNodes.add(node);
			}
			for (IElement ie : n2.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode node = ed.getDifferingNode(n2);
				if (reachableNodes.contains(node)) {
					parents.put(n2, node);
					neighbourFound = true;
					break;
				}
			}

			if (!neighbourFound) {
				r += 1;
			}
		}

		if (r > 0) {
			r = 0;
			this.reset_();
			this.compute();
		} else {
			for (EdgeAddition ea : b.getEdgeAdditions()) {
				UndirectedEdge e = (UndirectedEdge) ea.getEdge();
				UndirectedNode n1 = e.getNode1();
				UndirectedNode n2 = e.getNode2();

				int c1 = lookUp(n1);
				int c2 = lookUp(n2);
				if (c1 != c2) {

					if (this.componentList.get(c1).getSize() < this.componentList
							.get(c2).getSize()) {
						n2 = e.getNode1();
						n1 = e.getNode2();
						int temp = c1;
						c1 = c2;
						c2 = temp;
					}
					this.componentList.get(c1).increaseSize(
							this.componentList.get(c2).getSize());

					UndirectedNode temp = n2;
					UndirectedNode newParent = n1;
					UndirectedNode newChild = parents.get(temp);

					while (!this.parents.containsKey(temp)) {
						newChild = parents.get(temp);
						parents.put(temp, newParent);
						newParent = temp;
						temp = newChild;
					}
					parents.put(temp, newParent);
					this.componentList.remove(c2);
					componentConnection.put(c2, c1);

				}
			}

		}

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
