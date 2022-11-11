package BoundCheck;

import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;

public class Bounds {
	private static Bounds bounds = null;
	private static int uiHeight;
	private static int uiWidth;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static HashSet<ObjectSerialize> walls;
	private static TankCheck tCheck;
	
	public static Bounds getInstance() {
		if (bounds == null) {
			bounds = new Bounds();
		}
		tCheck = TankCheck.getInstance();
		return bounds;
	}
	
	public ObjectSerialize check(ObjectSerialize obj) {
		//System.out.println("Bound Checking: " + obj);
		if (obj.name().equals("Tank")) {
			tCheck.setObj(obj);
			tCheck.check();
			System.out.println("Returning Tank: " + obj);
			return tCheck.getObj();
		}
		return obj;
	}
	
	public void tanks(HashMap<Integer, ObjectSerialize> tanks) {
		Bounds.tanks = tanks;
	}
	
	public void walls(HashSet<ObjectSerialize> walls) {
		Bounds.walls = walls;
		tCheck.setWalls(walls);
	}
	
	public void setBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
		tCheck.setUIBounds(uiHeight, uiWidth);
	}
	

}
