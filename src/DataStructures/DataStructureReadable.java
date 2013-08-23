package DataStructures;

import Graph.IElement;

public abstract class DataStructureReadable extends DataStructure implements IReadable {
	
	public boolean dataEquals(IDataStructure that) {
		if (that instanceof DataStructureReadable) {
			return dataEquals((DataStructureReadable) that);
		}
		return false;
	}
	
	public boolean dataEquals(IReadable that) {
		if ( this.size() != that.size() ) return false;
		int checkedAndFound = 0;
		
		if ( this.size() == 0 ) return true;
			
		for ( IElement thisElement: this.getElements() ) {
			for ( IElement thatElement: that.getElements() ) {
				if ( thisElement == null || thatElement == null ) continue;
				if ( thisElement.equals(thatElement) && thisElement.deepEquals(thatElement)) checkedAndFound++;
			}
		}
		
		return checkedAndFound == this.size();
	}

}
