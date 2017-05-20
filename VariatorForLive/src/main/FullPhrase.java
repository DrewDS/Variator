package main;

import java.util.ArrayList;

/**
 * Highest Level Object for containing and manipulating information necessary to the
 * creation of new variations.
 * 
 * Contains multi-bar phrases of "bases", "homes", and "velocitymaps" for every drum named,
 * in addition to an associated phrase of updating variators for each.
 * 
 * @author drewdyer-symonds
 *
 */

public class FullPhrase {

	private BasisPhrase basisPhrase;
	private HomePhrase homePhrase;
	private VelocityPhrase velocityPhrase;
	private ArrayList<FullVariator> fullVariatorPhrase;
	int resolution;
	int length;
	
	private enum PhraseType {HOME_PHRASE, BASIS_PHRASE, VELOCITY_PHRASE}
	
	/* FullPhrase must be instantiated with a preset!  The default preset is fine
	 * 
	 * 
	public FullPhrase(int resolution, int numOfBars) {
		this.length = numOfBars;
		this.resolution = resolution;
		
		initializeFullVariators();
		
		basisPhrase = new BasisPhrase(resolution, numOfBars);
		homePhrase = new HomePhrase(resolution, numOfBars);
		velocityPhrase = new VelocityPhrase(resolution, numOfBars);
		
		updateAllVariators();
		
	}*/
	
	/**
	 * Constructor for FullPhrase.  
	 * 
	 * Initializes resolution (all other data structures take their resolution from this) so
	 * that simple, fixed-length, array objects can be used.
	 * 
	 * Intialiasizes and updates sub-phrases. 
	 * 
	 * The sub-phrases act as conatainers for their own data. When any one of them is
	 * updated, updatePhrase() is called which updates the fullVariatorPhrase.
	 * 
	 * fullVariatorPhrase is what actually produces the variations, and so must 
	 * continuously reflect changes made to the sub-phrases.
	 * 
	 * @param resolution
	 * @param numOfBars
	 * @param presetName
	 */
	public FullPhrase(int resolution, int numOfBars, String presetName) {
		this.length = numOfBars;
		this.resolution = resolution;
		
		initializeFullVariatorPhrase();
		
		VariatorObject.post("FullVariators initialized (3 arg constructor)");
		
		basisPhrase = new BasisPhrase(resolution, numOfBars, presetName);
		homePhrase = new HomePhrase(resolution, numOfBars); // Does not contain drum assignments on initialization
		velocityPhrase = new VelocityPhrase(resolution, numOfBars, presetName);
		
		updateAllVariators();
		
	}
	
	/**
	 * Constructor that allows for a pattern to be chosen, 
	 * representing different iterations of a fullPhrase.
	 * (for example a different basis on bar 2 of a 4-bar phrase, with {0,2,0,0})
	 * 
	 * @param resolution
	 * @param numOfBars
	 * @param presetName
	 * @param pattern
	 */
	public FullPhrase(int resolution, int numOfBars, String presetName, int[] pattern) {
		this.length = numOfBars;
		this.resolution = resolution;
		
		initializeFullVariatorPhrase();
		
		VariatorObject.post("FullVariators initialized (4 arg constructor)");
		
		basisPhrase = new BasisPhrase(resolution, numOfBars, presetName, pattern);
		homePhrase = new HomePhrase(resolution, numOfBars, presetName, pattern);
		velocityPhrase = new VelocityPhrase(resolution, numOfBars, presetName, pattern);
		
		
		updateAllVariators();
		
	}
	
	// =============== METHODS ================ //
	
	/**
	 * Primary method for creating a variation. This method cascades through the enclosed
	 * objects, eventually arriving at the correct single-pitch, single-bar variator requested.
	 * 
	 * @param density
	 * @param drumName
	 * @param velocityFactor
	 * @param barNum
	 * @return
	 */
	public double[] makeVariation(int density, String drumName, double velocityFactor, int barNum) {
		
		FullVariator fullVariator = fullVariatorPhrase.get(barNum);
		return fullVariator.makeVariation(density, drumName, velocityFactor);
		
	}

	
	/**
	 * Sets the basisPhrase from a preset and updates the appropriate FullVariators
	 * 
	 * @param presetName
	 */
	public void setBasisFromPreset(String presetName) {
		
		basisPhrase.setPreset(presetName);
		updatePhrase(PhraseType.BASIS_PHRASE);
		
	}
	
