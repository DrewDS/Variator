package main;

import java.util.ArrayList;

/**
 * Abstract class for Phrase object.  Sub-phrases of FullPhrase extend this.
 * 
 * Allows for multi-bar implementation of DataMap subclasses as well as the selection 
 * and storage of different types of a sub-phrase within a preset.
 * 
 * @author drewdyer-symonds
 *
 */
public abstract class Phrase {
	
	
	// *** Note: "bartype number" refers to the different types of a basis or velocity map that can exist 
	//           within a single preset in the presets resource (eg. rock bartype number 1 could represent a fill bar)
	
	protected int displayRes;
	protected String presetName;
	protected int length;
	protected int[] pattern;	//  A repeating pattern of barype numbers
	protected ArrayList<Integer> arrangement;  //  The result of all repetitions of pattern
	protected ArrayList<DataMap> bars;

	
	/**
	 * Constructor initializes an empty Phrase with no preset
	 * 
	 * @param displayRes
	 * @param numOfBars
	 */
	public Phrase(int displayRes, int numOfBars) {
		
		this.displayRes = displayRes;
		this.length = numOfBars;
		
		initArrangement();

		bars = new ArrayList<DataMap>(length);
		
		populateBars();
		
	}
	
	/**
	 * 3-arg Constructor creates default Phrase, where each bar is 
	 * the default bartype (i.e. 0) of the preset
	 *  
	 * @param displayRes
	 * @param numOfBars
	 * @param presetName
	 */
	public Phrase(int displayRes, int numOfBars, String presetName) {
		
		this.displayRes = displayRes;
		this.presetName = presetName;
		length = numOfBars;
		
		initArrangement();	

		bars = new ArrayList<DataMap>(numOfBars);
		
		
		populateBars();
		
	}
	
	/**
	 * 4-arg Constructor creates default Phrase where each bar is selected according
	 * the bar-type number.  
	 * 
	 * -- Illegal Argument exceptions are handled in PresetReader.
	 * 
	 * @param displayRes
	 * @param numOfBars
	 * @param presetName
	 * @param pattern
	 */
	public Phrase(int displayRes, int numOfBars, String presetName, int[] pattern) {
		
		this.displayRes = displayRes;
		this.presetName = presetName;
		length = numOfBars;
		
		initArrangement();
		
		setPattern(pattern);
		
		bars = new ArrayList<DataMap>(numOfBars);
		
		populateBars();
		
	}
	
	/**
	 * Creates the multi-bar arrangement to be later filled with "pattern"
	 */
	private void initArrangement() {
		
		assert length != 0;
		
		arrangement = new ArrayList<Integer>(length);
		
		for (int i = 0; i < length; i++) {
			
			arrangement.add(0);
			
		}
		
	}
	
	
	public void setPattern(int[] pattern) {
		this.pattern = pattern;	
		arrangePattern();
		populateBars();
	}
	
	
	
	/**
	 * Sets arrangment by repeating the input pattern 
	 * 
	 * If the pattern length is less than the phrase length, then  it will loop until the phrase is done
	 *  The beginning of the pattern will repeat if it does not evenly divide.
	 *  
	 *  Might eventually want to change this so that the end of the pattern finishes the phrase
	 */
	public void arrangePattern() {
		
		for (int i = 0; i < arrangement.size(); i += pattern.length) {		
			for (int j = 0; j < pattern.length; j++) {			
				arrangement.set(i, pattern[j]);				
			}			
		}	
	}
	
	/**
	 * Fills the ArrayList<DataMap>, bars, with bars of the subtype of 
	 * DataMap that is required (i.e. FullBasis or VelocityMap)
	 */
	abstract void populateBars();
	
	public void setPreset(String presetName) {
		this.presetName = presetName;
		VariatorObject.post("Setting preset (" + presetName + ")...");
		populateBars();
	}
	
	public void setLength(int length) {
		this.length = length;
		arrangement = new ArrayList<Integer>(length);
		arrangePattern();
		bars = new ArrayList<DataMap>(length);
	}
	
	public DataMap getBar(int barNum) {
		return bars.get(barNum);
	}

	// NOTES FOR FUTURE DEVELOPMENT:
	// Because this class is somewhat pivotal in the object
	// structure of the entire package, future developments should make
	// sure the following considerations are carefully addressed.
	
	/* WHAT CAN CHANGE ABOUT THIS CLASS?
	 * 
	 * - pattern? YES: Recreate arrangement and repopulate bars
	 * - length? YES: Recreate arrangement and repopulate bars
	 * - presetName? YES: Repopulate bars.  (If the bartype numbers from the pattern don't exist in the preset,
	 * 					 then preset reader should account for this and return the default
	 * 	 				 bartype number for that preset - This functionality exists, but double-check)
	 * 
	 * - arrangement? NO: This should only be changed through pattern (unless the repeat pattern algo becomes more complex)
	 * - displayRes? NO: This needs to be initialized with the Phrase itself, and most probably on the highest level in this
	 * 					 application that is not directly linked to Max4Live (currently that is FullVariator.java)
	 * - bars? NO: This should only be populated internally
	 * 
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
