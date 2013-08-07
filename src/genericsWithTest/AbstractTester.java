package genericsWithTest;
import static org.junit.Assert.*;

import org.junit.Test;

public abstract class AbstractTester<T extends DataStructure> {

	protected T list;
	protected Element element;

	@Test
	public void checkAdd() {
		Element dummy = element.getDummy();
		list.add(dummy); 
		assertTrue(list.contains(dummy));
		assertEquals(1,list.size());
	}

}
