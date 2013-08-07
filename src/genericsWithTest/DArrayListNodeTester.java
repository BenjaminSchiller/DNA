package genericsWithTest;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class DArrayListNodeTester extends AbstractTester<DArrayList<Node>> {

	@Before
	public void setUp() {
		this.element = new Node();
		this.list = new DArrayList<>();
	}

}
