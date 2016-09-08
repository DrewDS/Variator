import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DataMap {
	
	String name;
	private final int baseResolution = 128;
	protected int displayRes;
	protected LinkedHashMap<String, double[]> data; 
	
	public DataMap(int displayRes) {
		this.displayRes = displayRes;
		data = new LinkedHashMap<String, double[]>();
	}
	
	
	public void addDrumData(String name, double[] drumData) {
		
		assert !data.containsKey(name);
		
		double[] newBasisData = matchDrumResolutionToMap(drumData);
		
		if (!data.containsKey(name)) {
			data.put(name, newBasisData);
		}
	}
	
	public void replaceDrumData(String name, double[] drumData) {
		
		assert data.containsKey(name);
		
		if (data.containsKey(name)) {
			data.remove(name);
			addDrumData(name, drumData);
		}
		
	}
	
	public void addZeroEntry(String name) {
		
		double[] emptyData = Tools.createZeroArray(baseResolution);
		
		if (data.containsKey(name)) {
			replaceDrumData(name, emptyData); 
		} else {
			addDrumData(name, emptyData);
		}
		
	}
	
	
	private double[] matchDrumResolutionToMap(double[] inputData) {
		return matchDrumResolution(inputData, baseResolution);
	}
	
	private double[] matchDrumResolution(double[] inputData, int newResolution) {
		
		int inputRes = inputData.length;
		
		assert inputRes > 0;
		assert isPowerOfTwo(inputRes);
		
		if (!isPowerOfTwo(newResolution)) {
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
	
	public double[] getDrumData(String drum) {
		
		if (getData().containsKey(drum)) {
			return getData().get(drum);
		} else {
			Exception e = new IllegalArgumentException("Drum does not exist in this data map");
			e.printStackTrace();
			return null;
		}
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
		if (!isPowerOfTwo(resolution)) {
			Exception e = new IllegalArgumentException("display resolution must be a power of two");
			e.printStackTrace();
			System.exit(1);
		}
		this.displayRes = resolution;
	}
	
	public void printData() {
		
		for (String key : data.keySet()) {
			System.out.print(key + ":\t");
			Tools.printArray(getDrumData(key, displayRes));
		}	
	}
	
	public ArrayList<String> getKeys() {
		ArrayList<String> keys = new ArrayList<String>(data.keySet());
		return keys;
	}
	
	public LinkedHashMap<String, double[]> getData() {
		return data;
	}
	
		
	
	private static boolean isPowerOfTwo(int number) {

		if ((number & (number - 1)) == 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
