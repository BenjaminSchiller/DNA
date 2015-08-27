package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.INode;

public class HubAddCandidatePickerRan implements IHubAddCandidatePicker{
	
	@Override
	public Collection<INode> getNextHubAddCandidate(int amount, Graph graph, HubManager hubManager,
			int minHubDegree) {
		
		List<IElement> nodes = Lists.newArrayList(graph.getNodes());
		List<INode> choosenNodes = new ArrayList<>();
		
		Random r = new Random();
		while (choosenNodes.size() <= amount && nodes.size() > 0) {
			int next = r.nextInt(nodes.size());
			INode node = (INode) nodes.get(next);
			if (node.getDegree() >= minHubDegree) {
				choosenNodes.add(node);
			}
			nodes.remove(next);
		}
		
		return choosenNodes;
	}

}
