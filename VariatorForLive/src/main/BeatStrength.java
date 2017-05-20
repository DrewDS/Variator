package main;

import java.util.ArrayList;

/**
 * Class to define another system of weighting rythmic subdivisions within a bar
 * 
 * This is not fully developed, and may ultimately require a more complex solution.
 * 
 * For example: Rather that attempt to further implement expentencies
 * heuristically, such as through weightings, I think I would rather pursue 
 * a ML expectency model and fit this into the "bar-based" architecture ex post facto.
 * 
 * @author drewdyer-symonds
 *
 */
public class BeatStrength {

	private int resolution;
	private double[] beatStrengthArray; 
	
	
	public BeatStrength(int resolution) {
		
		this.resolution = resolution;
		beatStrengthArray = buildArray(resolution);
		
	}
	
	/*
	public ArrayList<ArrayList<Integer>> singleDensityPermutationMatrix(int density) {
		
		assert density <= resolution;
			
	}*/
	
	public static double getTotalStrength(double[] variation) {
		
		double[] beatStrengthArray = buildArray(variation.length);
		
		int totalBeatStrength = 0;
		
		// Finds total beat strength by treating every non-zero variation point as 1
		// Does not account for velocity ... yet :)
		for (int i = 0; i < variation.length; i++) {
			
			if (variation[i] != 0) {
				
				totalBeatStrength += beatStrengthArray[i];
				
			}
		}
		
		return totalBeatStrength;
	}
	
	
	public static double getAverageStrength(double[] variation) {
		
		int count = 0;
		for (int i = 0; i < variation.length; i++) {
			
			if (variation[i] != 0) {
				
				count++;
				
			}
		}
		return getTotalStrength(variation) / count;
		
	}
	
	
	// public double[] leftShiftSingle(double[] variation);
	
	public static int powerOfTwoExponent(int number) {
		
		int exponent = 1;
		
		if (number == 1) {return 0;}
		
		try {
			
			if (!Tools.isPowerOfTwo(number)) {	
				
				throw new IllegalArgumentException("Input number is not a power of two");
				
			} else {			
				
				while (number > 2) {
					exponent++;
					number/=2;
				}
			}
			
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		}
		
		return exponent;
		
	}
	
	/**
	 * 
	 * Builds array that represents beat strength heirarchy where each lower level 
	 * represents the down beat of another division by two
	 * 
	 */
	public static double[] buildArray(int resolution) {
		
		int level = powerOfTwoExponent(resolution);
		ArrayList<Double> arr = new ArrayList<Double>();
		arr.add(0.0);

		
		ArrayList<Double> weightedArray = buildArrayHelper(arr, level);
		
		double[] outArray = new double[weightedArray.size()];
		
		// Make the returned array so that the strongest beat 
		//     has the highest number starting at level
		for (int i = 0; i < weightedArray.size(); i++) {
			outArray[i] = level - weightedArray.get(i);		
		}
		
		return outArray;
		
	}
	
	public static ArrayList<Double> buildArrayHelper(ArrayList<Double> arr, int level) {

		if (level == 0) {
			
			return arr;
			
		}
		
		for (int i = 0; i < arr.size(); i++) {
			
			arr.set(i, arr.get(i) + 1);
			
		}
		
		arr.addAll(arr);
		arr.set(0, 0.0);
		
		return buildArrayHelper(arr, level-1);
		
	}
	
	
	public static void main(String[] args) {
		
		BeatStrength bs = new BeatStrength(16);
		
		System.out.println(BeatStrength.powerOfTwoExponent(32));
		
		System.out.println(Tools.printArray(BeatStrength.buildArray(16)));
		double[] variation = {0.0,0.0,0.0,0.0,1.0,0.0,0.0,1.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0}; 
		System.out.println(BeatStrength.getTotalStrength(variation));
		System.out.println(BeatStrength.getAverageStrength(variation));

	}
	
}
