import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.midi.*;


public class Tools {
	
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	public static void printMidiData(MidiEvent event) {
		
		List<MidiEvent> list = new ArrayList<MidiEvent>();
		list.add(event);
		printMidiData(list);
		
	}
	
	public static void printMidiData(Sequence sequence) {
		
		int i = 0;
		for (Track track : sequence.getTracks()) {
			
			System.out.println("\n======= Track: " + i + " ==========");
			i++;
			printMidiData(track);
			
		}
		
	}

	public static void printMidiData(Track track) {
		
		System.out.println("Size of track: " + track.size() + " Midi Events");
		for (int i = 0; i < track.size(); i++) {
			javax.sound.midi.MidiEvent midiEvent = track.get(i);
			System.out.print("\nEvent " + i + ":");
			System.out.println("\tTime Stamp in MIDI ticks: " + midiEvent.getTick());
			System.out.println("\t\tMIDI message status byte: " + midiEvent.getMessage().getStatus());
			byte[] data = midiEvent.getMessage().getMessage();
			System.out.print("\t\tMIDI message data: ");
			for (int j = 0; j < data.length; j++) {
				System.out.print("Byte " + j + ": " + Integer.toHexString(data[j]).toUpperCase() + ", ");
			}
			System.out.println();
			
		}
	}
	
	public static void printMidiData(List<MidiEvent> list) {
		for (int i = 0; i < list.size(); i++) {
			MidiEvent midiEvent = list.get(i);
			System.out.print("\nEvent " + i + ":");
			System.out.println("\tTime Stamp in MIDI ticks: " + midiEvent.getTick());
			System.out.println("\t\tMIDI message status byte: " + midiEvent.getMessage().getStatus());
			byte[] data = midiEvent.getMessage().getMessage();
			System.out.print("\t\tMIDI message data: ");
			for (int j = 0; j < data.length; j++) {
				System.out.print("Byte " + j + ": " + Integer.toHexString(data[j]).toUpperCase() + ", ");
			}
			System.out.println();
		}
	}
	
