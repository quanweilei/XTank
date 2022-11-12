package BoundCheck;

import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;

public class BulletCheck implements BoundCalc{

	private static BulletCheck tCheck = null;
	private static ObjectSerialize obj;
	private static HashMap<Integer, ObjectSerialize> bullets;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static HashSet<ObjectSerialize> walls;
	private static int uiHeight;
	private static int uiWidth;
	
	public static BulletCheck getInstance() {
		if (tCheck == null) {
			tCheck = new BulletCheck();
		}
		return tCheck;
	}
	
	@Override
	public void setObj(ObjectSerialize obj) {
		BulletCheck.obj = obj;
	}

	@Override
	public ObjectSerialize getObj() {
		return obj;
	}

	@Override
	public void check() {
		// First check if bullet is going out of bounds
		ObjectSerialize curr = obj;
		int currx = curr.x();
		int curry = curr.y();
		int cDirX = curr.dirX();
		int cDirY = curr.dirY();
		int cWidth = curr.width();
		int cHeight = curr.height();
		int midX = ((2 * currx + cWidth)/2);
		int midY = ((2 * curry + cHeight)/2);
		
		int bulltX = midX + cDirX*7;
		int bulltY = midY + cDirY*7;
		
		// If bullet goes through left side
		if (bulltX < 0) {
			obj.setStatus(0);
			return;
		}
		
		// If bullet goes through right side
		if (bulltX + 10 > uiWidth) {
			obj.setStatus(0);
			return;
		}
		
		// If bullet goes through top
		if (bulltY < 0) {
			obj.setStatus(0);
			return;
		}
		
		// If bullet goes through bottom
		if (bulltY + 10 > uiHeight) {
			obj.setStatus(0);
			return;
		}
		
		
		
		System.out.println("Bound Checking Bullet: " + obj);
	}

	@Override
	public void setUIBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
	}

	@Override
	public void setWalls(HashSet<ObjectSerialize> walls) {
		BulletCheck.walls = walls;
	}

	@Override
	public void setTarget(HashMap<Integer, ObjectSerialize> bullets) {
		BulletCheck.bullets = bullets;
	}

	@Override
	public void setTanks(HashMap<Integer, ObjectSerialize> tanks) {
		// TODO Auto-generated method stub
		
	}

}
