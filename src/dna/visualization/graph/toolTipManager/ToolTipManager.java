package dna.visualization.graph.toolTipManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import dna.graph.weights.Weight;
import dna.util.Log;
import dna.util.parameters.Parameter;
import dna.visualization.config.graph.toolTips.ToolTipConfig;
import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.toolTips.ToolTip;

/**
 * The ToolTipManager handles the ToolTips used in the GraphVisualization.
 * 
 * <p>
 * 
 * Every GraphPanel (which has ToolTips enabled) uses one ToolTipManager. The
 * manager handles all the ToolTips/Sprites shown when right-clicking a node.
 * 
 * @author Rwilmes
 * @date 16.12.2015
 */
public class ToolTipManager extends GraphStyleRule {

	// registered components
	private GraphPanel panel;
	private SpriteManager sm;

	// class fields
	private String name;
	private ArrayList<String> toolTipsNames;
	private ArrayList<Class<?>> toolTipsClasses;
	private ArrayList<Parameter[]> toolTipsParams;

	// configuration
	private int distance;
	private int offset;
	private double angle;

	/** ToolTipManager constructor. **/
	public ToolTipManager(String name, SpriteManager sm, GraphPanel panel,
			int distance, int offset, double angle) {
		this.name = name;
		this.sm = sm;
		this.panel = panel;
		this.distance = distance;
		this.offset = offset;
		this.angle = angle;
		this.toolTipsNames = new ArrayList<String>();
		this.toolTipsClasses = new ArrayList<Class<?>>();
		this.toolTipsParams = new ArrayList<Parameter[]>();
	}

	/** Adds a tooltip of the given name and class to the manager. **/
	public void addToolTip(String name, Class<?> cl) {
		addToolTip(name, cl, new Parameter[0]);
	}

	/** Adds a tooltip of the given name, class and params to the manager. **/
	public void addToolTip(String name, Class<?> cl, Parameter[] params) {
		this.toolTipsNames.add(name);
		this.toolTipsClasses.add(cl);
		this.toolTipsParams.add(params);
	}

	/** Adds a tooltip from the given tool-tip-config. **/
	public void addToolTip(ToolTipConfig ttCfg) {
		try {
			addToolTip(ttCfg.getName(), Class.forName(ttCfg.getKey()),
					ttCfg.getParams());
		} catch (ClassNotFoundException e) {
			Log.warn("ToolTip class: " + ttCfg.getKey() + " not found!");
			e.printStackTrace();
		}
	}

	/** Adds multiple tooltips from their configs. **/
	public void addToolTips(ArrayList<ToolTipConfig> toolTips) {
		// sort by index
		HashMap<Integer, ArrayList<ToolTipConfig>> indexListMap = new HashMap<Integer, ArrayList<ToolTipConfig>>();
		ArrayList<ToolTipConfig> lastlyAdded = new ArrayList<ToolTipConfig>();

		for (ToolTipConfig ttCfg : toolTips) {
			int index = ttCfg.getIndex();

			if (index >= 0) {
				if (!indexListMap.containsKey(index))
					indexListMap.put(index, new ArrayList<ToolTipConfig>());

				indexListMap.get(index).add(ttCfg);
			} else {
				lastlyAdded.add(ttCfg);
			}
		}

		for (Integer index : indexListMap.keySet()) {
			for (ToolTipConfig ttCfg : indexListMap.get(index)) {
				addToolTip(ttCfg);
			}
		}
		for (ToolTipConfig ttCfg : lastlyAdded) {
			addToolTip(ttCfg);
		}
	}

	/** Removes the tooltip from the manager. **/
	public void removeToolTip(String name, Class<?> cl) {
		for (int i = 0; i < this.toolTipsClasses.size(); i++) {
			if (this.toolTipsClasses.get(i).equals(cl)
					&& this.toolTipsNames.get(i).equals(name)) {
				this.toolTipsNames.remove(i);
				this.toolTipsClasses.remove(i);
				this.toolTipsParams.remove(i);
			}
		}
	}

	/** Returns the managers name. **/
	public String getName() {
		return this.name;
	}

	/** Returns if the node has ToolTips shown at the moment. **/
	protected static boolean isToolTipsActive(Node node) {
		return node.hasAttribute(ToolTip.GraphVisToolTipActiveKey);
	}

	/** Sets ToolTips shown flag. **/
	protected static void setToolTipsActive(Node node) {
		node.setAttribute(ToolTip.GraphVisToolTipActiveKey);
	}

