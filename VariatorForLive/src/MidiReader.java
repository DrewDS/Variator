import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;


public class MidiReader {

	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	// < KEY, < Array of Lists of {Note Pairs} > > 
	private Map<Long, List<List<Note>>> eventData;
	
	/** Construct MidiReader from Sequence **/
	public MidiReader(Sequence sequence, int resolution) {
		
		eventData = makeBeatSortedNotes(sequence, resolution);
		
	}
	
	/** Construct MidiReader from File name **/
	public MidiReader(String fileName, int resolution) {
		
		assert fileName.endsWith(".mid");
		
		File midiFile = new File(fileName);
		
		assert midiFile.isFile();
		
		Sequence seq = Tools.sequenceFromFile(midiFile);
		eventData = makeBeatSortedNotes(seq, resolution);
		
	}
	
	
	private HashMap<Long, List<MidiEvent>> sortEventsByKey(Sequence sequence) {
		
		HashMap<Long, List<MidiEvent>> keySortedEvents = new HashMap<Long, List<MidiEvent>>();
		Track[] tracks = sequence.getTracks();
		
		for (Track track : tracks) {
			
			for (int i = 0; i < track.size(); i++) {
				
				MidiEvent event = track.get(i);
				
				if (event.getMessage() instanceof ShortMessage) {
					
					ShortMessage sm = (ShortMessage) event.getMessage();
					Long key = (long) sm.getData1();
					
					if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
						
						if (keySortedEvents.containsKey(key)) {
							
							List<MidiEvent> monophonicEventList = keySortedEvents.get(key);
							monophonicEventList.add(event);
							keySortedEvents.put(key, monophonicEventList);
							
						} else {
							
							List<MidiEvent> monophonicEventList = new ArrayList<MidiEvent>();
							monophonicEventList.add(event);
							keySortedEvents.put(key, monophonicEventList);
							
						}					
					}
				}			
			}			
		}
		return keySortedEvents;
	}
	
	/** Sorts pairs by beat for a segment of length 1 bar **/
	public Map<Long, List<List<Note>>> makeBeatSortedNotes(Sequence sequence, int resolution) {
		
		return makeBeatSortedPairs(sequence, resolution, 1);
		
	}
	
	public Map<Long, List<List<Note>>> getEventData() {
		return eventData;
	}


	/** Sorts pairs by beat for a segment of variable length **/
	public Map<Long, List<List<Note>>> makeBeatSortedPairs(Sequence sequence, int resolution, int numOfBars) {
		
		Map<Long, List<List<Note>>> data = new HashMap<Long, List<List<Note>>>();
		
		assert sequence.getDivisionType() != Sequence.PPQ : "Sequence Division Type must be in PPQ";
		
		long ppq = sequence.getResolution();
		long phraseLength = (ppq * 4) * numOfBars;
		long unitLength = phraseLength / resolution;
		
		// Quantize the input sequence
		Sequence quantized = Tools.quantize(sequence, resolution);
		
		HashMap<Long, List<MidiEvent>> sortedByKey = sortEventsByKey(quantized);
		
		// Iterate through each Key in the keysorted HashMap
		for (Long key : sortedByKey.keySet()) {
			
			MonophonicMidiReader reader = new MonophonicMidiReader(sortedByKey.get(key));
			List<List<Note>> beatArrayList = new ArrayList<List<Note>>(resolution);
			
			// Initialize sublists of beatArrayList
			for (int i = 0; i < resolution; i++) {
				beatArrayList.add(new ArrayList<Note>());
			}
			
			
			List<Note> noteList = reader.makeNotes();
			
			// Interate through each note (This will only be for one key)
			for (Note note : noteList) {
				
				// Find position in bar in ticks
				double positionInBar = note.getPosition() % phraseLength;
				
				// Find position in bar in beats
				long beatInBar = (long) (positionInBar / unitLength);
				
				// Add the pair to the ArrayList at the appropriate place in beatArrayList
				beatArrayList.get((int) beatInBar).add(note);
							
			}
			
			// Add the beatArrayList to the HashMap with the key
			data.put(key, beatArrayList);
		}
		
		return data;
		
	}
	
	
	public static void main(String[] args) {
		
		//MidiReader mr = new MidiReader("pianoImprovTest.mid", 16);
		
		//Map<Long, List<List<Note>>> data = mr.eventData;
		
	
	}
	
	
	
	
	
}
