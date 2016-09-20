package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PresetReader {

	private static String PRESET_FILE = "/PresetsTest.xml";
	
	
	public static LinkedHashMap<String, double[]> getData(String mapType, String targetName) {
		
		LinkedHashMap<String, double[]> data = new LinkedHashMap<String, double[]>();
		
		try {
			
			// 
			InputStream inputStream = PresetReader.class.getResourceAsStream(PRESET_FILE);
			//File fXmlFile = new File("/Users/drewdyer-symonds/git/Variator/VariatorForLive/src/PresetsTest.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputStream);
			
			doc.getDocumentElement().normalize();
			
			// Gets the root node for the preset list (i.e. "presets")
			System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
					
			// Node List of all elements that match the MAP TYPE 
			// (i.e basis, home, or velocity)
			NodeList nList = doc.getElementsByTagName(mapType);
			
			System.out.println("---------------------------");
			
			// Iterate through the node list (There might 
			// only be only one element)
			for (int i = 0; i < nList.getLength(); i++) {
				
				Node nNode = nList.item(i);
				
				System.out.println("\nDataMap Type: " + nNode.getNodeName());
				
				// Node List of PRESETS (i.e. rock, funk, etc)
				NodeList nPresets = nNode.getChildNodes();
				
				// Iterate through PRESETS
				for (int preset = 0; preset < nPresets.getLength(); preset++) {
					
					Node presetNode = nPresets.item(preset);
					
					// conditional eleminates white space
					if (presetNode.getNodeType() == Node.ELEMENT_NODE) {
						
						// represents a PRESET node of element type
						// (i.e. rock, funk, etc)
						Element ePresetElement = (Element) presetNode;
					
						// Get the name of the current PRESET in the iteration (i.e. rock)
						System.out.println("\n\tPreset: " + ePresetElement.getNodeName() + "\n");
						String currentName = ePresetElement.getNodeName();
						
						// match to desired PRESET
						if (currentName.equals(targetName)) {
							
							// Node List of DRUMS within that preset
							NodeList nDrumList = presetNode.getChildNodes();
							
							// Iterate through the DRUMS of each preset (i.e. kick, snare, etc)
							for (int drum = 0; drum < nDrumList.getLength(); drum++) {

								// Represents single DRUM with drum node list
								Node nDrumNode = nDrumList.item(drum);

								if (nDrumNode.getNodeType() == Node.ELEMENT_NODE) {
									
									Element eElement = (Element) nDrumNode;
									
									String drumName = eElement.getNodeName();
									String sDrumData = eElement.getTextContent();
									System.out.println(
											"\t\t" + drumName + ": " + sDrumData);
									
									String[] stringArrDrumData = sDrumData.split(",");
									int length = stringArrDrumData.length;
									double[] drumData = new double[length];
									for (int num = 0; num < length; num++) {
										drumData[num] = Double.valueOf(stringArrDrumData[num]);
									}
									data.put(drumName, drumData);
								}
							} 
						}
					}			
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return data;
		
	}
	

	public static void main(String[] args) {
		getData("basis", "rock");
		System.out.println(Tools.printArray(PresetReader.getData("basis", "rock").get("hhClosed")));
	}
	
}
