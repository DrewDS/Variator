package main;
import java.util.List;

public class FullBasis extends DataMap{
	

	
	public FullBasis(int displayRes) {
		super(displayRes);
	}
	public FullBasis(int displayRes, String presetName) {
		super(displayRes, "basis", presetName);
	}
	
	public void addDrumBasis(String name, double[] drumData) {
		addDrumData(name, drumData);
	}
	
	/**
	 * Creates a basis (for use in a Variator) that represents the 
	 * frequency distribution for one key from a Histogram
	 * 
	 * @param histo
	 * @param key
	 * @return
	 * 
	 */
	public static double[] getBasisFromHistogram(Histogram histo, long key) {
		
		double[] basis = new double[histo.getResolution()];
		double[] rawFrequencies = new double[histo.getResolution()];
		
		
		List<List<Note>> beats = histo.getEventData().get(key);
		
		double maxFreq = 0;
		
		for (int i = 0; i < beats.size(); i++) {
			
			List<Note> pairs = beats.get(i);
			double freq = pairs.size();
			if (freq > maxFreq) {
				maxFreq = freq;
			}
			rawFrequencies[i] = pairs.size();
			
		}
		
		for (int i = 0; i < rawFrequencies.length; i++) {
			
			// The multiplication by *99 in BASIS will cause HOME to be
			// prioritized over BASIS
			basis[i] = (rawFrequencies[i] / maxFreq) * 0.99;
			
		}
		
		return basis;
	}	


	public static void main(String[] args) {
		
		FullBasis fb1 = new FullBasis(8);
		double[] kick = {.9,.1,.8,.7};
		double[] snare = {.1,.4,.1,.6};
		
		
		DataMap fh = new DataMap(4);
		double[] kickH = {1,0,0,1,0,0,0,0};
		double[] snareH = {0,0,1,0,0,0,1,0};
	
		fb1.addDrumData("kick", kick);
		fb1.addDrumData("snare", snare);
		
		fh.addDrumData("kick", kickH);
		fh.addDrumData("snare", snareH);
		
		fb1.printData();
		
		fh.printData();
		fh.setDisplayRes(16);
		fh.printData();
		fb1.setDisplayRes(4);
		fb1.printData();
		
		
		
	}
	
	/*
	private double[] kick;
	private double[] snare;
	private double[] clap;
	private double[] hhOpen;
	private double[] hhClosed;
	private double[] tom1;
	private double[] tom2;
	private double[] tomFloor;
	private double[] crash;
	private double[] ride;
	private double[] other1;
	private double[] other2;
	private double[] other3;
	private double[] other4;
	private double[] other5;
	private double[] other6;
	*/

}
