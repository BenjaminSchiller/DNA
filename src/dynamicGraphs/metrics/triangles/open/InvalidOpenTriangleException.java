package dynamicGraphs.metrics.triangles.open;

public class InvalidOpenTriangleException extends Exception {

	private static final long serialVersionUID = 8131947463772289828L;
	
	public InvalidOpenTriangleException(OpenTriangle t){
		super(t.toString() + " is not a valid open triangle");
	}

}
