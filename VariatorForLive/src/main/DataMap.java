package main;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Superclass for single-bar data maps such as FullBasis, FullHome, 
 * and VariatorMap.
 * 
 * The primary function of DataMap and its subclasses is to organize weighting
 * data for a single bar by associating it with a set of defined drumNames.
 * 
 * Auxillary functions include resolution matching (assuming there are descrepencies),
 * and creation of constant-entry maps for intialization and graceful degradation.
 * 
 * @author drewdyer-symonds
 *
 */
public class DataMap {
	
	private final int baseResolution = 128;
	protected int displayRes;
	protected LinkedHashMap<String, double[]> data; 
	
	public DataMap(int displayRes) {
		this.displayRes = displayRes;
		data = new LinkedHashMap<String, double[]>();
	}
	
	public DataMap(int displayRes, String mapType, String presetName) {
		
		this(displayRes, mapType, presetName, 0);
		
	}
	
	public DataMap(int displayRes, String mapType, String presetName, int presetBar) {
		
		this(displayRes);
		data = PresetReader.getData(mapType, presetName, presetBar);
		
	}
	
	/**
	 * 
	 * Creates new constant-filled DataMap with the same drumName key-set as the
	 * parameter, dataMap.
	 * 
	 * @param dataMap
	 * @param constant
	 */
	public static DataMap createMatchingConstantDataMap(DataMap dataMap, double constant) {
		
		DataMap newMap = new DataMap(dataMap.displayRes);
		
		for (String drumName : dataMap.getKeys()) {		
			newMap.addConstantEntry(drumName, constant);		
		}		
		return newMap;	
	}
	
	public void addDrumData(String name, double[] drumData) {
		
		double[] newBasisData = matchDrumResolutionToMap(drumData);
		
		//VariatorObject.post("New Data to add to Drum data for " + name + ": " + Tools.printArray(newBasisData));
		
		if (!data.containsKey(name)) {
			data.put(name, newBasisData);
		} else {
			replaceDrumData(name, newBasisData);
		}
	}
	
	public void replaceDrumData(String name, double[] drumData) {
		
		assert data.containsKey(name);
		
		if (data.containsKey(name)) {
			data.remove(name);
			addDrumData(name, drumData);
		}		
	}
	
	public void matchKeySet(DataMap dataMap, double defaultConstant) {
		
		for (String drumName : dataMap.getKeys()) {
			
			if ( !this.getData().containsKey(drumName) ) {				
				this.addConstantEntry(drumName, defaultConstant);				
			}	
		}
	}
	
	public void addConstantEntry(String name, double constant) {
		
		double[] emptyData = Tools.createConstantArray(baseResolution, constant);
		
		if (data.containsKey(name)) {
			replaceDrumData(name, emptyData); 
		} else {
			addDrumData(name, emptyData);
		}	
	}
	
	
	private double[] matchDrumResolutionToMap(double[] inputData) {
		return matchDrumResolution(inputData, baseResolution);
	}
	
	/** 
	 * Changes the resolution of an input array to newResolution by returning 
	 * a new array with new elements inserted or old ones removed depending on
	 * their relative lenghts.  
	 * 
	 * This only handles resolutions and input arrays of lengths that are a power of 2.
	 * 
	 * @param inputData
	 * @param newResolution
	 * @return
	 */
	private double[] matchDrumResolution(double[] inputData, int newResolution) {
		
		int inputRes = inputData.length;
		
		assert inputRes > 0;
		assert Tools.isPowerOfTwo(inputRes);
		
		if (!Tools.isPowerOfTwo(newResolution)) {
			Exception e = new IllegalArgumentException("new resolution is not a power of two");
			e.printStackTrace();
			System.exit(1);
		}
		
		double[] newData = new double[newResolution];
			
		if (inputRes > newResolution) {
			
			int factor = inputRes / newResolution;		
			for (int i = 0; i < newResolution; i++) {
				newData[i] = inputData[factor * i];
			}
			
		} else if (inputRes < newResolution) {
			
			int factor = newResolution / inputRes;			
			for (int i = 0; i < inputRes; i++) {
				newData[i * factor] = inputData[i];
			}
			
		} else {			
			return inputData;			
		}		
		return newData;		
	}
	
	
	public double[] getDrumData(String drum, int resolution) {
		if (getData().containsKey(drum)) {
			double[] data = matchDrumResolution(getData().get(drum), resolution);
			return data;
		} else {
			Exception e = new IllegalArgumentException("Drum does not exist in this data map");
			e.printStackTrace();
			return null;
		}
	}
	
	public int getDisplayRes() {
		return displayRes;
	}
	
	public void setDisplayRes(int resolution) {
		if (!Tools.isPowerOfTwo(resolution)) {
			Exception e = new IllegalArgumentException("display resolution must be a power of two");
			e.printStackTrace();
			System.exit(1);
		}
		this.displayRes = resolution;
	}
	
	public void printData() {
		
		for (String key : data.keySet()) {
			System.out.print(key + ":\t");
			System.out.println(Tools.printArray(getDrumData(key, displayRes)));
		}	
	}
	
	public ArrayList<String> getKeys() {
		ArrayList<String> keys = new ArrayList<String>(data.keySet());
		return keys;
	}
	
	public LinkedHashMap<String, double[]> getData() {
		return data;
	}
	
	/* Returns drum data in array of length base resolution
	 * 
	 * FOR TESTING ONLY: SHOULD NOT BE USED IN PRODUCTION CODE
	 * 
	public double[] getDrumData(String drum) {
		
		if (getData().containsKey(drum)) {
			return getData().get(drum);
		} else {
			Exception e = new IllegalArgumentException("Drum does not exist in this data map");
			e.printStackTrace();
			return null;
		}
	} 
	*/
	
}
