import java.util.LinkedHashMap;

public class BasisPresets {
	
	private static LinkedHashMap<String, FullBasis> presets = new LinkedHashMap<String, FullBasis>();
	
	private BasisPresets() {}
	
	public static FullBasis rock(int displayRes) {
		FullBasis rock = new FullBasis(displayRes);
		double[] kick = {.99,.5,.1,.5,.6,.7,.01,.1};
		double[] snare = {.1,.01,.99,.01,.1,.01,.99,.01};
		double[] hhClosed = {.99,.8,.89,.8,.99,.8,.89,.85};
		rock.addDrumBasis("kick", kick);
		rock.addDrumBasis("snare", snare);
		rock.addDrumBasis("hhClosed", hhClosed);	
		return rock;
	}
	
	public static void init(int displayRes) {
		presets.put("rock", rock(displayRes));
	}
	
	public static void main(String[] args) {
		
		FullBasis rock = BasisPresets.rock(16);
		double[]  kick = rock.getDrumData("kick", rock.getDisplayRes());
		Tools.printArray(kick);
		Tools.printArray(rock.getDrumData("snare", rock.getDisplayRes()));
		
	}
	
	public static FullBasis getPreset(String preset) {
		
		if (presets.containsKey(preset)) {
			
			return presets.get(preset);
			
		} else {
			
			Exception e = new Exception("No preset found");
			e.printStackTrace();
			return null;
			
		}
		
	}

}
