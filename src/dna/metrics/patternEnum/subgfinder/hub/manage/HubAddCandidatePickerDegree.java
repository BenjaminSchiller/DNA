package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.INode;

public class HubAddCandidatePickerDegree implements IHubAddCandidatePicker{
	
	@Override
	public Collection<INode> getNextHubAddCandidate(int amount, Graph graph,
			final HubManager hubManager, final int minHubDegree) {
		@SuppressWarnings("unchecked")
		Iterable<INode> nodes = (Iterable<INode>)(Object)graph.getNodes();
		
		List<INode> filteredNodes = new ArrayList<>();
		for (INode node : nodes) {
			if (node.getDegree() >= minHubDegree
					&& !hubManager.getStoredPathInfos().containsKey(node.getIndex())) {
				filteredNodes.add(node);
			}
		}
		
		if (filteredNodes.isEmpty()) {
			return filteredNodes;
		}
		
		Collections.sort(filteredNodes, new Comparator<INode>() {
				public int compare(INode n1, INode n2){
					return n2.getDegree() - n1.getDegree();
				}
			});
		
		amount = Math.min(amount, filteredNodes.size() - 1);
		return filteredNodes.subList(0, amount);
	}

}
