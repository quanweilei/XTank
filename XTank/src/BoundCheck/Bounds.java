package BoundCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;
/*
 * Quanwei Lei
 * Class Bounds is used to check any object for bound violation, or hitmarker
 */
public class Bounds {
	private static Bounds bounds = null;
	private static int uiHeight;
	private static int uiWidth;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static ArrayList<ObjectSerialize> walls;
	private static TankCheck tCheck;
	private static BulletCheck bCheck;
	private int myId;
	
	// returns an instance of bounds
	public static Bounds getInstance() {
		if (bounds == null) {
			bounds = new Bounds();
		}
		tCheck = TankCheck.getInstance();
		bCheck = BulletCheck.getInstance();
		return bounds;
	}
	
	// checks the given object, returns
	public ObjectSerialize check(ObjectSerialize obj) {
		//System.out.println("Bound Checking: " + obj);
		if (obj.name().equals("Tank")) {
			tCheck.setObj(obj);
			tCheck.check();
			System.out.println("Returning Tank: " + obj);
			return tCheck.getObj();
		}
		if (obj.name().equals("bull")) {
			bCheck.setObj(obj);
			bCheck.check();
			return bCheck.getObj();
		}
		return obj;
	}
	
	// sets tanks of bounds
	public void tanks(HashMap<Integer, ObjectSerialize> tanks) {
		Bounds.tanks = tanks;
		tCheck.setTanks(tanks);
		bCheck.setTanks(tanks);
	}
	
	// sets world walls
	public void walls(ArrayList<ObjectSerialize> walls) {
		Bounds.walls = walls;
		tCheck.setWalls(walls);
		bCheck.setWalls(walls);
	}
	
	// sets world bounds
	public void setBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
		tCheck.setUIBounds(uiHeight, uiWidth);
		bCheck.setUIBounds(uiHeight, uiWidth);
	}
	
	// sets id
	public void setID(int id) {
		myId = id;
		tCheck.myID(id);
		bCheck.myID(id);
	}

}
