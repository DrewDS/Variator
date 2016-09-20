package main;


public class Note {

	private long key;
	private double position;
	private double duration;
	private long velocity;
	private DivisionType divisionType;
	
	public enum DivisionType {FRACTIONAL, PPQ};
	
	/** Constructs Note explicityly from the necessary fields
	 * 	Because the division type is not known, it must be specified.
	 * 
	 *  (Can maybe introduce functionality to infer division type from input)
	 * 
	 * @param key
	 * @param position
	 * @param duration
	 * @param velocity
	 * @param divisionType
	 */
	public Note(long key, double position, double duration, long velocity, DivisionType divisionType) {
		this.key = key;
		this.position = position;
		this.duration = duration;
		this.velocity = velocity;
		this.divisionType = divisionType;

	}
	
	/** Constructs Note from NOTE ON and NOTE OFF
	 *  DivisionType is assumed to be PPQ becuse NoteMessage uses Sequence from javax.sound.midi
	 *  
	 * @param on	NOTE ON message
	 * @param off 	NOTE OFF message
	 */
	public Note(NoteMessage on, NoteMessage off) {
		
		if (on.isOn() && !off.isOn()) {
			
			key = on.getKey();
			velocity = on.getVelocity();
			position = on.getPosition();
			duration = off.getPosition() - on.getPosition();
			this.divisionType = DivisionType.PPQ;
			
		} else {
			
			throw new IllegalArgumentException("Must use NOTE ON and NOTE OFF to construct Pair");
			
		}
		
	}

	public long getKey() {
		return key;
	}

	public long getVelocity() {
		return velocity;
	}
	
	public void setPosition(double position) {
		this.position = position;
	}

	public double getPosition() {
		return position;
	}

	public double getDuration() {
		return duration;
	}
	
	public DivisionType getDivisionType() {
		return divisionType;
	}
	
	public void setDivisionType(DivisionType newDivisionType, long PPQ) {
		
		if (divisionType == newDivisionType) {	
			return;			
		}
		
		switch (newDivisionType) {
		
			case PPQ: 	
				position *= PPQ;
				break;
			case FRACTIONAL: 
				position /= PPQ;
				break;					
		}
		
		return;
		
	}

	
}
