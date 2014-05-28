package dna.plot.data;

/**
 * @author benni
 * 
 */
public class FunctionData extends PlotData {

	public FunctionData(String functionName, String formular, PlotStyle style,
			String title) {
		super(functionName, formular, style, title);
	}

	@Override
	public boolean isStyleValid() {
		return !this.style.equals(PlotStyle.candlesticks)
				&& !this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
		StringBuffer buff = new StringBuffer();
		buff.append(this.data + " with " + this.style);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(" title \"" + this.getLine() + "\"");
		return buff.toString();
	}

	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY);
	}

	public String getLine() {
		return super.data + "=" + super.domain;
	}

}
