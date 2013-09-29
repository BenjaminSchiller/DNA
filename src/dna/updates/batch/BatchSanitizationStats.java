package dna.updates.batch;

public class BatchSanitizationStats {

	private int deletedNodeAdditions;

	private int deletedNodeRemovals;

	private int deletedNodeWeights;

	private int deletedEdgeAdditions;

	private int deletedEdgeRemovals;

	private int deletedEdgeWeights;

	public BatchSanitizationStats(int deletedNodeAdditions,
			int deletedNodeRemovals, int deletedNodeWeights,
			int deletedEdgeAdditions, int deletedEdgeRemovals,
			int deletedEdgeWeights) {
		this.deletedNodeAdditions = deletedNodeAdditions;
		this.deletedNodeRemovals = deletedNodeRemovals;
		this.deletedNodeWeights = deletedNodeWeights;
		this.deletedEdgeAdditions = deletedEdgeAdditions;
		this.deletedEdgeRemovals = deletedEdgeRemovals;
		this.deletedEdgeWeights = deletedEdgeWeights;
	}

	public BatchSanitizationStats() {

	}

	public String toString() {
		return this.getTotal() + " updates deleted ("
				+ this.deletedNodeAdditions + "," + this.deletedNodeRemovals
				+ "," + this.deletedNodeWeights + "/"
				+ this.deletedEdgeAdditions + "," + this.deletedEdgeRemovals
				+ "," + this.deletedNodeWeights + ")";
	}

	public int getTotal() {
		return this.deletedNodeAdditions + this.deletedNodeRemovals
				+ this.deletedNodeWeights + this.deletedEdgeAdditions
				+ this.deletedEdgeRemovals + this.deletedEdgeWeights;
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

	public int getDeletedNodeWeights() {
		return deletedNodeWeights;
	}

	public void setDeletedNodeWeights(int deletedNodeWeights) {
		this.deletedNodeWeights = deletedNodeWeights;
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

	public int getDeletedEdgeWeights() {
		return deletedEdgeWeights;
	}

	public void setDeletedEdgeWeights(int deletedEdgeWeights) {
		this.deletedEdgeWeights = deletedEdgeWeights;
	}
}
