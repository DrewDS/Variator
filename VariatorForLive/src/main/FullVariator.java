package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.tools.Tool;



public class FullVariator {

	private FullBasis fullBasis;
	private FullHome fullHome;
	private VelocityMap velocityMap;
	private int resolution;
	
	private double DEFAULT_HOME_CONSTANT = 0;
	
	private LinkedHashMap<String, Variator> variators = new LinkedHashMap<String, Variator>();
	
	public FullVariator(int resolution) {
		this.resolution = resolution;
		fullBasis = new FullBasis(resolution);
		//fullHome = new FullHome(resolution);
		//velocityMap = new VelocityMap(resolution);
	}
	
	public void updateAllVariators() {
		
		if (fullBasis == null) {
			
			Exception e = new NullPointerException("there is no defined fullBasis to build variators");
			e.printStackTrace();
			System.exit(1);
			
		} else { // fullBasis is not null
			
			if (fullHome == null) {		
				fullHome = FullHome.getMatchingConstantHome(fullBasis);
			}
			if (velocityMap == null) {
				velocityMap = VelocityMap.getMatchingConstantVelocityMap(fullBasis);
			}
			
			fullHome.matchKeySet(fullBasis, DEFAULT_HOME_CONSTANT);
			velocityMap.matchKeySet(fullBasis, Tools.DEFAULT_INTERNAL_VEL);		
			
		}
		
		for (String drumName : fullBasis.getKeys()) {
			updateVariator(drumName);
		}
		
	}

	
	public void updateVariator(String drumName) {
		
		if (fullBasis == null) {
			
			Exception e = new NullPointerException("there is no defined fullBasis to build variators");
			e.printStackTrace();
			System.exit(1);
			
		} else {
			
			if (variators.containsKey(drumName)) { 	
				Variator drumVar = variators.get(drumName);
				drumVar.setHome(fullHome.getDrumData(drumName, resolution)); 
				drumVar.setVelocities(velocityMap.getDrumData(drumName, resolution));
				//VariatorObject.post("Variator for " + drumName + "exists, ");
				//VariatorObject.post("and its home is being updated with: " + Tools.printArray(fullHome.getDrumData(drumName, resolution)));
			} else {
				Variator drumVar = new Variator(fullHome.getDrumData(drumName, resolution),
												fullBasis.getDrumData(drumName, resolution),
												resolution);
				//VariatorObject.post("Variator for " + drumName + "does not exist, ");
				//VariatorObject.post("and its home is being updated with: " + Tools.printArray(fullHome.getDrumData(drumName, resolution)));
				variators.put(drumName, drumVar);
			}
		}
	}
	
	public void addDrumHomeFromAbleton(String drumName, String rawData, long pitch, HomeSource sourceType) {
		
		fullHome.addDrumHome(drumName, rawData, pitch, HomeSource.ABLETON_CLIP);
		VariatorObject.post("The home for drum [" + drumName + "] is : " + Tools.printArray(fullHome.getDrumData(drumName)));
		updateVariator(drumName);
		VariatorObject.post("And the home for it's variator is: " + Tools.printArray(variators.get(drumName).getHome()));
	}
	
	//public void addDrumHomeFromMidiFile(String drum)
	
	public boolean homeSubsetOfBasis() {
		
		// If fullHome is null, variators can still be built
		if (fullHome == null) {
			return true;
			
		// But if fullBasis is null, variators cannot be built
		} else if (fullBasis == null) {
			return false;
			
		// Otherwise loop through the keySet of fullHome and make sure every key is in fullBasis
		} else {
			for (String name : fullHome.getData().keySet()) {
				if (!fullBasis.getData().containsKey(name)) {
					return false;
				}
			// Loop completes, therefore every key in fullHome is also in fullBasis
			}
			return true;
		}
	}
	
	
	public void setFullBasis(FullBasis basis) {	
		fullBasis = basis;
		updateAllVariators();
	}
	
	public void setFullBasis(String presetName) {
		fullBasis = new FullBasis(resolution, presetName);
		updateAllVariators();
	}
	
	/*
	/**
	 * Sets the Basis for the FullVariator and rebuilds Variator map
	 * 
	 * @param basis
	 *
	public void setBasis(FullBasis basis) {
		
		fullBasis = basis;
				
		// Iterate through every drum in FullBasis
		for (String name : fullBasis.getData().keySet()) {
			
			// If the drum already has a Variator assigned to it
			// replace the basis of that Variator with the new basis		
			if (variators.containsKey(name)) {
				variators.get(name).setBasis(fullBasis.getDrumData(name, resolution));
			
			// The drum has no Variator assigned to it
			} else {
				
				// If the drum has no home array assigned to it
				if (fullHome == null || !fullHome.getData().containsKey(name)) {
					
					// Create an empty FullHome
					fullHome = FullHome.getEmptyHome(resolution);
					
					// Make an array of 0's that is the correct resolution
					fullHome.addConstantEntry(name, DEFAULT_HOME_CONSTANT);
					
					// And build a Variator from it
					Variator v = new Variator(fullBasis.getDrumData(name, resolution), 
											  fullHome.getDrumData(name, resolution), resolution);
					variators.put(name, v);
				}
				
				// If the drum has a home array already assigned
				else  {
					
					// Make a new variator with the data from FullBasis and FullHome
					// The getDrumData resolution paramater makes the basis, home, 
					// and therefore the Variator, the correct resolution
					Variator v  = new Variator(fullBasis.getDrumData(name, resolution), 
												fullHome.getDrumData(name, resolution), 
												resolution);
					variators.put(name, v);
			
				} 
			}
		}
	}
	*/
	
	
	public void setFullHome(FullHome home) {	
		fullHome = home;
		updateAllVariators();		
	}
	
	public void setFullHome(String presetName) {
		fullHome = new FullHome(resolution, presetName);
		updateAllVariators();
	}
	
	public void setVelocityMap(VelocityMap velMap) {	
		velocityMap = velMap;
		updateAllVariators();		
	}
	
	public void setVelocityMap(String presetName) {
		velocityMap = new VelocityMap(resolution, presetName);
		updateAllVariators();
	}
	
	public FullHome getFullHome() {
		
		return fullHome;
		
	}
	
	public FullBasis getFullBasis() {
		
		return fullBasis;
		
	}
	
	public ArrayList<String> getBasisDrums() {
		
		return fullBasis.getKeys();
		
	}
	
	public LinkedHashMap<String, Variator> getVariators() {
		return variators;
	}
	
	
	public static void main(String[] args) {
		
		FullVariator fv = new FullVariator(16);
		fv.setFullBasis(new FullBasis(16, "rock"));
		fv.setVelocityMap(new VelocityMap(16, "rock"));
		System.out.println(Tools.printArray(fv.variators.get("kick").makeVariation(16, 0)));
		
	}
	
	
}
