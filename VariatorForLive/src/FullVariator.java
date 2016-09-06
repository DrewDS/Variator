import java.util.ArrayList;
import java.util.LinkedHashMap;



public class FullVariator {

	private FullBasis fullBasis;
	private FullHome fullHome;
	private VelocityMap velocityMap;
	private int resolution;
	
	public FullVariator(int resolution) {
		this.resolution = resolution;
		fullBasis = new FullBasis(resolution);
		fullHome = new FullHome(resolution);
		velocityMap = new VelocityMap(resolution);
	}
	
	private LinkedHashMap<String, Variator> variators = new LinkedHashMap<String, Variator>();
	
	public void updateVariator(String drumName) {
		
		if (fullBasis == null) {
			
			Exception e = new NullPointerException("there is no defined fullBasis to build variators");
			e.printStackTrace();
			System.exit(1);
			
		} else {
			
			if (variators.containsKey(drumName)) { 			
				variators.get(drumName).setHome(fullHome.getDrumData(drumName, resolution)); 			
			}	
		}
	}
	
	public void addDrumHomeFromAbleton(String drumName, String rawData, long pitch, HomeSource sourceType) {
		
		fullHome.addDrumHome(drumName, rawData, pitch, HomeSource.ABLETON_CLIP);
		updateVariator(drumName);
	}
	
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
	
	/**
	 * Sets the Basis for the FullVariator and rebuilds Variator map
	 * 
	 * @param basis
	 */
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
				
				if (fullHome == null || !fullHome.getData().containsKey(name)) {
					
					// The drum has no home array assigned to it
					fullHome = FullHome.getEmptyHome(resolution);
					
					// Make and array of 0's that is the correct resolution
					double[] emptyHome = new double[resolution];
					for (int i = 0; i < resolution; i++) {
						emptyHome[i] = 0;
					}
					
					// And build a Variator from it
					Variator v = new Variator(fullBasis.getDrumData(name, resolution), 
											  emptyHome, resolution);
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
	
	public void seFullHome(FullHome home) {
		
		fullHome = home;
				
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
	
	
}
