package main;

import java.util.ArrayList;

public class BasisPhrase extends Phrase {
	

	public BasisPhrase(int displayRes, int numOfBars) {
		super(displayRes, numOfBars);
	}
	
	// Creates default BasisPhrase, where each Basis bar is the default bartype (i.e. 0) of the preset 
	public BasisPhrase(int displayRes, int numOfBars, String presetName) {
		
		super(displayRes, numOfBars, presetName);
		VariatorObject.post("Creating Basis phrase from: " + presetName + " preset... ");
		
	}
	
	public BasisPhrase(int displayRes, int numOfBars, String presetName, int[] pattern) {
		
		super(displayRes, numOfBars, presetName, pattern);
		
	}
	
	@Override
	protected void populateBars() {
		
		VariatorObject.post("Populating Bars (check)...");
		
		assert length > 0;
		assert arrangement.size() == length;
		assert bars.size() == length;

		
		for(int i = 0; i < length; i++) {
			
			VariatorObject.post("Populating Bar " + i + " ...");
			try {
				
				VariatorObject.post("size of bar array: " + bars.size());
				
				if (bars.size() <= i) {
					VariatorObject.post("size of arrangement array: " + arrangement.size());
					bars.add(new FullBasis(displayRes, presetName, arrangement.get(i)));
				} else {
					VariatorObject.post("size of bar array(check): " + bars.size());
					bars.set(i, new FullBasis(displayRes, presetName, arrangement.get(i)));
				}
				
			} catch (IllegalArgumentException e) {
				
				System.out.println("*** No preset named " + presetName + " Exists");
				System.out.println("Setting Basis Bar to default FullBasis preset");
				
				bars.set(i, new FullBasis(displayRes, "default", arrangement.get(i)));
				
			}
			
		}	
	}
	
	/**
	 * Passes the ArrayList containing the drumNames ultimately out to the
	 * Max4Live enviroment
	 */
	public ArrayList<String> getBasisDrums() {
		
		FullBasis fb = (FullBasis) bars.get(0);
		return fb.getKeys();
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
