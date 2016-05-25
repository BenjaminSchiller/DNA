package dna.visualization.graph.toolTips;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.weights.Weight;
import dna.util.Log;
import dna.visualization.graph.GraphVisualization;

/**
 * ToolTip is a wrapper class for the GraphStream Sprite class. Sprites are
 * objects inside the GraphStream graph that can be used to display information
 * and data. ToolTip introduces several methods that make the handling of
 * sprites more enjoyable.
 * 
 * <p>
 * 
 * In order to implement own ToolTips one has to give a constructor as follows:
 * 
 * <p>
 * 
 * public constructor(Sprite s, String name, Node n, Parameter[] params)
 * 
 * <p>
 * 
 * It will be called on initialization with exactly this parameters.<br>
 * The Parameter[] array can be used to pass additional parameters.<br>
 * Beside the constructor each ToolTip implementation<br>
 * (in this example the class would be "BananaToolTip")<br>
 * has to have a method:
 * 
 * <p>
 * 
 * <pre>
 * <code>
 * public static BananaToolTip getFromSprite(Sprite s) {<br>
 * 	return (BananaToolTip) ToolTip.getToolTipFromSprite(s);<br>
 * }
 * </code>
 * </pre>
 * <p>
 * 
 * For additional functionality, like mouse interactions, one can overwrite the<br>
 * onLeftClick() and onRightClick() methods.
 * 
 * <p>
 * 
 * Note that each ToolTip will uniquely identify one Sprite. Due to the nature
 * of Sprite handling in GraphStream this requires to save some data on each
 * Sprite: The ToolTips class path and the ToolTips name.
 * 
 * @author Rwilmes
 * @date 25.05.2016
 */
public abstract class ToolTip {

	public static final String GraphVisToolTipClassKey = "dna.tt.class";
	public static final String GraphVisToolTipNameKey = "dna.tt.name";

	public static final String GraphVisToolTipActiveKey = "dna.tt.active";

	public static final String GraphVisToolTipStorageKey = "dna.tooltip.storage";

	/** Sets the class on the Sprite as its ToolTip-Class. **/
	protected void setClass(Class<?> cl) {
		this.s.setAttribute(ToolTip.GraphVisToolTipClassKey, cl.getName());
	}

	/** Sprite the ToolTip is wrapped around. **/
	protected Sprite s;

	/** Sets the name of the ToolTip onto the Sprite. **/
	protected void setName(String name) {
		this.s.setAttribute(GraphVisToolTipNameKey, name);
	}

	/** Stores the ToolTip object on the Sprite. **/
	public void storeThisOnSprite() {
		this.s.setAttribute(GraphVisToolTipStorageKey, this);
	}

	/** Returns the ToolTips name from the Sprite. **/
	public String getName() {
		return this.s.getAttribute(GraphVisToolTipNameKey);
	}

	/**
	 * Sets the position around the node based on the rotation-angle and the
	 * distance.
	 **/
	public void setPosition(double distance, double angle) {
		this.s.setPosition(Units.PX, distance, 0, angle);
	}

	/** Attaches the ToolTip to a node. **/
	public void attachToNode(String nodeId) {
		this.s.attachToNode(nodeId);
	}

	/**
	 * Returns a ToolTip object from a Sprite s. Used to identify which ToolTip
	 * belongs to a specific sprite.
	 **/
	public static ToolTip getFromSprite(Sprite s) {
		String ttClassPath = getToolTipClassPathFromSprite(s);
		ToolTip tt = null;
		try {
			Class<?> ttCl = Class.forName(ttClassPath);
			Method m = ttCl.getDeclaredMethod("getFromSprite", Sprite.class);
			tt = (ToolTip) m.invoke(Sprite.class, s);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			Log.error("problem when instantiating ToolTip from class: "
					+ ttClassPath);
			e.printStackTrace();
		}

		return tt;
	}

	/** Returns the ToolTip class from a Sprite s. **/
	public static String getToolTipClassPathFromSprite(Sprite s) {
		if (!s.hasAttribute(GraphVisToolTipClassKey))
			return null;

		return s.getAttribute(GraphVisToolTipClassKey);
	}

	/** Default style of a tool-tip. **/
	public static final String default_style = "" + "shape:rounded-box; "
			+ "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(220,220,220, 150); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:center;";

	/** Sets the default style. **/
	public void setDefaultStyle() {
		this.s.setAttribute(GraphVisualization.styleKey, default_style);
	}

	/** Retrieves the stored ToolTip from a Sprite. **/
	public static final ToolTip getToolTipFromSprite(Sprite s) {
		if (s.hasAttribute(GraphVisToolTipStorageKey))
			return s.getAttribute(GraphVisToolTipStorageKey, ToolTip.class);

		return null;
	}

	/*
	 * METHODS FOR DYNAMIC EVENTS IN GRAPH
	 */
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {

	}

	public void onEdgeAddition(Edge e, Node n1, Node n2) {

	}

	public void onEdgeRemoval(Edge e, Node n1, Node n2) {

	}

	/*
	 * MOUSE INTERACTIONS
	 */

	/**
	 * Called when the button is clicked with the left mouse-button.
	 * 
	 * <p>
	 * 
	 * Overwrite to add actual logic to your tooltip.
	 * **/
	public void onLeftClick() {

	}

	/**
	 * Called when the button is clicked with the right mouse-button.
	 * 
	 * <p>
	 * 
	 * Overwrite to add actual logic to your tooltip.
	 * **/
	public void onRightClick() {

	}
}
