package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;

import dna.visualization.graph.GraphVisualization;

/** Utility class for graph style rules. **/
public class GraphStyleUtils {

	/** Returns the color of the element. **/
	public static Color getColor(Element e) {
		return e.getAttribute(GraphVisualization.colorKey);
	}

	/** Sets the element to a given color. **/
	public static void setColor(Element e, Color c) {
		e.setAttribute(GraphVisualization.colorKey, c);
	}

	/** Returns the size of the element. **/
	public static double getSize(Element e) {
		return e.getAttribute(GraphVisualization.sizeKey);
	}

	/** Sets the element to a given size. **/
	public static void setSize(Element e, double size) {
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Increases the size of the given element by value x. **/
	public static void increaseSize(Element e, double x) {
		double size = (double) e.getAttribute(GraphVisualization.sizeKey) + x;
		if (size < 0)
			size = 0;

		// set attribute
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Decreases the size of the given element by value x. **/
	public static void decreaseSize(Element e, double x) {
		GraphStyleUtils.increaseSize(e, -x);
	}

	/** Increases the size of the given element by value x. **/
	public static void increaseSize(Edge e, double x) {
		double size = (double) e.getAttribute(GraphVisualization.sizeKey) + x;

		// set attribute
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Different supported shapes for graphstream elements. **/
	public enum ElementShape {
		circle, box, rounded_box, diamond, triangle, cross
	}

	/** Sets a shape to an element. **/
	public static void setShape(Element e, ElementShape shape) {
		e.setAttribute(GraphVisualization.shapeKey,
				GraphStyleUtils.getStringFromShape(shape));
	}

	/** Returns the shape of an element. **/
	public static ElementShape getShape(Element e) {
		String shape = e.getAttribute(GraphVisualization.shapeKey);
		switch (shape) {
		case "circle":
			return ElementShape.circle;
		case "box":
			return ElementShape.box;
		case "rounded-box":
			return ElementShape.rounded_box;
		case "diamond":
			return ElementShape.diamond;
		case "triangle":
			return ElementShape.triangle;
		case "cross":
			return ElementShape.cross;
		default:
			return null;
		}
	}

	/** Returns a string representing the shape. **/
	public static String getStringFromShape(ElementShape shape) {
		switch (shape) {
		case rounded_box:
			return "rounded-box";
		default:
			return shape.toString();
		}
	}

	/** Updates the elements style. **/
	public static void updateStyle(Element e) {
		String style = "";
		// add shape
		if (e.hasAttribute(GraphVisualization.shapeKey))
			style += "shape: " + e.getAttribute(GraphVisualization.shapeKey)
					+ ";";

		// add color
		if (e.hasAttribute(GraphVisualization.colorKey)) {
			Color c = e.getAttribute(GraphVisualization.colorKey);
			style += "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + ","
					+ c.getBlue() + ");";
		}

		// calc size
		double size = (double) e.getAttribute(GraphVisualization.sizeKey);
		size = size < 0 ? 0 : size;

		style += "size: " + size + "px;";

		// set style
		e.setAttribute(GraphVisualization.styleKey, style);
	}
}
