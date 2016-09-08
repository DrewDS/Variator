import java.util.ArrayList;

import com.cycling74.max.*;


public class VariatorObject extends MaxObject {

	private String homeString = "";
	private static double DEFAULT_VELOCITY = 100;
	private static int DISPLAY_RES = 16;
	private long pitch;
	private String rawAbletonClipData = "";
	private FullVariator fullVariator;
	
	/** Constructor **/
	public VariatorObject() {
		
		declareIO(3, 3);
		BasisPresets.init(DISPLAY_RES);
		fullVariator = new FullVariator(DISPLAY_RES);
		
	}
	
	public void inlet(int density) {
		
		if (getInlet() ==  0) {
			
			Variator testVariator = Variator.getDefaultKickVariator();
			double[] testVariation = testVariator.makeVariation(density);
			Atom[] formatted = formatForJSPort(testVariation, pitch);
			
			outlet(1, formatted);
		}
	}
	
	public void makeVariation(int density, String drumName) {
		
		Variator variator = fullVariator.getVariators().get(drumName);
		post("Variator name: " + drumName);
		post("Variator basis: " + Tools.printArray(variator.getBasis()));
		post("Variator home: " + Tools.printArray(variator.getHome()));
		;
		post("Variator sum: "+ Tools.printArray(variator.getSum()));
		double[] variation = variator.makeVariation(density);
		Atom[] formatted = formatForJSPort(variation, pitch);
		
		outlet(1, formatted);
		
	}
	
	
	public void addDrumHome(String drumName) {
		//outlet(1, rawAbletonClipData);
		fullVariator.addDrumHomeFromAbleton(drumName, rawAbletonClipData, pitch, HomeSource.ABLETON_CLIP);
		// THIS LINE MOVED TO FULL VARIATOR - fullVariator.updateVariator(drumName);
	}
	
	public void setHome(Atom[] args) {
		
		String noteData = "";
		for (Atom a : args) {
			String s = a.toString() + " ";
			noteData += s;
		}
		homeString = noteData;
	}
	
	public void setRawData(Atom[] args) {
		
		String noteData = "";
		for (Atom a : args) {
			String s = a.toString() + " ";
			noteData += s;
		}
		rawAbletonClipData = noteData;
	}
	
	
	
	
	public void bang() {
		
		
		
		Variator testVariator = Variator.getDefaultKickVariator();
		//post("Default: ");
		//postVariatorInfo(testVariator);
		
		testVariator.setHome(homeString, 60);
		
		post("-----------------------");
		post("New Home: ");
		postVariatorInfo(testVariator);
		
		
		double[] testVariation = testVariator.makeVariation(2);
		Atom[] formatted = formatForJSPort(testVariation, 60);
		
		outlet(0, "Bang");
		outlet(1, formatted);
		
		
	}
	
	public void setBasis(String basis) {
		
		BasisPresets.getPreset("rock");
		
		fullVariator.setBasis(BasisPresets.getPreset("rock"));
	
	}
	
	public void getBasisDrums() {
		
		ArrayList<String> basisDrums = fullVariator.getBasisDrums();
		Atom[] formatted = new Atom[basisDrums.size()];
		
		for (int i = 0; i < basisDrums.size(); i++) {	
			
			formatted[i] = Atom.newAtom(basisDrums.get(i));
			
		}
		outlet(2, formatted);
	}
	
	
	private Atom[] formatForJSPort(double[] variation, double key) {
		
		String signature = "writeVariation";
		
		Atom[] maxMessage = new Atom[2];
		
		double resolution = variation.length;
		
		// Formatting arguments for use in JS script
		String noteData = "Notes " + String.valueOf(key) + " ";		
				
		for (int i = 0; i < variation.length; i++) {
			
			if  (variation[i] == 1) {
				
				double position = (4 * i) / resolution;
				// Default duration of 1 smallest resolution unit
				double duration = 1 / resolution;
				// Default un-muted
				int muted = 0;
				String data = "Note " + String.valueOf(pitch) + " " +
							  String.valueOf(position) + " " + 
							  String.valueOf(duration) + " " +
							  String.valueOf(DEFAULT_VELOCITY) + " " +
							  String.valueOf(muted) + " ";
				noteData += data;
				
			}
			
		}
		
		Atom call = Atom.newAtom(signature);
		Atom arg = Atom.newAtom(noteData);
		
		maxMessage[0] = call;
		maxMessage[1] = arg;
		
		return maxMessage;
		
	}
	
	private static void postVariatorInfo(Variator v) {
		String home = "";
		for (int i = 0; i < v.getHome().length; i++) {
			home += v.getHome()[i] + " ";
		}
		post("HOME: < " + home + ">");
		String basis = "";
		for (int i = 0; i < v.getBasis().length; i++) {
			basis += v.getBasis()[i] + " ";
		}
		post("Basis: < " + basis + ">");
		String svar = "";
		double[] variation = v.makeVariation(2);
		for (int i = 0; i < v.getBasis().length; i++) {
			svar += variation[i] + " ";
			
		}
		post("VARIATON: < " + svar + ">");
	}
	
	
	
	public void setPitch(long pitch) {
		this.pitch = pitch;
	}
	
	public void pitches() {
		// DO NOTHING
		// This is meant for the maxEnviroment, not this external
	}
	
	public void postVariators() {
		
		for (String name : fullVariator.getVariators().keySet()) {
			
			post("name" + name);
			post("Basis: " + Tools.printArray(fullVariator.getVariators().get(name).getBasis()));
			post("Home: " + Tools.printArray(fullVariator.getVariators().get(name).getHome()));
			post("Sum: " + Tools.printArray(fullVariator.getVariators().get(name).getSum()));
			
		}
		
	}
	
}
