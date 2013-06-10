package dna.util;

public class Settings {
	//public static final String gnuplotPath = "/usr/bin/gnuplot";	// unix
	public static final String gnuplotPath = "f:\\!proxx\\gnuplot\\bin\\gnuplot.exe";	// rwilmes windows
	
	// sorted by degree of freedom: 1,2,3,...,29,30,40,50,60,70,80,90,100,200,300,400,500,infinity
	private static final double[] Q95 = {12.71, 4.303, 3.182, 2.776, 2.571, 2.447, 2.365, 2.306, 2.262, 2.228, 2.201, 2.179, 2.160, 2.145, 2.131, 2.120, 2.110, 2.101, 2.093, 2.086, 2.080, 2.074, 2.069, 2.064, 2.060, 2.056, 2.052, 2.048, 2.045, 2.042, 2.021, 2.009, 2.000, 1.994, 1.990, 1.987, 1.984, 1.972, 1.968, 1.966, 1.965, 1.960}; 
	 
	
	/**
	 * Returns the quantile of the Student's t-distribution wich a 0,95 confidence niveau.
	 * 
	 * @param values double array the confidence is calculated from
	 * @return confidence of the given double array
	 */
	public static double getStudentT(double niveau, int degree) {
		// always returns quantiles for 0,95 confidence niveau. Others not implented yet.
		return getStudentT95(degree);
	}
	
	private static double getStudentT95(int degree){
		if(degree < 31)
			return Q95[degree-1];
		if(degree < 36)
			return Q95[29];
		if(degree < 46)
			return Q95[30];
		if(degree < 56)
			return Q95[31];
		if(degree < 66)
			return Q95[32];
		if(degree < 76)
			return Q95[33];
		if(degree < 86)
			return Q95[34];
		if(degree < 96)
			return Q95[35];
		if(degree < 150)
			return Q95[36];
		if(degree < 250)
			return Q95[37];
		if(degree < 350)
			return Q95[38];
		if(degree < 450)
			return Q95[39];
		if(degree < 550)
			return Q95[40];
		return Q95[41];
	}
}
