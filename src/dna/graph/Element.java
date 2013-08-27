package dna.graph;

public abstract class Element implements IElement {
	@Override
	public boolean deepEquals(IElement other) {
		return this.getStringRepresentation().equals(other.getStringRepresentation());
	}

}
