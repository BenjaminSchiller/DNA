package Graph;

/**
 * Interface for weighted nodes and edges
 * 
 * @author Nico
 * 
 * @param <T>
 *            data type to store weight information
 */
public interface IWeighted<T> {

	public void setWeight(T newWeight);

	public T getWeight();
}
