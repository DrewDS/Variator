package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FullHome extends DataMap {
	
	private static double EMPTY_VALUE = 0;
	//private HashMap<String, Integer> pitchMap = new HashMap<String, Integer>();

	
	public FullHome(int displayRes) {
		super(displayRes);
	}
	
	public FullHome(int displayRes, String presetName) {
		super(displayRes, "home", presetName);
	}
	
	public FullHome(int displayRes, String presetName, int bar) {
		super(displayRes, "home", presetName, bar);
	}
	
	/** 
	 * Safely Construct FullHome from DataMap
	 *   Created for use with making a map full of 
	 *   constants with a matching keySet
	 * 
	 * @param dataMap
	 */
	public FullHome(DataMap dataMap) {
		super(dataMap.getDisplayRes());
		this.data = dataMap.getData();
	}
	
	public static FullHome getMatchingConstantHome(DataMap dataMap) {
		
		return new FullHome(createMatchingConstantDataMap(dataMap, EMPTY_VALUE));
		
	}
	
	/** 
	 * Adds data for the drumName from one of two sources.  ABLETON_CLIP is
	 * most commonly used in this program, but the source could be a MIDI_FILE.
	 * 
	 * Other sources may become useful in the future, hence the switch structure.
	 * 
	 */
	public void addDrumHome(String drumName, String data, long pitch, HomeSource sourceType, int barNum) {
		
		switch (sourceType) {
		
			case ABLETON_CLIP:	addDrumData(drumName, makeDrumHomeFromAbletonClip(data, pitch, barNum));
								//VariatorObject.post("Attempted to add the following home to " + drumName + " in fullHome: " + Tools.printArray(makeDrumHomeFromAbletonClip(data,pitch)));
								//VariatorObject.post("Home for " + drumName + " after adding to FullHome : " + Tools.printArray(this.getDrumData(drumName)));
								break;
			case MIDI_FILE:		addDrumData(drumName, makeDrumHomeFromMidiFile(data, pitch));
								break;
		}
		
	}
	
	
	// Maybe Assumes only one BAR?  VERIFY
	/**
	 * Get's a home array for a single pitch from a midi file
	 * 
	 * @param fileName Name of midi file
	 * @param pitch The pitch to operate on
	 * @return
	 */
	public double[] makeDrumHomeFromMidiFile(String fileName, long pitch) {
		
		// Using a Histogram object is due to the pre-built quantizing and midireading functionality
		Histogram histo = new Histogram(getDisplayRes());
		
		histo.addFile(fileName);
		
		double[] home = new double[histo.getResolution()];
		
		List<List<Note>> beats = histo.getEventData().get(pitch);
		
		for (int i = 0; i < beats.size(); i++) {
			
			List<Note> pairs = beats.get(i);
			home[i] = pairs.size();
			
		}
		
		return home;
	}
	
	// ASSUMES 4/4 TIMING
	/**
	 * Get's a home array from an Ableton Clip 
	 * Integrates with variator-port.js
	 * 
	 * @param rawString String formatted by variator-port.js
	 * @param pitch
	 * @return
	 */
	public double[] makeDrumHomeFromAbletonClip(String rawString, long pitch, int barNum) {
		
		double division = 4.0/getDisplayRes();

		double[] home = new double[getDisplayRes()];
		
		ArrayList<Note> Notes = new ArrayList<Note>();
		
		String[] noteStrings = rawString.split("Note ");
		
		// POSSIBLE RANGE BUG
		// Start at i = 1 because noteStrings[0] is ""
		for (int i = 1; i < noteStrings.length; i++) {
			
			String[] properties = noteStrings[i].split(" ");
			
			long notePitch = Long.parseLong(properties[0]);
			double position = Double.parseDouble(properties[1]);
			double duration = Double.parseDouble(properties[2]);
			double velocity = Double.parseDouble(properties[3]);
			
			if (notePitch == pitch) {
				Note note = new Note(notePitch, position, duration, velocity, Note.DivisionType.FRACTIONAL);
				Notes.add(note);
				VariatorObject.post("Note added to Home (FullHome.java): " + notePitch + " " + position +" " + duration + " "+velocity);
			}				
		}
		
		// Creates home array for a drum by adding the velocity from the raw Ableton Clip Data
		// to the home array
		ArrayList<Note> quantized = Tools.quantize(Notes, getDisplayRes());
		
		for (Note note : quantized) {
			
			if (note.getPosition() >= barNum*4 && note.getPosition() < (barNum+1)*4) {

				VariatorObject.post("Note Velocity: " + note.getVelocity());
				VariatorObject.post("Divided by 100: " + note.getVelocity()/100);
				
				// subtract the number of bars (in ableton fractional quarter notes from the position) and divide by division
				// to get the index in the home array
				home[(int) ((note.getPosition()-4*barNum)/division)] = note.getVelocity()/100;
				
				
			}
			
		}
		
		//VariatorObject.post("Home Array to add to FullVariator: " + Tools.printArray(home));
		
		return home;		
	}

}
