package BoundCheck;

import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;

public class BulletCheck implements BoundCalc{

	private static TankCheck tCheck = null;
	private static ObjectSerialize obj;
	private static HashMap<Integer, ObjectSerialize> bullets;
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
		
		int gunX = midX + cDirX*7;
		int gunY = midY + cDirY*7;
		
		// If gun goes through left side
		if (gunX < 0) {
			obj.setXY((((-cDirX*14) - cWidth)/2), curry);
			return;
		}
		
		// If gun goes through right side
		if (gunX > uiWidth) {
			obj.setXY(((2*(uiWidth-cDirX*7) - cWidth)/2), curry);
			return;
		}
		
		// If gun goes through top
		if (gunY < 0) {
			obj.setXY(gunX - 25, (((-cDirY*14) - cHeight)/2));
			return;
		}
		
		if (gunY > uiHeight) {
			obj.setXY(gunX - 25, ((2*(uiHeight-cDirY*7) - cHeight)/2));
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
