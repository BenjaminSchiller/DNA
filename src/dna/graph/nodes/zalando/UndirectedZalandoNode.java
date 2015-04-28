package dna.graph.nodes.zalando;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;

/**
 * {@link DirectedNode} for Zalando graphs.
 */
public class UndirectedZalandoNode extends UndirectedNode implements
		IZalandoNode {

	private EventColumn[] type;

	public UndirectedZalandoNode(int index, GraphDataStructure gds,
			EventColumn[] type) {
		super(index, gds);
		this.type = type;
		
	}

	public UndirectedZalandoNode(String str, GraphDataStructure gds) {
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
