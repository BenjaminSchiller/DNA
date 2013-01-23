package dynamicGraphs.test;

import dynamicGraphs.util.ArrayUtils;

public class TestUtil {
	public static void main(String[] args) {
		int[] values = new int[] { 0, 1, 2, 4 };
		System.out.println(ArrayUtils.sum(values));
		System.out.println(ArrayUtils.toString(values));
		values = ArrayUtils.incr(values, 0);
		System.out.println(ArrayUtils.toString(values));
		values = ArrayUtils.incr(values, 2);
		System.out.println(ArrayUtils.toString(values));
		values = ArrayUtils.incr(values, 4);
		System.out.println(ArrayUtils.toString(values));
		values = ArrayUtils.incr(values, 0);
		System.out.println(ArrayUtils.toString(values));
	}
}
