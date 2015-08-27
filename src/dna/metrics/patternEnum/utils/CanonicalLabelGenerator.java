package dna.metrics.patternEnum.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.AdjacencyMatrix;
import dna.metrics.patternEnum.datastructures.SmallGraph;


public class CanonicalLabelGenerator {
	
	public long genCanonicalLabelFor(SmallGraph g) {
		
		Collection<List<INode>> allNodeCombinations = genAllNodeCombinations(g);
		
		long lowesAdjMatrixId = Long.MAX_VALUE;
		for(List<INode> nodeCombination : allNodeCombinations) {
			AdjacencyMatrix am = new AdjacencyMatrix(nodeCombination);
			long id = getAdjMatrixAsLong(am);
			if(id < lowesAdjMatrixId) {
				lowesAdjMatrixId = id;
			}
		}
		
		return lowesAdjMatrixId;
	}
	
	private Collection<List<INode>> genAllNodeCombinations(SmallGraph g){
		return genAllNodeCombinationsRec(g.getNodes(), new ArrayList<INode>());
	}
	
	private Collection<List<INode>> genAllNodeCombinationsRec(Collection<INode> allNodes, List<INode> usedNodes){
		Collection<List<INode>> resultList = new ArrayList<>();
		
		if(allNodes.size() == usedNodes.size()) {
			resultList.add(usedNodes);
			return resultList;
		}
		
		for(INode node : allNodes) {
			if(!usedNodes.contains(node)) {
				ArrayList<INode> newUsedNodes = new ArrayList<INode>(usedNodes);
				newUsedNodes.add(node);
				resultList.addAll(genAllNodeCombinationsRec(allNodes, newUsedNodes));
			}
		}
		
		return resultList;
	}
	
	private long getAdjMatrixAsLong(AdjacencyMatrix m) {
		long value = 0L;
		int counter = 0;
		
		for( boolean[] row : m.getMatrix()) {
			for(boolean element : row) {
				if(element) {
					value += 1L << counter; 
				}
				counter++;
			}
		}
		
		return value;
	}
	
	public String labelToGraphRepresentation(long label, int nodeCount) {
		List<Integer> bits = convert(label, (int)Math.pow(nodeCount, 2));
		
		int nodeCounter = 0;
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<bits.size(); i++) {
			int otherNode = i % nodeCount;
			if(bits.get(i) == 1) {
				sb.append(String.format("[%s->%s], ", nodeCounter, otherNode));
			}
			
			if(i != 0 && (i+1) % nodeCount == 0) {
				nodeCounter++;
			}
		}
		
		return sb.toString();
	}
	
  private List<Integer> convert(long value, int size) {
    List<Integer> bits = new ArrayList<>(size);
    
    while (value != 0L) {
      if (value % 2L != 0) {
        bits.add(1);
      } else {
      	bits.add(0);
      }
      value = value >>> 1;
    }
    
    for(int i=bits.size(); i<size; i++) {
    	bits.add(0);
    }
    
    return bits;
  }
}
