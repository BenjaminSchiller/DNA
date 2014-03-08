package dna.metrics.connectivity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

/**
 * 
 * Abstract super class for all metrics that compute the connetivity of a graph,
 * i.e., the connected components and their sizes. the following three metrics
 * are returned: (1) the number of components (2) the size of the largest
 * component (3) the average component size (4) list of the sizes of all
 * components (as a "distribution")
 * 
 * @author benni
 * 
 */
public abstract class Connectivity extends Metric {

	protected HashMap<Node, ConnectedComponent> nodeComponents;

	protected HashSet<ConnectedComponent> components;

	protected int maxIndex = 0;

	public Connectivity(String name, ApplicationType type,
			MetricType metricType, Parameter... p) {
		super(name, type, metricType, p);
	}

	protected abstract Collection<Node> getConnectedNodes(Node n);

	@Override
	public boolean compute() {

		for (IElement node : this.g.getNodes()) {
			if (this.nodeComponents.containsKey(node)) {
				continue;
			}
			Collection<Node> nodes = this.getConnectedNodes((Node) node);
			ConnectedComponent c = this.addNewComponent();
			for (Node n : nodes) {
				this.addNodeToComponent(n, c);
			}
		}

		return true;
	}

	/**
	 * creates and adds a new empty components
	 * 
	 * @return created component
	 */
	protected ConnectedComponent addNewComponent() {
		ConnectedComponent c = new ConnectedComponent(this.maxIndex++);
		this.components.add(c);
		return c;
	}

	/**
	 * 
	 * creates and adds a new components containing one single node
	 * 
	 * @param node
	 *            single node to be contained in the new component
	 * @return created component
	 */
	protected ConnectedComponent addNewComponent(Node node) {
		ConnectedComponent c = new ConnectedComponent(this.maxIndex++);
		this.components.add(c);
		this.addNodeToComponent(node, c);
		return c;
	}

	/**
	 * 
	 * creates and adds a new component containing the given set of nodes
	 * 
	 * @param nodes
	 *            set of nodes to be contained in the new component
	 * @return created component
	 */
	protected ConnectedComponent addNewComponent(Collection<Node> nodes) {
		ConnectedComponent c = new ConnectedComponent(this.maxIndex++, nodes);
		this.components.add(c);
		for (Node node : nodes) {
			this.addNodeToComponent(node, c);
		}
		return c;
	}

	/**
	 * 
	 * adds the given node to the given component
	 * 
	 * @param node
	 *            node to be added
	 * @param c
	 *            component to add the node to
	 */
	protected void addNodeToComponent(Node node, ConnectedComponent c) {
		c.addNode(node);
		this.nodeComponents.put(node, c);
	}

	/**
	 * 
	 * merges the two given components, i.e., adds all nodes from the second
	 * component to the first and deleted the second component afterwards
	 * 
	 * @param a
	 *            first component (will remain)
	 * @param b
	 *            second component (will be removed)
	 */
	protected void mergeComponents(ConnectedComponent a, ConnectedComponent b) {
		for (Node n : b.getNodes()) {
			this.addNodeToComponent(n, a);
		}
		this.components.remove(b);
	}

	/**
	 * removes the set of nodes from the component and adds them to a new
	 * component
	 * 
	 * @param c
	 *            component to remove the nodes from
	 * @param nodes
	 *            nodes to remove from the given component and add to the new
	 *            component
	 */
	protected void splitOff(ConnectedComponent c, Collection<Node> nodes) {
		c.removeNodes(nodes);
		this.addNewComponent(nodes);
	}

	@Override
	public void init_() {
		this.nodeComponents = new HashMap<Node, ConnectedComponent>();
		this.components = new HashSet<ConnectedComponent>();
		this.maxIndex = 0;
	}

	@Override
	public void reset_() {
		this.nodeComponents = new HashMap<Node, ConnectedComponent>();
		this.components = new HashSet<ConnectedComponent>();
		this.maxIndex = 0;
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("NumberOfComponents", this.components.size());
		Value v2 = new Value("MaxComponentSize", this.getMaxComponentSize());
		Value v3 = new Value("AverageComponentSize",
				(double) this.g.getNodeCount()
						/ (double) this.components.size());
		return new Value[] { v1, v2, v3 };
	}

	protected int getMaxComponentSize() {
		int max = 0;
		for (ConnectedComponent c : this.components) {
			if (c.getSize() > max) {
				max = c.getSize();
			}
		}
		return max;
	}

	@Override
	public Distribution[] getDistributions() {
		ConnectedComponent[] c = new ConnectedComponent[this.components.size()];
		int index = 0;
		for (ConnectedComponent comp : this.components) {
			c[index++] = comp;
		}
		Arrays.sort(c);
		int[] v = new int[c.length];
		for (int i = 0; i < c.length; i++) {
			v[i] = c[i].getSize();
		}
		DistributionInt d = new DistributionInt("Components", v,
				this.g.getNodeCount());
		return new Distribution[] { d };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof Connectivity)) {
			return false;
		}
		Connectivity c = (Connectivity) m;
		boolean success = true;
		success &= ArrayUtils.equals(
				((DistributionInt) this.getDistributions()[0]).getIntValues(),
				((DistributionInt) c.getDistributions()[0]).getIntValues(),
				"Connectivity.Components");
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

	public Iterable<ConnectedComponent> getComponents() {
		return this.components;
	}

}
