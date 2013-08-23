package Graph;

public interface IElement extends Comparable<Element> {
	public String getStringRepresentation();
	public boolean deepEquals(IElement other);
}
