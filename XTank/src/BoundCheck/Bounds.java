package BoundCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;

public class Bounds {
	private static Bounds bounds = null;
	private static int uiHeight;
	private static int uiWidth;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static ArrayList<ObjectSerialize> walls;
	private static TankCheck tCheck;
	private static BulletCheck bCheck;
	private int myId;
	
	public static Bounds getInstance() {
		if (bounds == null) {
			bounds = new Bounds();
		}
		tCheck = TankCheck.getInstance();
		bCheck = BulletCheck.getInstance();
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
		if (obj.name().equals("bull")) {
			bCheck.setObj(obj);
			bCheck.check();
			return bCheck.getObj();
		}
		return obj;
	}
	
	public void tanks(HashMap<Integer, ObjectSerialize> tanks) {
		Bounds.tanks = tanks;
		tCheck.setTanks(tanks);
		bCheck.setTanks(tanks);
	}
	
	public void walls(ArrayList<ObjectSerialize> walls) {
		Bounds.walls = walls;
		tCheck.setWalls(walls);
		bCheck.setWalls(walls);
	}
	
	public void setBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
		tCheck.setUIBounds(uiHeight, uiWidth);
		bCheck.setUIBounds(uiHeight, uiWidth);
	}
	
	public void setID(int id) {
		myId = id;
		tCheck.myID(id);
		bCheck.myID(id);
	}

}
