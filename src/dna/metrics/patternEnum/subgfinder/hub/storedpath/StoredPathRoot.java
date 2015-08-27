package dna.metrics.patternEnum.subgfinder.hub.storedpath;

import dna.graph.nodes.INode;

/**
 * Represents the root of the stored paths.
 * 
 * @author Bastian Laur
 *
 */
public class StoredPathRoot {
	private INode associatedNode;
	private StoredPathVertex storedPathRoot;
	
	public INode getAssociatedNode() {
		return associatedNode;
	}
	
	public void setAssociatedNode(INode associatedNode) {
		this.associatedNode = associatedNode;
	}
	
	public StoredPathVertex getStoredPathRoot() {
		return storedPathRoot;
	}
	
	public void setStoredPathRoot(StoredPathVertex storedPathRoot) {
		this.storedPathRoot = storedPathRoot;
	}
	
	public StoredPathRoot(INode associatedNode, StoredPathVertex storedPathRoot) {
		super();
		this.associatedNode = associatedNode;
		this.storedPathRoot = storedPathRoot;
	}
	
	public int totalNodeSize() {
		return totalNodeSize(storedPathRoot);
	}
	
	private int totalNodeSize(StoredPathVertex spv) {
		int count = 1;
		
		for (StoredPathVertex next : spv.getNextVertices()) {
			count += totalNodeSize(next);
		}
		
		return count;
	}
}
