
public class VelocityMap extends DataMap {
	
	
	public VelocityMap(int displayRes) {
		super(displayRes);
	}
	
	public void addDrumVelocities(String drumName, double[] velocities) {
		
		// All drums in a velocity map must contain values between 1 and 127 inclusive
		for (int i = 0; i < velocities.length; i++) {
			
			if (velocities[i] <= 0) {
				velocities[i] = 1;
			}
			if (velocities[i] > 127) {
				velocities[i] = 127;
			}		
		}		
		addDrumData(drumName, velocities);	
	}

}
