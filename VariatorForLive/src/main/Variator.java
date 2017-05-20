package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.lang.Math;

/**
 * Low Level class to create single-drum, single-bar variations from associated home,
 * basis, and velocity DataMaps.
 * 
 * Primary algorithm is the addition of the constituent DataMaps, followed by the ordering
 * of indeces.  Density of 'x' in a variation will return populate the first x ordered
 * indeces of output variation with velocities determined by the velocity map and weighting
 * factors.
 * 
 * @author drewdyer-symonds
 *
 */
public class Variator {

	
	private double[] basis;
	private double[] home;  // is no longer 0 or 1.  Will now contain velocites 0.0 - 1.27
	private double[] sum;
	private double[] velocities;
	private int[] orderedIndexes;
	private int homeDensity;

	private int resolution;

	public enum TieBreakAlgo {LEFT_RIGHT, PRIORITIZE_VELOCITY};
	private TieBreakAlgo tieBreakAlgo = TieBreakAlgo.PRIORITIZE_VELOCITY;
	private String tieBreakChoice = "LEFT";

		
	static int EIGHTH_RESOLUTION = 8;
	static int DEFAULT_RESOLUTION = 8;
	static int MAX_RESOLUTION = 64;
	

	/** 
	 * 	Constructor for single-drum, single-bar Variator 
	 * 
	 * @param home
	 * @param basis
	 * @param resolution 
	 */
	public Variator(double[] home, double[] basis, int resolution) {

		this.resolution = resolution;
		this.basis = basis;
		this.home = home;
		this.homeDensity = countHomeDensity();
		sum = add(this.basis, this.home);
		velocities = Tools.createConstantArray(resolution, Tools.DEFAULT_INTERNAL_VEL);
		
	
	}
	
