package dna.graph.nodes.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.zalando.EventColumn;
import dna.graph.nodes.DirectedNode;

/**
 * {@link DirectedNode} for Zalando graphs.
 */
public class DirectedZalandoNode extends DirectedNode implements IZalandoNode {

	private EventColumn[] type;

	public DirectedZalandoNode(int i, GraphDataStructure gds, EventColumn[] type) {
		super(i, gds);
		this.type = type;
	}

	public DirectedZalandoNode(String str, GraphDataStructure gds) {
		super(str.split(" ")[0], gds);
		this.type = ZalandoNode.eventColumnsStringToArray(str.split(" ")[1]);
	}

	@Override
	public String toString() {
		return super.toString() + " "
				+ ZalandoNode.eventColumnsArrayToString(this.getType());
	}

	@Override
	public EventColumn[] getType() {
		return this.type;
	}

	@Override
	public String asString() {
		return Integer.toString(this.index) + " "
				+ ZalandoNode.eventColumnsArrayToString(this.getType());
	}
}
