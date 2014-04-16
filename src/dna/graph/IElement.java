package dna.graph;

/**
 * Interface for all types of nodes and edges we might create
 * 
 * @author Nico
 * 
 */
public interface IElement extends Comparable<Element> {
	/**
	 * String representation for an element, through which it can be recreated
	 * on reading in a dumped graph
	 * 
	 * @return
	 */
	public String asString();

	/**
	 * Checks whether two elements are completely equal, mostly defined through
	 * equality of their string representations. This goes deeper than the
	 * common equality which does not check for equal additional fields like
	 * weights
	 * 
	 * @param other
	 * @return
	 */
	public boolean deepEquals(IElement other);
}
