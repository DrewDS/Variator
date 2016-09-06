/* VariatorObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
import java.util.ArrayList;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;

public class VariatorObject extends MaxObject
{
    private String homeString = "";
    private static double DEFAULT_VELOCITY = 100.0;
    private static int DISPLAY_RES = 16;
    private long pitch;
    private String rawAbletonClipData = "";
    private FullVariator fullVariator = new FullVariator(DISPLAY_RES);
    
    public VariatorObject() {
	declareIO(2, 2);
	BasisPresets.init(DISPLAY_RES);
    }
    
    public void inlet(int density) {
	if (getInlet() == 0) {
	    Variator testVariator = Variator.getDefaultKickVariator();
	    int[] testVariation = testVariator.makeVariation(density);
	    Atom[] formatted = formatForJSPort(testVariation, (double) pitch);
	    outlet(1, formatted);
	}
    }
    
    public void addDrumHome(String drumName) {
	fullVariator.addDrumHomeFromAbleton(drumName, rawAbletonClipData,
					    pitch, HomeSource.ABLETON_CLIP);
    }
    
    public void setHome(Atom[] args) {
	String noteData = "";
	Atom[] atoms;
	int i = (atoms = args).length;
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    Atom a = atoms[i_0_];
	    String s = new StringBuilder(a.toString()).append(" ").toString();
	    noteData = new StringBuilder(noteData).append(s).toString();
	}
	homeString = noteData;
    }
    
    public void setRawData(Atom[] args) {
	String noteData = "";
	Atom[] atoms;
	int i = (atoms = args).length;
	for (int i_1_ = 0; i_1_ < i; i_1_++) {
	    Atom a = atoms[i_1_];
	    String s = new StringBuilder(a.toString()).append(" ").toString();
	    noteData = new StringBuilder(noteData).append(s).toString();
	}
	rawAbletonClipData = noteData;
    }
    
    public void bang() {
	Variator testVariator = Variator.getDefaultKickVariator();
	testVariator.setHome(homeString, 60);
	post("-----------------------");
	post("New Home: ");
	postVariatorInfo(testVariator);
	int[] testVariation = testVariator.makeVariation(2);
	Atom[] formatted = formatForJSPort(testVariation, 60.0);
	outlet(0, "Bang");
	outlet(1, formatted);
    }
    
    public void setBasis(String basis) {
	BasisPresets.getPreset("rock");
	fullVariator.setBasis(BasisPresets.getPreset("rock"));
    }
    
    public void getBasisDrums() {
	ArrayList basisDrums = fullVariator.getBasisDrums();
	Atom[] formatted = new Atom[basisDrums.size()];
	for (int i = 0; i < basisDrums.size(); i++)
	    formatted[i] = Atom.newAtom((String) basisDrums.get(i));
	outlet(1, formatted);
    }
    
    private int[] matrixFormat(int[] input, int row) {
	int[] output = new int[input.length * 3];
	for (int i = 0; i < input.length; i++) {
	    output[i * 3] = i;
	    output[i * 3 + 1] = row;
	    output[i * 3 + 2] = input[i];
	}
	return output;
    }
    
    private Atom[] formatForJSPort(int[] variation, double key) {
	String signature = "writeVariation";
	Atom[] maxMessage = new Atom[2];
	double resolution = (double) variation.length;
	String noteData
	    = new StringBuilder("Notes ").append(String.valueOf(key)).append
		  (" ").toString();
	int density = 0;
	for (int i = 0; i < variation.length; i++) {
	    if (variation[i] == 1) {
		double position = (double) (4 * i) / resolution;
		double duration = 1.0 / resolution;
		int muted = 0;
		String data = new StringBuilder("Note ").append
				  (String.valueOf(pitch)).append
				  (" ").append
				  (String.valueOf(position)).append
				  (" ").append
				  (String.valueOf(duration)).append
				  (" ").append
				  (String.valueOf(DEFAULT_VELOCITY)).append
				  (" ").append
				  (String.valueOf(muted)).append
				  (" ").toString();
		noteData = new StringBuilder(noteData).append(data).toString();
	    }
	}
	Atom call = Atom.newAtom(signature);
	Atom arg = Atom.newAtom(noteData);
	maxMessage[0] = call;
	maxMessage[1] = arg;
	return maxMessage;
    }
    
    private static void postVariatorInfo(Variator v) {
	String home = "";
	for (int i = 0; i < v.getHome().length; i++)
	    home = new StringBuilder(home).append(v.getHome()[i]).append
		       (" ").toString();
	post(new StringBuilder("HOME: < ").append(home).append(">")
		 .toString());
	String basis = "";
	for (int i = 0; i < v.getBasis().length; i++)
	    basis = new StringBuilder(basis).append(v.getBasis()[i]).append
			(" ").toString();
	post(new StringBuilder("Basis: < ").append(basis).append(">")
		 .toString());
	String svar = "";
	int[] variation = v.makeVariation(2);
	for (int i = 0; i < v.getBasis().length; i++)
	    svar = new StringBuilder(svar).append(variation[i]).append(" ")
		       .toString();
	post(new StringBuilder("VARIATON: < ").append(svar).append(">")
		 .toString());
    }
    
    public void setPitch(long pitch) {
	this.pitch = pitch;
    }
}
