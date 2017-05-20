package main;
import java.util.ArrayList;

import com.cycling74.max.*;


public class VariatorObjectRef extends MaxObject {

	private static double DEFAULT_VELOCITY = 100;
	private static int DISPLAY_RES = 16;
	private long pitch;
	private String rawAbletonClipData = "";
	private FullVariator fullVariator;
	
	/** Constructor **/
	public VariatorObjectRef() {
		
		declareIO(3, 3);
		//BasisPresets.init(DISPLAY_RES);
		fullVariator = new FullVariator(DISPLAY_RES);
		
	}
 
	
	public void makeVariation(int density, String drumName, double velocityFactor, int barNum) {
		
		double[] variation = fullVariator.makeVariation(density, drumName, velocityFactor);
		Atom[] formatted = formatForJSPort(variation, barNum);
		
		outlet(1, formatted);
		
	}
	
	// ONLY ADDS DRUM HOME FROM BAR 0 !!!!! CHANGE WHEN USING FullPhrase
	public void addDrumHome(String drumName) {
		//outlet(1, rawAbletonClipData);
		fullVariator.addDrumHomeFromAbleton(drumName, rawAbletonClipData, pitch, HomeSource.ABLETON_CLIP, 0);
		// THIS LINE MOVED TO FULL VARIATOR - fullVariator.updateVariator(drumName);
	}
	
	
	public void setRawData(Atom[] args) {
		
		String noteData = "";
		for (Atom a : args) {
			String s = a.toString() + " ";
			noteData += s;
		}
		rawAbletonClipData = noteData;
	}
	
	// delete this method when using FullPhrase
	public void setBasis(String basisPresetName) {
		
		fullVariator.setFullBasis(basisPresetName);

	}
	
	// Change this method when FullPhrase is used
	public void setBasisFromPreset(String basisPresetName) {
		
		fullVariator.setFullBasis(basisPresetName);

	}
	
	// delete this method when using FullPhrase
	public void setVelocityMap(String presetName) {
		
		fullVariator.setVelocityMap(presetName);
		
	}
	
	// Change this method when FullPhrase is used
	public void setVelocityMapFromPreset(String basisPresetName) {
		
		fullVariator.setFullBasis(basisPresetName);

	}
	
	public void getBasisDrums() {
		
		ArrayList<String> basisDrums = fullVariator.getBasisDrums();
		Atom[] formatted = new Atom[basisDrums.size()];
		
		for (int i = 0; i < basisDrums.size(); i++) {	
			
			formatted[i] = Atom.newAtom(basisDrums.get(i));
			
		}
		outlet(2, formatted);
	}
	
	
	private Atom[] formatForJSPort(double[] variation, int barNumber) {
		
		String signature = "writeVariation";
		
		Atom[] maxMessage = new Atom[4]; // [signature, note data, key, barNumber]
		
		double resolution = variation.length;
		
		// Formatting arguments for use in JS script
		String noteData = "Notes " + String.valueOf(pitch) + " ";		
				
		for (int i = 0; i < variation.length; i++) {
			
			
			if  (variation[i] > 0) {
				
				// position expressed in a decimal fraction of 1 bar
				double position = ((4 * i) / resolution) + (4 * barNumber);
				// Default duration of 1 smallest resolution unit
				double duration = 1 / resolution;
				// Default un-muted
				int muted = 0;
				String data = "Note " + String.valueOf(pitch) + " " +
							  String.valueOf(position) + " " + 
							  String.valueOf(duration) + " " +
							  String.valueOf(variation[i]*100) + " " +
							  String.valueOf(muted) + " ";
				noteData += data;
				
			}
			
		}
		
		Atom call = Atom.newAtom(signature);
		Atom data = Atom.newAtom(noteData);
		Atom key = Atom.newAtom(pitch);
		Atom bar = Atom.newAtom(barNumber);
		
		maxMessage[0] = call;
		maxMessage[1] = data;
		maxMessage[2] = key;
		maxMessage[3] = bar;
		
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
	
	
	
	public void setPitch(int pitch, String drumName) {
		this.pitch = pitch;
	}
	
	public void pitches() {
		// DO NOTHING
		// This is meant for the maxEnviroment, not this external
	}
	
	public void postVariators() {
		
		for (String name : fullVariator.getAllVariators().keySet()) {
			
			post("name" + name);
			post("Basis: " + Tools.printArray(fullVariator.getAllVariators().get(name).getBasis()));
			post("Home: " + Tools.printArray(fullVariator.getAllVariators().get(name).getHome()));
			post("Sum: " + Tools.printArray(fullVariator.getAllVariators().get(name).getSum()));
			
		}
		
	}
	
}
