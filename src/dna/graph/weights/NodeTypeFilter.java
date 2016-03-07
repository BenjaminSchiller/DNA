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

	@Override
	public boolean apply(IElement n_) {
		Node n = (Node) n_;
		return n instanceof IWeightedNode
				&& ((IWeightedNode) n).getWeight() instanceof ITypedWeight
				&& this.types.contains(((ITypedWeight) ((IWeightedNode) n)
						.getWeight()).getType());
	}

}
