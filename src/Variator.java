
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Variator {

	
	private double[] basis;
	private double[] home;
	private double[] sum;
	private double[] velocities;

	private int resolution;

	private String tieBreakChoice = "LEFT";
	
	
	private static double[] DEFAULT_KICK_BASIS = {0.99,0,.5,0,.99,0,.5,0};
	private static double[] DEFAULT_KICK_HOME = {1,0,0,1,0,0,0,0};
	
	private static double[] DEFAULT_SNARE_BASIS = {0,0,.99,0,.75,0,.99,.5};
	private static double[] DEFAULT_SNARE_HOME = {0,0,1,0,0,0,1,0};
	
	private static double[] DEFAULT_HAT_BASIS = {.1,.8,.7,.8,.2,.8,.5,.6};
	private static double[] DEFAULT_HAT_HOME = {0,1,0,1,0,1,0,1};
	
	@SuppressWarnings("unused")
	private int homeDensity;
		
	static int EIGHTH_RESOLUTION = 8;
	static int DEFAULT_RESOLUTION = 8;
	static int MAX_RESOLUTION = 64;
	

	/** 
	 * 	Constructor for Variator Object
	 * @param home
	 * @param basis
	 * @param resolution 
	 */
	public Variator(double[] home, double[] basis, int resolution) {

		this.resolution = resolution;
		this.basis = basis;
		this.home = home;
		this.sum = add(this.basis, this.home);
		homeDensity = getDensity(home);
		
	}
	
	/** 
	 * 	Constructor for Variator Object of Default Resolution
	 * @param home
	 * @param basis
	 * @param resolution 
	 */
	public Variator(double[] home, double[] basis) {
		this.resolution = DEFAULT_RESOLUTION;
		this.basis = basis;
		this.home = home;
		this.sum = add(this.basis, this.home);
		homeDensity = getDensity(home);
		
	}
	
	
	////======= STATIC FACTORY METHODS FOR DEFAULT SINGLE DRUM VARIATORS ==========
	
	static Variator getDefaultKickVariator() { 
		
		return new Variator(DEFAULT_KICK_HOME, DEFAULT_KICK_BASIS, DEFAULT_RESOLUTION);
		
	}
	
	static Variator getDefaultSnareVariator() { 
		
		return new Variator(DEFAULT_SNARE_HOME, DEFAULT_SNARE_BASIS, DEFAULT_RESOLUTION);
		
	}
	
	static Variator getDefaultHatVariator() { 
		
		return new Variator(DEFAULT_HAT_HOME, DEFAULT_HAT_BASIS, DEFAULT_RESOLUTION);
		
	}
	
	// =============================================================================
	
	
	
	
	public static void main(String[] args) {
		
		//Variator kickVariator = getDefaultKickVariator();
		//Variator snareVariator = getDefaultSnareVariator();
		//Variator hatVariator = getDefaultHatVariator();
		//printArray(kickVariator.getBasis());
		//printArray(kickVariator.getHome());

		Histogram histo = new Histogram(DEFAULT_RESOLUTION);
		histo.addFile("Test Loop 1.mid");
		Tools.printHistogram(histo);
		histo.addFile("Test Loop 2.mid");
		Tools.printHistogram(histo);
		histo.addFile("Test Loop 3.mid");
		Tools.printHistogram(histo);
		
		int kickValue = KeyValues.C_ONE.getValue();
		FullHome fh = new FullHome(DEFAULT_RESOLUTION);
		
		Variator kickVariator = new Variator(fh.makeDrumHomeFromMidiFile("Test Loop 4.mid", kickValue) ,
				FullBasis.getBasisFromHistogram(histo, kickValue));
		
		System.out.print("Kick Home: " + kickVariator.getHome().length);
		Tools.printArray(kickVariator.getHome());
		System.out.print("Kick Basis: " + kickVariator.getBasis().length);
		Tools.printArray(kickVariator.getBasis());
		int[] kickVariation = kickVariator.makeVariation(23);
		//int[] snareVariation = snareVariator.makeVariation(3);
		//
		//int[] hatVariation = hatVariator.makeVariation(1);
		
		Tools.printArray(kickVariation);
		//Tools.printArray(snareVariation);
		//Tools.printArray(hatVariation);

	}
	
	private void updateVariator() {
		
		sum = add(basis, home);
		homeDensity = getDensity(home);
		
	}
	
	public double[] getBasis() {
		return basis;
	}


	public void setBasis(double[] basis) {
		this.basis = basis;
		updateVariator();
	}


	public double[] getHome() {
		return home;
	}


	public void setHome(double[] home) {
		this.home = home;
		updateVariator();
	}
	
	public void setHome(String abletonClip, int key) {
		home = Tools.getHomeFromAbletonClip(abletonClip, key, resolution);
		updateVariator();
	}

	public double[] getSum() {
		return this.sum;
	}
	
	private double[] add(double[] basis, double[] home) {
		
		double[] tempSum = new double[resolution];
		for (int i = 0; i < resolution; i++) {
			
			tempSum[i] = basis[i] + home[i];
		}
		
		VariatorObject.post("Sum at add() in Variator.java: " + Tools.printArray(tempSum));
		
		return tempSum;
	}
	
	private int[] orderIndexes(double[] sum) {
		
		double[] rawTrack = sum.clone();
		
		//Accommodate for resolution by "zooming out" track - i.e. less detail
		double[] track = new double[resolution];
		int resScale = rawTrack.length / resolution;
		System.out.println(resScale);
		for (int i = 0; i < rawTrack.length; i++) {
			if  (i % resScale == 0) {
				track[i/resScale] = rawTrack[i];
			}
		}
		
		Vector<Integer> indexes = new Vector<Integer>();
		
		while (indexes.size() < sum.length) {
			
			List<Integer> maxIndexes = findMaxes(track);
			
			if (maxIndexes.size() > 1) {
				int index = tieBreak(maxIndexes);
				indexes.add(index);
				track[index] = -1;
	
			} else {
				int index = maxIndexes.get(0);
				indexes.add(index);
				track[index] = -1;
			}
		}
		
		int[] ordered = new int[resolution];
		
		int count = 0;
		for (Integer i : indexes) {
			ordered[count] = i;
			count++;
		}
		
		return ordered;
		
	}
	
	// Returns an ArrayList that contains the indexes of the maximum values of track
	// If there is only one maximum (i.e. there are no ties), the size of this ArrayList is 1
	private List<Integer> findMaxes(double[] track) {
		
		double maxValue = -1;
		List<Integer> maxes = new ArrayList<Integer>();
		
		for (int i = 0; i < track.length; i++) {
			if (track[i] == maxValue) {
				maxes.add(i);
			}
			if (track[i] > maxValue) {
				maxValue = track[i];
				maxes = new ArrayList<Integer>();
				maxes.add(i);
			}
		}
		
		return maxes;
	}
	
	// tieBreak method that alternates choosing left and rightmost index of a tie
	private int tieBreak(List<Integer> indexes) {
		
		if (tieBreakChoice == "LEFT") {
			tieBreakChoice = "RIGHT";
			return indexes.get(0);
		} else {
			tieBreakChoice = "LEFT";
			return indexes.get(indexes.size() - 1);
		}			
	}
	
	
	public int[] makeVariation(int density) {
		
		int[] ordered = orderIndexes(sum);
				
		int[] variation = new int[resolution];
		
		int[] notes = Arrays.copyOfRange(ordered, 0, density);
		
		for (int note : notes) {
			variation[note] = 1;
		}
		
		return variation;
	}
	
	/** INTRODUCE VELOCITY TO VARIATIONS
	public int[] makeVariation(int density, double velocityFactor) {
		
		int[] flatVariation = makeVariation(density);
		
		
		
	}*/
	
	public int getDensity(double[] variation) {
		int count = 0;
		for (double i : variation) {
			if (i == 1) {
				count++;
			}
		}
		return count;
	}
	
	public int getResolution() {
		return resolution;
	}
	
	

}
