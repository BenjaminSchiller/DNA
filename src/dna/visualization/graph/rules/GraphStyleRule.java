package dna.visualization.graph.rules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.Log;
import dna.util.parameters.Parameter;
import dna.visualization.config.graph.rules.GraphStyleRuleConfig;

/** Abstract class for all graph style rules. **/
public abstract class GraphStyleRule {

	protected String name;

	// used for rule application order
	protected int index;

	public void onNodeAddition(Node n, Weight w) {

	}

	public void onNodeRemoval(Node n) {

	}

	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {

	}

	public void onEdgeAddition(Edge e, Weight w, Node n1, Node n2) {

	}

	public void onEdgeRemoval(Edge e, Node n1, Node n2) {

	}

	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {

	}

	public String getName() {
		return this.name;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public abstract String toString();

	/** Returns a new GraphStyleRule instance based on the config. **/
	public static GraphStyleRule getRule(GraphStyleRuleConfig config) {
		GraphStyleRule rule = null;

		try {
			Class<?> cl = Class.forName(config.getKey());
			Constructor<?> cons = cl.getConstructor(String.class,
					Parameter[].class);
			rule = (GraphStyleRule) cons.newInstance(config.getName(),
					config.getParams());
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Log.error("problem when instantiating GraphStyleRule: "
					+ config.getKey());
			e.printStackTrace();
		}

		return rule;
	}
}
