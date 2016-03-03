package dna.visualization.graph.rules.edges;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

/**
 * A simple rule which adds the edge-weight to the label of an edge.<br>
 * <br>
 * 
 * <b>Note:</b> When instantiating with a label-string, it should contain the
 * substring $weight$, which will be replaced by the actual weight.<br>
 * For example: String label = "w=$weight$" <br>
 * 
 * @author Rwilmes
 * 
 */
public class EdgeWeightLabel extends GraphStyleRule {

	public static final String weightReplacement = "$weight$";

	protected String label;

	public EdgeWeightLabel() {
		this("w=" + weightReplacement);
	}

	public EdgeWeightLabel(String label) {
		this.label = label;
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		if (e.hasAttribute(GraphVisualization.weightKey)) {
			Weight w = e.getAttribute(GraphVisualization.weightKey);

			if (GraphStyleUtils.getLabel(e) == null)
				GraphStyleUtils.setLabel(e, craftLabel(w.asString()));
			else
				GraphStyleUtils.appendToLabel(e, ", "
						+ craftLabel(w.asString()));
		}
	}

	@Override
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
		String oldLabel = GraphStyleUtils.getLabel(e);
		String newWeightLabel = craftLabel(wNew.asString());

		if (oldLabel != null && wOld != null
				&& oldLabel.contains(craftLabel(wOld.asString())))
			GraphStyleUtils.setLabel(e, oldLabel.replace(
					craftLabel(wOld.asString()), newWeightLabel));
		else
			GraphStyleUtils.appendToLabel(e, newWeightLabel);
	}

	@Override
	public String toString() {
		return "EdgeWeightLabel-Rule";
	}

	protected String craftLabel(String weight) {
		return this.label.replace(weightReplacement, weight);
	}

}
