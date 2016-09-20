package main;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.*;


public class Histogram {

	// < KEY, < List of Beats containing Lists {Note Pairs} > > 
	private Map<Long, List<List<Note>>> eventData;
	private int resolution;
	


	public Histogram(int resolution) {
		
		this.resolution = resolution;
		eventData = new HashMap<Long, List<List<Note>>>();
		
	}
	
	public void addFile(String fileName) {
		
		assert fileName.endsWith(".mid");
		
		File midiFile = new File(fileName);
		Sequence sequence = Tools.sequenceFromFile(midiFile);
		addSequence(sequence);
		
	}
	
	public void addSequence(Sequence sequence) {
		
		MidiReader reader = new MidiReader(sequence, resolution);
		Map<Long, List<List<Note>>> newData = reader.getEventData();
		
		if (eventData.isEmpty()) {
			
			eventData = newData;
			return;
			
		}
		
		for (Long key : newData.keySet()) {
			
			List<List<Note>> newBeats = newData.get(key);
			
			// If key is not in Histogram
			if (!eventData.containsKey(key)) {
				
				// put Key in event Data containing the List of Beats (which contain lists of pairs)
				eventData.put(key, newBeats);
			
			// If key is in Histogram
			} else {
				
				// List of Beats already in Histogram for this key
				List<List<Note>> oldBeats = eventData.get(key);
				
				// Iterate through beats and add pairs to each
				for (int i = 0; i < newBeats.size(); i++) {
					
					List<Note> oldPairs = oldBeats.get(i);
					List<Note> newPairs = newBeats.get(i);
					
					oldPairs.addAll(newPairs);		
					
				}	
			}	
		}		
	}
	
	public void clearHistogram() {
		
		eventData.clear();
		
	}
	
	public Map<Long, List<List<Note>>> getEventData() {
		return this.eventData;
	}
	
	public int getResolution() {
		return resolution;
	}
	
	
	public static void main(String[] args) {
		
		Histogram histo = new Histogram(128);
		histo.addFile("beatTest1.mid");
		Tools.printHistogram(histo);
		histo.addFile("beatTest2.mid");
		Tools.printHistogram(histo);
		histo.addFile("pianoImprovTest.mid");
		Tools.printHistogram(histo);

		
		
	}
	
	
}
