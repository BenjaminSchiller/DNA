package dna.updates;

public class BatchSanitizationStats {

	private int deletedNodeAdditions;

	private int deletedNodeRemovals;

	private int deletedNodeWeightUpdates;

	private int deletedEdgeAdditions;

	private int deletedEdgeRemovals;

	private int deletedEdgeWeightUpdates;

	public String toString() {
		return this.getTotal() + " updates removed ("
				+ this.deletedNodeAdditions + "," + this.deletedNodeRemovals
				+ "," + this.deletedNodeWeightUpdates + "/"
				+ this.deletedEdgeAdditions + "," + this.deletedEdgeRemovals
				+ "," + this.deletedNodeWeightUpdates + ")";
	}

	public int getTotal() {
		return this.deletedNodeAdditions + this.deletedNodeRemovals
				+ this.deletedNodeWeightUpdates + this.deletedEdgeAdditions
				+ this.deletedEdgeRemovals + this.deletedEdgeWeightUpdates;
	}

	public int getDeletedNodeAdditions() {
		return deletedNodeAdditions;
	}

	public void setDeletedNodeAdditions(int deletedNodeAdditions) {
		this.deletedNodeAdditions = deletedNodeAdditions;
	}

	public int getDeletedNodeRemovals() {
		return deletedNodeRemovals;
	}

	public void setDeletedNodeRemovals(int deletedNodeRemovals) {
		this.deletedNodeRemovals = deletedNodeRemovals;
	}

	public int getDeletedNodeWeightUpdates() {
		return deletedNodeWeightUpdates;
	}

	public void setDeletedNodeWeightUpdates(int deletedNodeWeightUpdates) {
		this.deletedNodeWeightUpdates = deletedNodeWeightUpdates;
	}

	public int getDeletedEdgeAdditions() {
		return deletedEdgeAdditions;
	}

	public void setDeletedEdgeAdditions(int deletedEdgeAdditions) {
		this.deletedEdgeAdditions = deletedEdgeAdditions;
	}

	public int getDeletedEdgeRemovals() {
		return deletedEdgeRemovals;
	}

	public void setDeletedEdgeRemovals(int deletedEdgeRemovals) {
		this.deletedEdgeRemovals = deletedEdgeRemovals;
	}

	public int getDeletedEdgeWeightUpdates() {
		return deletedEdgeWeightUpdates;
	}

	public void setDeletedEdgeWeightUpdates(int deletedEdgeWeightUpdates) {
		this.deletedEdgeWeightUpdates = deletedEdgeWeightUpdates;
	}
}
