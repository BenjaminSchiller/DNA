package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import dna.graph.Graph;
import dna.graph.nodes.INode;

public class HubAddCandidatePickerImpl implements IHubAddCandidatePicker{

	@Override
	public Collection<INode> getNextHubAddCandidate(int amount, Graph graph, HubManager hubManager,
			int minHubDegree) {
		@SuppressWarnings("unchecked")
		Iterable<INode> nodes = (Iterable<INode>)(Object)graph.getNodes();
		List<INode> nodeList = Lists.newArrayList(nodes);
		
		Collections.sort(nodeList, new Comparator<INode>()
			{
				public int compare(INode n1, INode n2){
					return n2.getDegree() - n1.getDegree();
				}
			});
		
		return nodeList.subList(0, amount);
	}

}
