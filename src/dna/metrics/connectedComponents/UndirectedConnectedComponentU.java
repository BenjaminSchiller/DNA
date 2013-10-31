package dna.metrics.connectedComponents;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedConnectedComponentU extends UndirectedConnectedComponent {

	public UndirectedConnectedComponentU() {
		super("CCUndirectedU", ApplicationType.AfterUpdate);
	}

	public void init() {
		super.init();
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition) {
			return applyAfterNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemoval(u);
		}
		return false;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (lookUp(n1) == lookUp(n2)) {
			if (this.parents.containsKey(n1) && parents.get(n1).equals(n2)) {
				checkEdgeRemoval(n2, n1);
				return true;
			}
			if (this.parents.containsKey(n2) && parents.get(n2).equals(n1)) {
				checkEdgeRemoval(n1, n2);
				return true;
			}

		}

		return true;
	}

	private void checkEdgeRemoval(UndirectedNode n1, UndirectedNode n2) {
		boolean neighbourFound = false;

		if (n1.getDegree() == 0) {
			this.componentList.get(lookUp(n1)).decreaseSize(1);
			counter++;
			UndirectedComponent c = new UndirectedComponent(counter);
			c.setSize(1);
			parents.remove(n2);
			this.nodeComponentMembership.put(n1, counter);
			this.componentList.put(counter, c);
			return;
		}

		if (n2.getDegree() == 0) {
			parents.remove(n2);
			counter++;
			this.componentList.get(lookUp(n2)).decreaseSize(1);
			UndirectedComponent c = new UndirectedComponent(counter);
			c.setSize(1);
			this.nodeComponentMembership.put(n2, counter);
			this.componentList.put(counter, c);
			return;
		}

		// check for direct neighbour
		HashSet<UndirectedNode> reachableNodes = new HashSet<UndirectedNode>();
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
				if (parents.containsKey(node) && parents.get(node).equals(n2))
					parents.put(node, n1);
				break;
			}
		}

		if (!neighbourFound) {
			neighbourFound = saveRemove(n2, reachableNodes, n1);
		}

	}

	private boolean saveRemove(UndirectedNode n,
			HashSet<UndirectedNode> reachables, UndirectedNode target) {

		Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
		q.add(n);

		HashSet<UndirectedNode> seenNodes = new HashSet<UndirectedNode>();
		HashSet<UndirectedNode> reachableNodes = new HashSet<UndirectedNode>();
		seenNodes.add(n);
		boolean found = false;
		UndirectedNode temp = n;
		while (!q.isEmpty() && !found) {
			temp = q.poll();
			for (IElement ie : temp.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode x = ed.getDifferingNode(temp);

				if (parents.containsKey(x) && parents.get(x).equals(temp)) {
					q.add(x);
					reachableNodes.remove(x);
					seenNodes.add(x);
					continue;
				}
				if (!seenNodes.contains(x)) {
					reachableNodes.add(x);
				}
			}
		}

		if (reachableNodes.isEmpty()) {
			counter++;
			UndirectedComponent c = new UndirectedComponent(counter);
			this.componentList.put(counter, c);
			c.setSize(seenNodes.size());
			this.parents.remove(n);
			this.componentList.get(lookUp(n)).decreaseSize(seenNodes.size());
			for (UndirectedNode uN : seenNodes) {
				this.nodeComponentMembership.put(uN, counter);
			}
			return false;
		}

		UndirectedNode connection = reachableNodes.iterator().next();
		for (IElement ie : connection.getEdges()) {
			UndirectedEdge ed = (UndirectedEdge) ie;
			UndirectedNode node = ed.getDifferingNode(connection);
			if (seenNodes.contains(node)) {
				restructureTree(connection, node, n);
				break;
			}
		}

		return true;

	}

	private void restructureTree(UndirectedNode connection,
			UndirectedNode node, UndirectedNode start) {
		UndirectedNode temp = node;
		UndirectedNode newParent = connection;
		UndirectedNode newChild = parents.get(temp);

		while (!temp.equals(start)) {
			newChild = parents.get(temp);
			parents.put(temp, newParent);

			newParent = temp;
			temp = newChild;
		}
		parents.put(temp, newParent);
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		int c1 = lookUp(n1);
		Integer c2 = lookUp(n2);
		if (c1 != c2) {
			if (this.componentList.get(c1).getSize() < this.componentList.get(
					c2).getSize()) {
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
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();

		g.addNode(n);
		HashSet<UndirectedEdge> bla = new HashSet<>();
		for (IElement ie : n.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			e.connectToNodes();
			bla.add(e);
		}
		for (UndirectedEdge e : bla) {
			e.disconnectFromNodes();
			applyAfterEdgeRemoval(new EdgeRemoval(e));
		}

		this.componentList.remove(this.lookUp(n));
		this.nodeComponentMembership.remove(n);
		g.removeNode(n);
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();
		counter++;
		UndirectedComponent stn = new UndirectedComponent(counter);
		stn.setSize(1);
		this.componentList.put(counter, stn);
		this.nodeComponentMembership.put(n, counter);
		return true;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}
}
