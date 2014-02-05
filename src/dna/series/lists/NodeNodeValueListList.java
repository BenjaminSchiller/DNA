package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.NodeNodeValueList;

/**
 * A NodeNodeValueListList lists NodeNodeValueList objects.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class NodeNodeValueListList extends List<NodeNodeValueList> {

	public NodeNodeValueListList() {
		super();
	}

	public NodeNodeValueListList(int size) {
		super(size);
	}

	public void write(String dir) throws IOException {
		for (NodeNodeValueList n : this.getList()) {
			n.write(dir, Files.getNodeNodeValueListFilename(n.getName()));
		}
	}

	public static NodeNodeValueListList read(String dir, boolean readValues)
			throws IOException {
		String[] NodeNodeValueLists = Files.getNodeNodeValueLists(dir);
		NodeNodeValueListList list = new NodeNodeValueListList(
				NodeNodeValueLists.length);
		for (String nodeNodeValueList : NodeNodeValueLists) {
			list.add(NodeNodeValueList.read(dir, nodeNodeValueList,
					Files.getNodeValueListName(nodeNodeValueList), readValues));
		}
		return list;
	}
}
