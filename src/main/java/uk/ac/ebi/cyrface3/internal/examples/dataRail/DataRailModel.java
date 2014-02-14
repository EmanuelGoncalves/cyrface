package uk.ac.ebi.cyrface3.internal.examples.dataRail;


import java.io.File;

public class DataRailModel {

//	private RFunctionsModel rCommands; // TODO
	
	// Attributes
	private String midasFile = null;
	private File cnoListPlot = null;
	private File cnoListNormalisePlot = null;
	private String pknModelFile = null;
	
	private File optimisedCnoLisPlot = null;
	
	private File normalizedMidasFile = null;
	private Double ec50;
	private Double detection;
	private Double saturation; 
	
	private File optimizedMidasFile = null;
	
	public DataRailModel() throws Exception{
//		this.rCommands = new RFunctionsModel(this);
		this.ec50 = DataRailAttributes.ec50_default;
		this.detection = DataRailAttributes.detection_default;
		this.saturation = DataRailAttributes.saturation_default;
	}
	
//		Getter and Setter
//	public RFunctionsModel getRCommand() {
//		return rCommands;
//	}
//	
	public String getMidasFilePath() {
		return midasFile;
	}

	public void setMidasFilePath(String midasFile) {
		this.midasFile = midasFile;
	}

	public File getCnoListPlot() {
		return cnoListPlot;
	}

	public void setCnoListPlot(File cnoListPlot) {
		this.cnoListPlot = cnoListPlot;
	}

	public String getPknModelFile() {
		return pknModelFile;
	}

	public void setPknModelFile(String pknModelFile) {
		this.pknModelFile = pknModelFile;
	}

	public File getCnoListNormalisePlot() {
		return cnoListNormalisePlot;
	}

	public void setCnoListNormalisePlot(File cnoListNormalisePlot) {
		this.cnoListNormalisePlot = cnoListNormalisePlot;
	}

	public File getOptimisedCnoLisPlot() {
		return optimisedCnoLisPlot;
	}

	public void setCnoListOptimisedPlot(File optimisedCnoLisPlot) {
		this.optimisedCnoLisPlot = optimisedCnoLisPlot;
	}

	public File getNormalizedMidasFile() {
		return normalizedMidasFile;
	}

	public void setNormalizedMidasFile(File normalizedMidasFile) {
		this.normalizedMidasFile = normalizedMidasFile;
	}

	public File getOptimizedMidasFile() {
		return optimizedMidasFile;
	}

	public void setOptimizedMidasFile(File optimizedMidasFile) {
		this.optimizedMidasFile = optimizedMidasFile;
	}

	public Double getEc50() {
		return ec50;
	}

	public void setEc50(Double ec50) {
		this.ec50 = ec50;
	}

	public Double getDetection() {
		return detection;
	}

	public void setDetection(Double detection) {
		this.detection = detection;
	}

	public Double getSaturation() {
		return saturation;
	}

	public void setSaturation(Double saturation) {
		this.saturation = saturation;
	}
}