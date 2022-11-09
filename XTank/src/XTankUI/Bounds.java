package XTankUI;

import Serializer.ObjectSerialize;

public class Bounds {
	private static Bounds bounds = null;
	private static XTankUI ui;
	
	public Bounds getInstance() {
		if (bounds == null) {
			bounds = new Bounds();
		}
		return bounds;
	}
	
	public void connect(XTankUI ui) {
		Bounds.ui = ui;
	}
	
	public boolean check(ObjectSerialize obj) {
		return false;
	}
}
