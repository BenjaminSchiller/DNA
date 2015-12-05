package dna.graph;

/**
 * The Interface IGDBGraph.
 * 
 * @author Matthias
 */
public interface IGDBGraph<G> {
	
	/**
	 * Commit.
	 */
	public abstract void commit();
	
	/**
	 * Gets the graph database instance.
	 *
	 * @return the graph database instance
	 */
	public abstract G getGraphDatabaseInstance();
	
	/**
	 * Gets the graph database type.
	 *
	 * @return the graph database type
	 */
	public abstract DNAGraphFactory.DNAGraphType getGraphDatabaseType();
	
	/**
	 * Sets the graph database instance.
	 *
	 * @param graph the new graph database instance
	 */
	public abstract void setGraphDatabaseInstance(G graph);
	
	/**
	 * Sets the graph database type.
	 *
	 * @param gdb the new graph database type
	 */
	public abstract void setGraphDatabaseType(DNAGraphFactory.DNAGraphType gdb);
}
