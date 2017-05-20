package main;

public class VelocityPhrase extends Phrase {

	public VelocityPhrase(int displayRes, int numOfBars) {
		super(displayRes, numOfBars);
	}
	
	// Creates default VelocityPhrase, where each Basis bar is the default bartype (i.e. 0) of the preset 
	public VelocityPhrase(int displayRes, int numOfBars, String presetName) {		
		super(displayRes, numOfBars, presetName);		
	}
	
	public VelocityPhrase(int displayRes, int numOfBars, String presetName, int[] pattern) {		
		super(displayRes, numOfBars, presetName, pattern);		
	}

	@Override
	void populateBars() {
		
		assert length > 0;
		assert arrangement.size() == length;
		assert bars.size() == length;

		
		for(int i = 0; i < length; i++) {
			
			try {
				
				if (bars.size() <= i) {
					bars.add(new VelocityMap(displayRes, presetName, arrangement.get(i)));
				} else {
					bars.set(i, new VelocityMap(displayRes, presetName, arrangement.get(i)));
				}
				
			} catch (IllegalArgumentException e) {
				
				System.out.println("*** No preset named " + presetName + " Exists");
				System.out.println("Setting Velocity Bar to an empty VelocityMap");
				
				bars.set(i, new VelocityMap(displayRes));
				
			}	
		}	
	}

	
	
}
