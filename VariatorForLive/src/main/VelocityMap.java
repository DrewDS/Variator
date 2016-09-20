package main;

public class VelocityMap extends DataMap {
	
	
	public static double EMPTY_VALUE = 1;
	
	public VelocityMap(int displayRes) {
		super(displayRes);
	}
	
	public VelocityMap(int displayRes, String presetName) {
		super(displayRes, "velocity", presetName);
	}
	
	public VelocityMap(DataMap dataMap) {
		super(dataMap.getDisplayRes());
		this.data = dataMap.getData();
	}
	
	public static VelocityMap getMatchingConstantVelocityMap(DataMap dataMap) {
		
		return new VelocityMap(createMatchingConstantDataMap(dataMap, EMPTY_VALUE));
		
	}
	
	public void addDrumVelocities(String drumName, double[] velocities) {
		
		// All drums in a velocity map must contain values between .01 and 1.27 inclusive
		for (int i = 0; i < velocities.length; i++) {
			
			if (velocities[i] <= 0) {
				velocities[i] = .01;
			}
			if (velocities[i] > 1.27) {
				velocities[i] = 1.27;
			}		
		}		
		addDrumData(drumName, velocities);	
	}

}
