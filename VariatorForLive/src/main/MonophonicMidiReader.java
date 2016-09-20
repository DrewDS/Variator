package main;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiEvent;


public class MonophonicMidiReader {

	private NoteMessage noteOnBuffer;
	private List<NoteMessage> noteOffBuffer;
	private List<MidiEvent> eventList;
	
	private int prematureNoteOffs = 0; // just for more information about the midi info
	
	public MonophonicMidiReader(List<MidiEvent> eventList) {
		
		this.eventList = eventList;
		noteOnBuffer = null;
		noteOffBuffer = new ArrayList<NoteMessage>();
		
	}
	
	public List<Note> makeNotes() {

		List<Note> notes = new ArrayList<Note>();
		
		for (MidiEvent event : eventList) {
			
			// Make note from each event
			NoteMessage currentNote = new NoteMessage(event);
			
			if (currentNote.isOn()) {
				
				if (noteOnBuffer == null) {
					
					noteOnBuffer = currentNote;
					
				} else {
					
					NoteMessage newNoteOff = new NoteMessage(false, currentNote.getKey(), currentNote.getPosition(), 0);
					Note newNote = new Note(currentNote, newNoteOff);
					notes.add(newNote);
					noteOffBuffer.add(newNoteOff);
					noteOnBuffer = currentNote;
					
				}				
			} 
			else if (currentNote.isOff()) {
				
				if (noteOffBuffer.isEmpty()) {
					
					// check for premature NOTE OFF (just for testing)
					if (noteOnBuffer == null) {
						
						prematureNoteOffs++;
						System.out.println(prematureNoteOffs);
						
					} else {
						
						Note newNote = new Note(noteOnBuffer, currentNote);
						notes.add(newNote);
						noteOnBuffer = null;
								
					}
					
				} else { 
					
					//remove the head of noteOffBuffer
					noteOffBuffer.remove(0);
					
				}
				
			} else {
				
				Tools.printMidiData(event);
				throw new IllegalArgumentException("MonophonicMidiReader can only process NOTE ON or OFF");
				
			}
			
		}
		// End iteration through MidiEvents
		
		// Check for floating NOTE ON and make it a pair with duration 100 ticks
		if (noteOnBuffer != null) {
			
			NoteMessage newNoteOff = new NoteMessage(false, noteOnBuffer.getKey(), noteOnBuffer.getPosition() + 100, 0);
			Note newNote = new Note(noteOnBuffer, newNoteOff);
			notes.add(newNote);
			
		}
	
		return notes;		
	}
	
	
	
}
