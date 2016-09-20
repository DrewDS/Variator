package main;
import javax.sound.midi.*;


public class NoteMessage {

	private long key;
	private long position;
	private long velocity;
	private boolean on;
	
	public static int NOTE_ON = 0x90;
	public static int NOTE_OFF = 0x80;
	
	/** Constructs Note (can be either On or Off) from a MidiEvent **/
	public NoteMessage(MidiEvent event) {
		
		position = event.getTick();
		
		MidiMessage message = event.getMessage();
		
		if (message instanceof ShortMessage) {
			
			ShortMessage sm = (ShortMessage) message;
			
			key = sm.getData1();
			
			if (sm.getCommand() == NOTE_ON) {
				on = true;
				velocity = sm.getData2();
			} 
			else if (sm.getCommand() == NOTE_OFF) {
				on = false;
				velocity = 0;
			} 
			else {
				Exception e = new Exception("Only note off and note on message supported");
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * Explicitly construct Note by parameters.
	 * 
	 * @param on
	 * @param key
	 * @param position
	 * @param velocity
	 */
	public NoteMessage(boolean on, long key, long position, long velocity) { 
		this.on = on;
		this.key = key;
		this.position = position;
		this.velocity = velocity;
	}
	
	public boolean isSameKey(NoteMessage newNote) {
		if (this.getKey() == newNote.getKey()) {
			return true;
		} else {
			return false;
		}
	}

	public long getKey() {
		return key;
	}

	public long getPosition() {
		return position;
	}

	public long getVelocity() {
		return velocity;
	}

	public boolean isOn() {
		return on;
	}
	
	public boolean isOff() {
		return !isOn();
	}
	
	public static void main(String[] args) {
		// Nothing here yet
	}
	
}