	/** Removes ToolTips shown flag. **/
	protected static void setToolTipsInactive(Node node) {
		node.removeAttribute(ToolTip.GraphVisToolTipActiveKey);
	}

	/** Returns the ToolTip id for tooltip with index i and node n. **/
	protected String getToolTipId(int i, Node n) {
		return getToolTipId(this.toolTipsNames.get(i),
				this.toolTipsClasses.get(i), n.getId());
	}

	/** Returns the ToolTip id based on the parameters. **/
	protected static String getToolTipId(String name, Class<?> toolTipClass,
			String nodeId) {
		return name + "_" + toolTipClass.getSimpleName() + "_" + nodeId;
	}

	/** Gathers all ToolTips on the given node. **/
	protected ArrayList<ToolTip> getAllToolTips(Node node) {
		ArrayList<ToolTip> list = new ArrayList<ToolTip>();
		for (int i = 0; i < this.toolTipsClasses.size(); i++) {
			String toolTipId = getToolTipId(i, node);
			if (sm.hasSprite(toolTipId))
				list.add(ToolTip.getFromSprite(sm.getSprite(toolTipId)));
		}

		return list;
	}

	/** Hides all ToolTips from the given node. **/
	protected void hideAllToolTips(Node node) {
		for (int i = 0; i < this.toolTipsClasses.size(); i++) {
			String toolTipId = getToolTipId(this.toolTipsNames.get(i),
					this.toolTipsClasses.get(i), node.getId());
			if (sm.hasSprite(toolTipId))
				sm.removeSprite(toolTipId);
		}

		// remove flag
		setToolTipsInactive(node);
	}

	/** Init class tooltip. **/
	protected void initToolTip(String name, Class<?> ttCl, Parameter[] params,
			Node node, int offset) {
		String nodeId = node.getId();
		ToolTip tt = null;

		// craft sprite
		String ttId = getToolTipId(name, ttCl, nodeId);
		Sprite sprite = sm.addSprite(ttId);

		try {
			Constructor<?> cons = ttCl.getConstructor(Sprite.class,
					String.class, Node.class, Parameter[].class);
			tt = (ToolTip) cons.newInstance(sprite, name, node, params);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			Log.error("problem when instantiating ToolTip: " + name
					+ " of class " + ttCl);
			e.printStackTrace();
		}

		tt.setPosition(distance + offset, angle);
	}

	/** Shows all ToolTips on the given node. **/
	protected void showAllToolTips(Node node) {
		// iterate over all set tooltips
		for (int i = 0; i < this.toolTipsClasses.size(); i++) {
			initToolTip(this.toolTipsNames.get(i), this.toolTipsClasses.get(i),
					this.toolTipsParams.get(i), node, i * offset);
		}

		// set tooltips-active flag
		setToolTipsActive(node);
	}

	/** Called when a left-click on a node occurs. **/
	public void onMouseLeftClick(GraphicNode graphicNode) {
		// DO NOTHING
	}

	/** Called when a right-click on a node occurs. **/
	public void onMouseRightClick(GraphicNode graphicNode) {
		if (this.toolTipsNames.size() < 1)
			return;

		String nodeId = graphicNode.getId();
		Node node = this.panel.getGraph().getNode(nodeId);
		boolean toolTipsActive = isToolTipsActive(node);

		if (toolTipsActive) {
			// if active -> remove
			hideAllToolTips(node);
		} else {
			// if not active -> create
			showAllToolTips(node);
		}
	}

	/** Called when a node is being removed. **/
	public void onNodeRemoval(Node node) {
		if (isToolTipsActive(node))
			hideAllToolTips(node);
	}

	/** Called when a node weight changes. **/
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		for (ToolTip tt : getAllToolTips(n))
			tt.onNodeWeightChange(n, wNew, wOld);
	}

	/** Called when an edge is being added. **/
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		for (ToolTip tt : getAllToolTips(n1))
			tt.onEdgeAddition(e, n1, n2);

		for (ToolTip tt : getAllToolTips(n2))
			tt.onEdgeAddition(e, n1, n2);
	}

	/** Called when an edge is being removed. **/
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		for (ToolTip tt : getAllToolTips(n1))
			tt.onEdgeRemoval(e, n1, n2);

		for (ToolTip tt : getAllToolTips(n2))
			tt.onEdgeRemoval(e, n1, n2);
	}

	@Override
	public String toString() {
		return "ToolTipManager: '" + this.name + "'";
	}
}