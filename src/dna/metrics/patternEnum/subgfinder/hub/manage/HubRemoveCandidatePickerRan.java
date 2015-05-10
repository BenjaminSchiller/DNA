package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import dna.graph.Graph;
import dna.graph.nodes.INode;
import dna.metrics.patternEnum.subgfinder.hub.storedpath.StoredPathRoot;

public class HubRemoveCandidatePickerRan implements IHubRemoveCandidatePicker {

	@Override
	public Collection<INode> getNextRemoveHubCandidate(int amount, Graph graph, HubManager hubManager,
			int minHubDegree) {
		ArrayList<INode> choosenNodes = new ArrayList<>();
				
		HashMap<Integer, StoredPathRoot> hubs = hubManager.getStoredPathInfos();
		List<Integer> keys = Arrays.asList((Integer[])hubs.keySet().toArray());
		Random r = new Random();
		while (choosenNodes.size() <= amount && keys.size() > 0) {
			int next = r.nextInt(keys.size());
			Integer key = keys.get(next);
			INode node = hubs.get(key).getStoredPathRoot().getVertex();
			choosenNodes.add(node);
			keys.remove(next);
		}
		
		return choosenNodes;
	}

}
