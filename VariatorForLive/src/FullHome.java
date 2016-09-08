import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FullHome extends DataMap {
	
	private LinkedHashMap<String, double[]> flatHome; 
	
	public FullHome(int displayRes) {
		super(displayRes);
	}
	
	public static FullHome getEmptyHome(int displayRes) {
		return new FullHome(displayRes);
	}
	
	
	public void addDrumHome(String drumName, String data, long pitch, HomeSource sourceType) {
		
		switch (sourceType) {
		
			case ABLETON_CLIP:	addDrumData(drumName, makeDrumHomeFromAbletonClip(data, pitch));
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
	
	// ONLY USES FIRST BAR OF ABLETON CLIP AND ASSUMES 4/4 TIMING
	/**
	 * Get's a home array from an Ableton Clip 
	 * Integrates with variator-port.js
	 * 
	 * @param rawString String formatted by variator-port.js
	 * @param pitch
	 * @return
	 */
	public double[] makeDrumHomeFromAbletonClip(String rawString, long pitch) {
		
		double division = 4.0/getDisplayRes();

		double[] home = new double[getDisplayRes()];
		
		ArrayList<Note> Notes = new ArrayList<Note>();
		
		String[] noteStrings = rawString.split("Note ");
		
		// POSSIBLE RANGE BUG
		// Start at i = 1 because First noteStrings[0] is ""
		for (int i = 1; i < noteStrings.length; i++) {
			
			String[] properties = noteStrings[i].split(" ");
			
			long notePitch = Long.parseLong(properties[0]);
			double position = Double.parseDouble(properties[1]);
			double duration = Double.parseDouble(properties[2]);
			long velocity = Long.parseLong(properties[3]);
			
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
			
			if (note.getPosition() < 4) {

				// NOTE: THIS WILL CAUSE A BUG IF variator-port.js IS NOT UPDATED
				// TO REFLECT THE NEW FUNCTIONALITY
				home[(int) (note.getPosition()/division)] = 1;
				
				
			}
			
		}
		
		return home;
		
	}

}
