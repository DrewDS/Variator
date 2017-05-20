package main;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import com.cycling74.max.*;

/**
 * 
 * @author drewdyer-symonds
 *
 *	This is the high level class that must be included in the Max4Live classpath
 *
 *	It is the Java external that will be referenced in a Max object titled VariatorObject.mxj
 *  Changes to anything in this code will need to be refreshed within Max4Live simply by retyping 
 *  the name of its Max Object and pressing the return key.
 *
 */

public class VariatorObject extends MaxObject {

	private static int DISPLAY_RES = 16;
	private String rawAbletonClipData = "";
	private int length;
	private FullPhrase fullPhrase;
	
	/** Constructor **/
	public VariatorObject() {
		
		post("Last VariatorObject.mxj reinitialized on " + Tools.getTimeStamp());
		declareIO(3, 3);

	}
	
	public void initializeClip() {
		
		// Make sure rawAbletonClipData and clipLength are initialized and
		// then initialize fulPhrase here:
		fullPhrase = new FullPhrase(DISPLAY_RES, length, "default");
		outlet(3, DISPLAY_RES);
		post("fullPhrase initialized - Length: " + length);
		
	}
 
	/**
	 * Creates and formats the data for 1 bar (barNum) and 1 pitch (pitch)
	 * This data is passed to the Max4Live environment in an Atom Array, which
	 * is then parsed by a JavaScript Object using the Ableton Live API to 
	 * actually write the notes described by the data.
	 * 
	 * @param density
	 * @param drumName
	 * @param barNum
	 * @param velocityFactor
	 * @param pitch
	 */
	public void makeVariation(int density, String drumName, int barNum, double velocityFactor, int pitch) {
		
		double[] variation = fullPhrase.makeVariation(density, drumName, velocityFactor, barNum);
		Atom[] formatted = formatForJSPort(variation, barNum, pitch);
		
		outlet(1, formatted);
		
	}
	
	/**
	 * Associates the data from a clip for a single pitch (from rawAbletonClipData) 
	 * to a drumName inside fullPhrase.  
	 *
	 * @param drumName
	 * @param pitch
	 */
	public void addDrumHome(String drumName, int pitch) {

		fullPhrase.addDrumHomeFromAbleton(drumName, rawAbletonClipData, pitch, HomeSource.ABLETON_CLIP);

	}
	
	/**
	 * Sets rawAbeltonClipData from the selected Abelton Clip 
	 * by parsing the Atom array passed from the Max4Live environment
	 * 
	 * @param args
	 */
	public void setRawData(Atom[] args) {
		
		String noteData = "";
		for (Atom a : args) {
			String s = a.toString() + " ";
			noteData += s;
		}
		rawAbletonClipData = noteData;
	}
	
	/**
	 * Sets the length field of this object to match the number of bars
	 * of the Ableton Clip.
	 * 
	 * This is required before initilialization of this object
	 * 
	 * @param length
	 */
	public void setLength(String length) {
		
		this.length = Integer.parseInt(length);
		//post("Length: " +  this.length);
		
	}
	
	/**
	 * Sets the basis from the Presets.xml file
	 * 
	 * @param basisPresetName
	 */
	public void setBasisFromPreset(String basisPresetName) {
		
		fullPhrase.setBasisFromPreset(basisPresetName);

	}
	
	/**
	 * Sets the Velocity Map from the Presets.xml file
	 * 
	 * @param velocityMapPresetName
	 */
	public void setVelocityMapFromPreset(String velocityMapPresetName) {
		
		fullPhrase.setVelocityMapFromPreset(velocityMapPresetName);

	}
	
	/**
	 * Gets the list of drums contained in fullPhrase and outputs them
	 * as an Atom Array for use in the Max4Live app and, ultimately, user interface
	 * 
	 */
	public void getBasisDrums() {
		
		ArrayList<String> basisDrums = fullPhrase.getBasisDrums();
		Atom[] formatted = new Atom[basisDrums.size()];
		
		for (int i = 0; i < basisDrums.size(); i++) {	
			
			formatted[i] = Atom.newAtom(basisDrums.get(i));
			
		}
		outlet(2, formatted);
	}
	
	/**
	 * Formats a variatation of a single pitch and single bar for use with the JSPort object,
	 * ultimately allowing the variation to be written into the selected Abelton Live clip.
	 * 
	 * @param variation
	 * @param barNumber
	 * @param pitch
	 * @return
	 */
	private Atom[] formatForJSPort(double[] variation, int barNumber, int pitch) {
		
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
	
	/** 
	 * (Debugging Method)
	 * Prints relevant information on the 1-pitch, 1-bar Variator, v 
	 * to the Max console
	 * 
	 * @param v
	 */
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
		double[] variation = v.makeVariation(2,0);
		for (int i = 0; i < v.getBasis().length; i++) {
			svar += variation[i] + " ";
			
		}
		post("VARIATON: < " + svar + ">");
	}
	
	
	/**
	 * (Debugging Method)
	 * Prints relevant information on every Variator in fullPhrase to the MaxConsole
	 * 
	 */
	public void postVariators() {
		
		LinkedHashMap<String, Variator> allVariators = fullPhrase.getFullVariators().get(0).getAllVariators();
		
		for (String name : allVariators.keySet()) {
			
			post("name" + name);
			post("Basis: " + Tools.printArray(allVariators.get(name).getBasis()));
			post("Home: " + Tools.printArray(allVariators.get(name).getHome()));
			post("Sum: " + Tools.printArray(allVariators.get(name).getSum()));
			
		}
		
	}
	
	/**
	 * Catch for an irrelevant method signature entering the same Max4Live Object inlet
	 * as other, relevant methods. 
	 * 
	 * (less-than graceful work-around for an oddity of Max4Live, but the alternative would 
	 * be to add an extra outlet to the originating object, which is also ugly)
	 */
	public void pitches() {
		// DO NOTHING
		// This is meant for the maxEnviroment, not this external
	}
	
}
