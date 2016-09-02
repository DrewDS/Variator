
public enum KeyValues {

	C_ONE(36, "C1"),
	C_SHARP_ONE(37, "C#1"),
	D_ONE(38, "D1"),
	D_SHARP_ONE(39, "D#1"),
	E_ONE(40, "E1"),
	F_ONE(41, "F1"),
	F_SHARP_ONE(42, "F#1"),
	G_ONE(43, "G1"),
	G_SHARP_ONE(44, "G#1"),
	A_ONE(45, "A1"),
	A_SHARP_ONE(46, "A#1"),
	B_ONE(47, "B1"),
	
	C_TWO(48, "C2"),
	C_SHARP_TWO(49, "C#2"),
	D_TWO(50, "D2"),
	D_SHARP_TWO(51, "D#2"),
	E_TWO(52, "E2"),
	F_TWO(53, "F2"),
	F_SHARP_TWO(54, "F#2"),
	G_TWO(55, "G2"),
	G_SHARP_TWO(56, "G#2"),
	A_TWO(57, "A2"),
	A_SHARP_TWO(58, "A#2"),
	B_TWO(59, "B2"),
	
	C_THREE(60, "C3"),
	C_SHARP_THREE(61, "C#3"),
	D_THREE(62, "D3"),
	D_SHARP_THREE(63, "D#3"),
	E_THREE(64, "E3"),
	F_THREE(65, "F3"),
	F_SHARP_THREE(66, "F#3"),
	G_THREE(67, "G3"),
	G_SHARP_THREE(68, "G#3"),
	A_THREE(69, "A3"),
	A_SHARP_THREE(70, "A#3"),
	B_THREE(71, "B3"),
	
	C_FOUR(72, "C4"),
	C_SHARP_FOUR(73, "C#4"),
	D_FOUR(74, "D4"),
	D_SHARP_FOUR(75, "D#4"),
	E_FOUR(76, "E4"),
	F_FOUR(77, "F4"),
	F_SHARP_FOUR(78, "F#4"),
	G_FOUR(79, "G4"),
	G_SHARP_FOUR(80, "G#4"),
	A_FOUR(81, "A4"),
	A_SHARP_FOUR(82, "A#4"),
	B_FOUR(83, "B4"),
	
	C_FIVE(84, "C5"),
	C_SHARP_FIVE(85, "C#5"),
	D_FIVE(86, "D5"),
	D_SHARP_FIVE(87, "D#5"),
	E_FIVE(88, "E5"),
	F_FIVE(89, "F5"),
	F_SHARP_FIVE(90, "F#5"),
	G_FIVE(91, "G5"),
	G_SHARP_FIVE(92, "G#5"),
	A_FIVE(93, "A5"),
	A_SHARP_FIVE(94, "A#5"),
	B_FIVE(95, "B5");
	
	private int value;
	private String name;
	
	private KeyValues(int value, String name) {
		
		this.value = value;
		this.name = name;
		
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(C_ONE.getName());
		System.out.println(A_SHARP_ONE.getValue());
		
	}
	
}
