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
		return this.getEntry(lt, lw, offsetX, offsetY, this.style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType, PlotStyle style) {
		return this.getEntry(lt, lw, offsetX, offsetY, style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			PlotStyle style) {
		// plot style
		PlotStyle styleTemp;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		buff.append(this.data + " with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(" title \"" + this.getLine() + "\"");
		return buff.toString();
	}

	public String getLine() {
		return super.data + "=" + super.domain;
	}

}