	private void updateVariator() {
		
		sum = add(basis, home);
		orderedIndexes = orderIndexes(sum);
		homeDensity = countHomeDensity();
		
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


	public double[] getSum() {
		return this.sum;
	}
	
	public void setVelocities(double[] velocities) {
		this.velocities = velocities;
		updateVariator();
	}
	
	// Adds basis and home, but substitutes the velocity of home for "1"
	// This grants priority to the basis regardless of home velocity
	// home velocity is then used for dynamics and tieBreak
	private double[] add(double[] basis, double[] home) {
		
		double[] tempSum = new double[resolution];
		
		for (int i = 0; i < resolution; i++) {
			
			double hitValue = 0;
			
			if (home[i] != 0) {
				hitValue = 1;
			}
			tempSum[i] = basis[i] + hitValue;
		}
		
		//VariatorObject.post("Sum at add() in Variator.java: " + Tools.printArray(tempSum));
		
		return tempSum;
	}
	
	
	private int[] orderIndexes(double[] sum) {
		
		double[] rawTrack = sum.clone();
		
		//Accommodate for resolution by "zooming out" track - i.e. less detail
		double[] track = new double[resolution];
		System.out.println("sum before ordering indeces: " + Tools.printArray(rawTrack));
		int resScale = rawTrack.length / resolution;
		for (int i = 0; i < rawTrack.length; i++) {
			if  (i % resScale == 0) {
				track[i/resScale] = rawTrack[i];
			}
		}
		
		Vector<Integer> indexes = new Vector<Integer>();
		
		while (indexes.size() < sum.length) {
			
			List<Integer> maxIndexes = findMaxes(track);
			
			if (maxIndexes.size() > 1) {
				int index = tieBreak(maxIndexes, tieBreakAlgo);
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
	private int tieBreak(List<Integer> indexes, TieBreakAlgo algo) {
		
		switch(algo) {
			
			case LEFT_RIGHT: return tieBreakLR(indexes);
			case PRIORITIZE_VELOCITY: return tieBreakVel(indexes);
			
			default:System.out.println("Default tieBreak method reached");
					return tieBreakLR(indexes);	
					
		}
		
			
	}
	
	private int tieBreakVel(List<Integer> indexes) {
		
		double maxVel = 0;
		int maxIndex = indexes.get(0);
		for (int i = 0; i < indexes.size(); i++) {
			int currentIndex = indexes.get(i);
			if (velocities[currentIndex] > maxVel) {
				maxIndex = currentIndex;
				maxVel = velocities[currentIndex];
			} 
			// If velocities from Velocity Map are the same for two given indexes
			//  return the result from the Left/Right Algorithm as the default
			else if (velocities[currentIndex] == maxVel) {
				return tieBreakLR(indexes);
			}
		}
		return maxIndex;
		
	}
	
	private int tieBreakLR(List<Integer> indexes) {
		
		if (tieBreakChoice == "LEFT") {
			tieBreakChoice = "RIGHT";
			return indexes.get(0);
		} else {
			tieBreakChoice = "LEFT";
			return indexes.get(indexes.size() - 1);
		}		
	}
	
	
	public double[] makeVariation(int density) {
		
		//int[] ordered = orderIndexes(sum); MOVED MAKING ORDERED INDEXES TO THE updateVariator() method
		int[] ordered = orderedIndexes;
		
		System.out.println("Ordered Indeces: " + Tools.printArray(ordered));
				
		double[] variation = new double[resolution];
		
		int[] notes = Arrays.copyOfRange(ordered, 0, density);
		
		for (int note : notes) {
			variation[note] = 1;
		}
		
		return variation;
	}
	
	/**
	 * Makes a variation array of useful velocities.
	 * 
	 * @param density - the number of "hits" to occur in the bar
	 * @param velocityFactor - the weighting of each hit toward or away from the homeVel
	 * (A velocityFactor of 0 will return the homeVel for each hit, and 1 will return the 
	 * correlated velocity from this class's "velocities" field.
	 * @return
	 */
	public double[] makeVariation(int density, double velocityFactor) {
		
		double[] flatVariation = makeVariation(density);

		double[] variation = new double[resolution];
		
		for (int i = 0; i < resolution; i++) {
			
			if (flatVariation[i] == 1) {
				
				double homeVel = home[i];
				//VariatorObject.post("Velocity of Home note at position [" + i + "]: " + homeVel);
				// Interpolate Velocity for notes not contained in HOME
				// Uses AVERAGE velocity of home array as "ghost" velocity
				double avgVel = meanHomeVelocity();
				if (home[i] == 0) {
					homeVel = avgVel; 
				}
				double newVel = ((velocities[i] - homeVel) * velocityFactor) + homeVel;
				//VariatorObject.post("Velocity of Note at position [" + i + "]: " + newVel);
				variation[i] = newVel;
		
			}
		}
		return variation;		
	}
	
	public double meanHomeVelocity() {
		double velSum = 0;
		for (int i = 0; i < resolution; i++) {
			if (home[i] > 0) {
				velSum += home[i];
			}
		}
		// To maintain a reasonable average home velocity
		if (getHomeDensity() == 0 || velSum == 0) {
			return Tools.DEFAULT_INTERNAL_VEL;
		}
		return velSum/getHomeDensity();
	}
	
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
	
	public int getHomeDensity() {
		return homeDensity;
	}
	
	private int countHomeDensity() {
		int dens = 0;
		for (int i = 0; i < resolution; i++) {
			if (home[i] != 0) {
				dens++;
			}
		}
		return dens;
	}
	
	public static void main(String[] args) {
		
		//Variator kickVariator = getDefaultKickVariator();
		//Variator snareVariator = getDefaultSnareVariator();
		//Variator hatVariator = getDefaultHatVariator();
		//printArray(kickVariator.getBasis());
		//printArray(kickVariator.getHome());

		/*Histogram histo = new Histogram(DEFAULT_RESOLUTION);
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
		double[] kickVariation = kickVariator.makeVariation(23);
		//int[] snareVariation = snareVariator.makeVariation(3);
		//
		//int[] hatVariation = hatVariator.makeVariation(1);
		
		Tools.printArray(kickVariation);
		//Tools.printArray(snareVariation);
		//Tools.printArray(hatVariation);
		 */
		
		double[] basis = {0.99,0,.5,0,.99,0,.5,0};
		double[] home = {1.2,0,0,.7,1,0,.95,0};
		double[] velocities = {1.20,.30,1.00,.60,1.10,.40,.80,1.05};
		Variator v = new Variator(basis, home, 8);
		v.setVelocities(velocities);
		for (int i = 0; i < v.getResolution()+1; i++) {
			double[] variation = v.makeVariation(i,.8);
			System.out.println(Tools.printArray(variation));
		}
		double[] variation = v.makeVariation(3, 0);
		System.out.println("Variation before right shift: " + Tools.printArray(variation));
		System.out.println("Total Strength Before: " + BeatStrength.getTotalStrength(variation));
		System.out.println("Average Strength Before: " + BeatStrength.getAverageStrength(variation));
		System.out.println("BeatSrength Array: " + Tools.printArray(BeatStrength.buildArray(8)));
		for (int i = 2; i < 5; i++) {
			if (variation[i] != 0) {
				variation[i+1] = variation[i];
				variation[i] = 0;
				i++;
			}
		}
		
		System.out.println("Variation after right shift: " + Tools.printArray(variation));
		System.out.println("Total Strength: " + BeatStrength.getTotalStrength(variation));
		System.out.println("Average Strength: " + BeatStrength.getAverageStrength(variation));

	}
	
	

}
