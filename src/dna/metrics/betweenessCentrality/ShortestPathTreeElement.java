package dna.metrics.betweenessCentrality;

import java.util.ArrayList;
import java.util.List;

public class ShortestPathTreeElement {
	private List<ShortestPathTreeElement> parents;
	private List<ShortestPathTreeElement> children;

	private int distanceToRoot;
	private int shortestPathCount;
	private int nodeIndex;
	private double accumulativSum;

	public ShortestPathTreeElement(int nodeIndex) {
		super();
		this.parents = new ArrayList<>();
		this.children = new ArrayList<>();
		this.distanceToRoot = -1;
		this.shortestPathCount = -1;
		this.nodeIndex = nodeIndex;
		this.setAccumulativSum(0);
	}

	public int getNodeIndex() {
		return nodeIndex;
	}

	public void addChild(ShortestPathTreeElement child) {
		parents.add(child);
	}

	public void deleteAllParents() {
		parents.clear();
	}

	public void addParent(ShortestPathTreeElement parent) {
		parents.add(parent);
	}

	public void removeChild(ShortestPathTreeElement child) {
		parents.remove(child);
	}

	public void removeParent(ShortestPathTreeElement parent) {
		parents.remove(parent);
	}

	public boolean containsChild(ShortestPathTreeElement child) {
		return parents.contains(child);
	}

	public void setShortestPathCount(int shortestPathCount) {
		this.shortestPathCount = shortestPathCount;
	}

	public void setDistanceToRoot(int distanceToRoot) {
		this.distanceToRoot = distanceToRoot;
	}

	public boolean containsParent(ShortestPathTreeElement parent) {
		return parents.contains(parent);
	}

	public int getDistanceToRoot() {
		return distanceToRoot;
	}

	public int getShortestPathCount() {
		return shortestPathCount;
	}

	public List<ShortestPathTreeElement> getParents() {
		return parents;
	}

	public List<ShortestPathTreeElement> getChildren() {
		return children;
	}

	public double getAccumulativSum() {
		return accumulativSum;
	}

	public void setAccumulativSum(double accumulativSum) {
		this.accumulativSum = accumulativSum;
	}

}
