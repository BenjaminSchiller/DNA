package dna.graph.weights;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Predicate;

import dna.graph.IElement;
import dna.graph.nodes.Node;

public class NodeTypeFilter implements Predicate<IElement> {

	private Set<String> types;

	public NodeTypeFilter(String... types) {
		this.types = new HashSet<String>();
		for (String type : types) {
			this.types.add(type);
		}
	}

	public boolean isNodeOfAssignedType(Node n) {
		return this.types.contains(((ITypedWeight) ((IWeightedNode) n)
				.getWeight()).getType());
	}

	@Override
	public boolean apply(IElement n) {
		return this.isNodeOfAssignedType((Node) n);
	}

}
