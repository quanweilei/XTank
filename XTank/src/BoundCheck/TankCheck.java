package BoundCheck;

import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;
/*
 * Quanwei Lei
 * TankCheck checks the bounds of the tank and whether or not it is touching the border or another tank
 */
public class TankCheck implements BoundCalc{
	private static TankCheck tCheck = null;
	private static ObjectSerialize obj;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static HashSet<ObjectSerialize> walls;
	private static int uiHeight;
	private static int uiWidth;
	
	public static TankCheck getInstance() {
		if (tCheck == null) {
			tCheck = new TankCheck();
		}
		return tCheck;
	}
	
	@Override
	public void setObj(ObjectSerialize obj) {
		TankCheck.obj = obj;
	}

	@Override
	public ObjectSerialize getObj() {
		return obj;
	}

	@Override
	public void check() {
		// First check if tank is going out of bounds
		ObjectSerialize curr = obj;
		int currx = curr.x();
		int curry = curr.y();
		int cDirX = curr.dirX();
		int cDirY = curr.dirY();
		int cWidth = curr.width();
		int cHeight = curr.height();
		int midX = ((2 * currx + cWidth)/2);
		int midY = ((2 * curry + cHeight)/2);
		
		int gunX = midX + cDirX*7;
		int gunY = midY + cDirY*7;
		
		// If gun goes through left side
		if (gunX < 0) {
			obj.setXY((((-cDirX*14) - cWidth)/2), curry);
			return;
		}
		
		if (gunX > uiWidth) {
			obj.setXY(((2*(uiWidth-cDirX*7) - cWidth)/2), curry);
			return;
		}
		
		if (gunY < 0) {
			obj.setXY(gunX, gunY);
			return;
		}
		
		
		
		System.out.println("Bound Checking Tank: " + obj);
	}

	@Override
	public void setUIBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
	}

	@Override
	public void setWalls(HashSet<ObjectSerialize> walls) {
		TankCheck.walls = walls;
	}

	@Override
	public void setTanks(HashMap<Integer, ObjectSerialize> tanks) {
		TankCheck.tanks = tanks;
	}

}
