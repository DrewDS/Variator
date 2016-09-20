package main;
import java.util.LinkedHashMap;

public abstract class DataMapPresets {

	private static LinkedHashMap<String, FullBasis> presets = new LinkedHashMap<String, FullBasis>();

	public DataMapPresets() {}
	
	public abstract void init(int displayRes);
	
	public static DataMap getPreset(String preset) {
		
		if (presets.containsKey(preset)) {
			
			return presets.get(preset);
			
		} else {
			
			Exception e = new Exception("No preset found");
			e.printStackTrace();
			return null;
			
		}
		
	}
	
}
