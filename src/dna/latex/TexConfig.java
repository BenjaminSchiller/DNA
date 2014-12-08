package dna.latex;

import dna.latex.TexTable.TableFlag;
import dna.plot.PlottingConfig.PlotFlag;

/** A TexConfig object is used to configure the tex-output process. **/
public class TexConfig {

	private String dstDir;
	private String srcDir;
	private String plotDir;

	private PlotFlag[] plotFlags;
	private TableFlag[] tableFlags;

	public TexConfig(String dstDir, String srcDir, String plotDir,
			PlotFlag[] plotFlags, TableFlag... tableFlags) {
		this.dstDir = dstDir;
		this.srcDir = srcDir;
		this.plotDir = plotDir;
		this.plotFlags = plotFlags;
		this.tableFlags = tableFlags;
	}

	public String getDstDir() {
		return dstDir;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public String getPlotDir() {
		return plotDir;
	}

	public PlotFlag[] getPlotFlags() {
		return plotFlags;
	}

	public TableFlag[] getTableFlags() {
		return tableFlags;
	}
}
