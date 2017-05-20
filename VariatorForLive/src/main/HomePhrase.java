package main;

public class HomePhrase extends Phrase {

	// Used to initialize HomePhrase in FullPhrase when drum names and assignements are not known
	public HomePhrase(int displayRes, int numOfBars) {
		super(displayRes, numOfBars);
	}
	
	// Creates default HomePhrase, where each Basis bar is the default bartype (i.e. 0) of the preset 
	public HomePhrase(int displayRes, int numOfBars, String presetName) {		
		super(displayRes, numOfBars, presetName);		
	}
	
	public HomePhrase(int displayRes, int numOfBars, String presetName, int[] pattern) {		
		super(displayRes, numOfBars, presetName, pattern);		
	}
	
	@Override
	void populateBars() {
		
		assert length > 0;
		assert arrangement.size() == length;
		assert bars.size() == length;
		
		if (presetName == null) {
			
			for (int i = 0; i < length; i++) {
				
				if (bars.size() <= i) {
					VariatorObject.post("size of arrangement array: " + arrangement.size());
					bars.add(new FullHome(displayRes, presetName, arrangement.get(i)));
				} else {
					VariatorObject.post("size of bar array(check): " + bars.size());
					bars.set(i, new FullHome(displayRes, presetName, arrangement.get(i)));
				}
				
			}
			
		} else {
		
			for (int i = 0; i < length; i++) {				
				bars.set(i, new FullHome(displayRes, presetName, arrangement.get(i)));			
			}	
			
		}
	}
	
}