	/** Returns a midi Sequence from a .midi File **/
	public static Sequence sequenceFromFile(File midiFile) {
		
		Sequence sequence = null;
		
		try {
			sequence = MidiSystem.getSequence(midiFile);
		} catch (InvalidMidiDataException e) {
			System.out.println("InvalidMidiData");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		return sequence;		
	}
	
	/**
	 * Quantizes ArrayList of Notes.  Assumes that the division type is quarter note and fractional
	 * 
	 * @param Notes
	 * @param resolution
	 * @return
	 */
	public static ArrayList<Note> quantize(ArrayList<Note> Notes, double resolution) {
		
		@SuppressWarnings("unchecked")
		ArrayList<Note> quantized = (ArrayList<Note>) Notes.clone();
		
		double unitLength = 4/resolution;
		
		for (Note note : quantized) {
			
			assert note.getDivisionType() == Note.DivisionType.FRACTIONAL : "This Method assumes Ableton Fractional Division Type";
			
			double position = note.getPosition();
			double numOfUnits = position/unitLength;
			
			double quotient = Math.floor(numOfUnits);
			double remainder = numOfUnits - quotient;
			
			double newPosition;
			
			if (remainder < 0.5) {
				newPosition = quotient * unitLength;
			}
			else {
				newPosition = (quotient * unitLength) + unitLength;
			}	
			
			note.setPosition(newPosition);
		}
		
		return quantized;
		
	}
	
	
	/** 
	 * 
	 * Quantizes Midi Sequence (On and Off Events)
	 * 
	 * @param sequence
	 * @param resolution The minimum allowable division: (i.e. 64 reprents a 64th note)
	 * @return
	 */
	public static Sequence quantize(Sequence sequence, int resolution) {
		
		assert sequence.getDivisionType() != Sequence.PPQ : "Sequence Division Type must be in PPQ";
		
		int ppq = sequence.getResolution();
		int unitLength = (ppq * 4) / resolution;
		
		Track[] tracks = sequence.getTracks();
		
		for (Track track : tracks) {
		
			for (int i = 0; i < track.size(); i++) {
				
				MidiEvent event = track.get(i);
				
				if (isNoteOnEvent(event) || isNoteOffEvent(event)) {
					
					long position = event.getTick();
					long quotient = position / unitLength;
					long remainder = position % unitLength;
					long newPosition;
					
					if (remainder >= unitLength / 2) {
						
						newPosition = (quotient * unitLength) + unitLength;	
						
					} else {
						
						newPosition = quotient * unitLength;
						
					}
					
					event.setTick(newPosition);
	
				}				
			}		
		}
		
		return sequence;
		
	}
	
	public static boolean isNoteOnEvent(MidiEvent event) {
				
		if (event.getMessage() instanceof ShortMessage) {
			
			ShortMessage sm = (ShortMessage) event.getMessage();
			
			if (sm.getCommand() == NOTE_ON) {
				
				return true;
				
			}
			return false;	
		}	
		return false;		
	}
	
	public static boolean isNoteOffEvent(MidiEvent event) {
		
		if (event.getMessage() instanceof ShortMessage) {
			
			ShortMessage sm = (ShortMessage) event.getMessage();
			
			if (sm.getCommand() == NOTE_OFF) {
				
				return true;
				
			}
			return false;	
		}	
		return false;		
	}
	
	/** Creates a sequencer using the MidiSystem **/
	@SuppressWarnings("unused")
	private Sequencer createSeq() {
		
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			return sequencer;
		}
		catch (MidiUnavailableException e) {
			System.out.println("Sequencer Device not supported");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void printPairList(List<Note> pairList) {
		
		for (int i = 0; i < pairList.size(); i++) {
			
			Note pair = pairList.get(i);
			System.out.println("\nNote Pair " + i + ":\t" + "Key: " + pair.getKey());
			System.out.println("\t\tPosition: " + pair.getPosition());
			System.out.println("\t\tVelocity: " + pair.getVelocity());
			System.out.println("\t\tDuration: " + pair.getDuration());
			
		}
		
	}
	
	public static void printHistogram(Histogram histo) {
		
		Map<Long, List<List<Note>>> data = histo.getEventData();
		
		System.out.println("\nKEY\t        FREQUENCY BY BEAT     ");
		for (long key : data.keySet()) {
			List<List<Note>> beats = data.get(key);
			System.out.print(key + ": ");
			System.out.print("\t< ");
			for (int i = 0; i < beats.size(); i++) {
				List<Note> pairList = beats.get(i);
				if (i == beats.size()/2) {
					System.out.print("  ");
				} else if (i == beats.size()/4 || i == (beats.size()/4) * 3) {
					System.out.print("  ");
				}
				System.out.print(pairList.size() + " ");
			}
			System.out.println(">");
		}
		
	}
	
	public static String printArray(int[] array) {

		String arr = "< ";
		for (int i = 0; i < array.length; i++) {
			String s = array[i] + " ";
			arr += s;
		}
		arr += ">";
		return arr;
	}
	
	
	public static String printArray(double[] array) {

		String arr = "< ";
		for (int i = 0; i < array.length; i++) {
			String s = array[i] + " ";
			arr += s;
		}
		arr += ">";
		return arr;
	}
	
	
	// Maybe Assumes only one BAR?  VERIFY
	public static double[] getHomeFromMidiFile(String fileName, long key, int resolution) {
		
		// Using a Histogram object is due to the pre-built quantizing and midireading functionality
		Histogram histo = new Histogram(resolution);
		
		histo.addFile(fileName);
		
		double[] home = new double[histo.getResolution()];
		
		List<List<Note>> beats = histo.getEventData().get(key);
		
		for (int i = 0; i < beats.size(); i++) {
			
			List<Note> pairs = beats.get(i);
			home[i] = pairs.size();
			
		}
		
		return home;
	}
	
	// ONLY USES FIRST BAR OF ABLETON CLIP AND ASSUMES 4/4 TIMING
	public static double[] getHomeFromAbletonClip(String rawString, int key, int resolution) {
		
		double division = 4.0/resolution;

		double[] home = new double[resolution];
		
		ArrayList<Note> Notes = new ArrayList<Note>();
		
		String[] noteStrings = rawString.split("Note ");
		
		// Start at i = 1 because First noteStrings[0] is ""
		for (int i = 1; i < noteStrings.length; i++) {
			
			String[] properties = noteStrings[i].split(" ");
			
			long pitch = Long.parseLong(properties[0]);
			double position = Double.parseDouble(properties[1]);
			double duration = Double.parseDouble(properties[2]);
			long velocity = Long.parseLong(properties[3]);
			
			Note note = new Note(pitch, position, duration, velocity, Note.DivisionType.FRACTIONAL);
				
			Notes.add(note);
				
		}
		
		ArrayList<Note> quantized = quantize(Notes, resolution);
		
		for (Note note : quantized) {
			
			if (note.getKey() == key && note.getPosition() < 4) {

				home[(int) (note.getPosition()/division)] = 1.0;
				
			}
			
		}
		
		return home;
		
	}
		
	
		
	
	public static void main(String[] args) {
		
		ArrayList<Note> notes = new ArrayList<Note>();
		Note note = new Note(64, 1.3, .3, 100, Note.DivisionType.FRACTIONAL);
		notes.add(note);
		@SuppressWarnings("unused")
		ArrayList<Note> quantized = quantize(notes, 16);
		
		double[] home = getHomeFromAbletonClip("Note 72 2.7 .5 110 Note 72 1.2 .1 90", 72, 8);
		printArray(home);
		
		
	}
		
	
}
