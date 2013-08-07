package genericsWithTest;
import java.util.ArrayList;

public class DArrayList<E> extends DataStructure<E> {
	private ArrayList<E> list;

	public DArrayList() {
		this.list = new ArrayList<>();
	}

	@Override
	public void add(E element) {
		list.add(element);
	}

	@Override
	public boolean contains(E element) {
		return list.contains(element);
	}

	@Override
	public int size() {
		return list.size();
	}
	
	

}
