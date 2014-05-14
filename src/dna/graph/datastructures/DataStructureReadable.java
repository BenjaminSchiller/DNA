package dna.graph.datastructures;

import java.util.Iterator;

import dna.graph.IElement;
import dna.util.Log;

/**
 * Base class for storing IElements in a format that supports reading (eg.
 * through get, but also remove) individual elements later
 * 
 * @author Nico
 * 
 */
public abstract class DataStructureReadable extends DataStructure implements
		IReadable {

	public DataStructureReadable(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public boolean dataEquals(IDataStructure that) {
		if (that instanceof DataStructureReadable) {
			return dataEquals((DataStructureReadable) that);
		}
		return false;
	}

	public boolean dataEquals(IReadable that) {
		Log.debug("Data equality check for a list of type " + dataType);

		if (this.size() != that.size())
			return false;
		int checkedAndFound = 0;

		if (this.size() == 0)
			return true;

		Iterator<IElement> thisIterator = this.iterator();
		Iterator<IElement> thatIterator = that.iterator();

		while (thisIterator.hasNext()) {
			IElement thisElement = thisIterator.next();
			thatIterator = that.iterator();
			while (thatIterator.hasNext()) {
				IElement thatElement = thatIterator.next();
				if (thisElement == null || thatElement == null)
					continue;
				if (thisElement.equals(thatElement)
						&& thisElement.deepEquals(thatElement)) {
					checkedAndFound++;
					if (thisIterator.hasNext()) {
						thisElement = thisIterator.next();
						thatIterator = that.iterator();
					} else
						break;

				}
			}
		}

		return (checkedAndFound == this.size());
	}

	public IDataStructure switchTo(IDataStructure newDatastructure) {
		newDatastructure.reinitializeWithSize(this.size());
		Iterator<IElement> elIt = this.iterator();
		while ( elIt.hasNext() )
			newDatastructure.add(elIt.next());
		this.prepareForGC();
		return newDatastructure;
	}
}
