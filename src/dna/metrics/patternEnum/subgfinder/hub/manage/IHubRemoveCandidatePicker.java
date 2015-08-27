package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.Collection;

import dna.graph.Graph;
import dna.graph.nodes.INode;

public interface IHubRemoveCandidatePicker {
	Collection<INode> getNextRemoveHubCandidate(int amount, Graph graph, HubManager hubManager,
			int minHubDegree);
}