	/**
	 * Sets the velocityPhrase from a preset and updates the appropriate FullVariators
	 * 
	 * @param presetName
	 */
	public void setVelocityMapFromPreset(String presetName) {
		
		velocityPhrase.setPreset(presetName);
		updatePhrase(PhraseType.VELOCITY_PHRASE);
		
	}
	
	/**
	 * Sets the homePhrase from a preset and updates the appropriate FullVariator.
	 * (This is unlikely to be used until Presets.xml is populated from actual saved clips)
	 * 
	 * @param presetName
	 */
	public void setHomeFromPreset(String presetName) {
		
		homePhrase.setPreset(presetName);
		updatePhrase(PhraseType.HOME_PHRASE);
		
	}
	
	/**
	 * Adds the data contained in the selected Ableton Clip (rawData) to the homePhrase
	 * 
	 * @param drumName
	 * @param rawData
	 * @param pitch
	 * @param sourceType
	 */
	public void addDrumHomeFromAbleton(String drumName, String rawData, long pitch, HomeSource sourceType) {
		
		for (int bar = 0; bar < length; bar++) {
			
			FullHome fullHome = (FullHome) homePhrase.getBar(bar);
			fullHome.addDrumHome(drumName, rawData, pitch, HomeSource.ABLETON_CLIP, bar);
			
			updatePhrase(PhraseType.HOME_PHRASE);
			
		}
	}
	
	/**
	 * Updates fullVariatorPhrase for the PhraseType, type.
	 * 
	 * This method must be called every time a change is made to one of the container
	 * sub-phrases, or else they will fall out of sync.
	 * 
	 * 
	 * @param type
	 */
	private void updatePhrase(PhraseType type) {
		
		switch(type) { 
		
		case BASIS_PHRASE: for (int i = 0; i < length; i++) {
								fullVariatorPhrase.get(i).setFullBasis((FullBasis) basisPhrase.getBar(i));
							}
		case HOME_PHRASE:  for (int i = 0; i < length; i++) {
								fullVariatorPhrase.get(i).setFullHome((FullHome) homePhrase.getBar(i)); 
							}
		case VELOCITY_PHRASE: for (int i = 0; i < length; i++) {
								fullVariatorPhrase.get(i).setVelocityMap((VelocityMap) velocityPhrase.getBar(i));
							}	
		}		
	}
	
	/**
	 * Updates fullVariatorPhrase for every sub-phrase
	 */
	private void updateAllVariators() {
		updatePhrase(PhraseType.BASIS_PHRASE);
		updatePhrase(PhraseType.HOME_PHRASE);
		updatePhrase(PhraseType.VELOCITY_PHRASE);
	}
	
	/**
	 * Populates every bar of fullVariatorPhrase with a new, empty FullVariator
	 * of the appropriate resolution.
	 */
	private void initializeFullVariatorPhrase() {
		
		fullVariatorPhrase = new ArrayList<FullVariator>(length);
		
		for (int i = 0; i < length; i++) {
			
			//VariatorObject.post("Setting FullVariator at Bar " + i + " ...");
			fullVariatorPhrase.add(new FullVariator(resolution));
			
		}	
	}
	
	/**
	 * Returns the list of names for the drums contained in basis. 
	 * This moves up for use in the Max4Live user interface when
	 * user needs to assign drumNames to pitches
	 * 
	 * @return
	 */
	public ArrayList<String> getBasisDrums() {
		
		return basisPhrase.getBasisDrums();
		
	}
	
	// Method is for debugging
	// Copy constructor is used for safety
	public ArrayList<FullVariator> getFullVariators() {
		
		ArrayList<FullVariator> fVList = new ArrayList<FullVariator>(fullVariatorPhrase);
		return fVList;
		
	}
	
}
