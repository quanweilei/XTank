package BoundCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.graphics.Rectangle;

import Serializer.ObjectSerialize;

/*
 * Quanwei Lei
 * BulletCheck checks if a bullet still exists, as it hits walls or tanks or borders
 */
public class BulletCheck implements BoundCalc{

	private static BulletCheck tCheck = null;
	private static ObjectSerialize obj;
	private static HashMap<Integer, ObjectSerialize> bullets;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static ArrayList<Rectangle> wallBounds;
	private static ArrayList<ObjectSerialize> walls;
	private static int uiHeight;
	private static int uiWidth;
	private static int id;
	
	// returns an instance of bulletcheck
	public static BulletCheck getInstance() {
		if (tCheck == null) {
			tCheck = new BulletCheck();
		}
		return tCheck;
	}
	
	// sets object to a given object for checking
	@Override
	public void setObj(ObjectSerialize obj) {
		BulletCheck.obj = obj;
	}
	
	// returns the object
	@Override
	public ObjectSerialize getObj() {
		return obj;
	}
	
	// checks the given object, and then sets the status if it exists still
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
		}
		
		// If bullet goes through right side
		if (bulltX > uiWidth) {
			obj.setStatus(0);
		}
		
		// If bullet goes through top
		if (bulltY < 0) {
			obj.setStatus(0);
		}
		
		// If bullet goes through bottom
		if (bulltY > uiHeight) {
			obj.setStatus(0);
		}
		Rectangle bullet = new Rectangle(bulltX, bulltY, 10,10);
		// Wall Check
		for (Rectangle r: wallBounds) {
			if (bullet.intersects(r)) {
				obj.setStatus(0);
				return;
			}
		}
		
		// Check directly if my tank was hit
		for (Integer k: tanks.keySet()) {
			ObjectSerialize cTank = tanks.get(k);
			if (cTank != null) {
				Rectangle cTankR = new Rectangle(cTank.x(), cTank.y(), cTank.width(), cTank.height());
				if (cTankR.intersects(bullet)) {
					if (cTank.id() == id) {
						System.out.println("I got hit");
						obj.setStatus(-1);
					}
					else {
						System.out.println("Other player got hit");
						obj.setStatus(0);
					}
				}
			}
			
		}


	}
	
	// sets the ui bounds 
	@Override
	public void setUIBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
	}
	
	// sets the walls, also initiates the walls as rectangles for bound check
	@Override
	public void setWalls(ArrayList<ObjectSerialize> walls) {
		BulletCheck.walls = walls;
		wallBounds = new ArrayList<>();
		for (ObjectSerialize w: walls) {
			if (w.x() == w.dirX()) {
				wallBounds.add(new Rectangle(w.x(), Math.min(w.y(), w.dirY()), 10, Math.max(w.y(), w.dirY()) - Math.min(w.y(), w.dirY())));
			}
			if (w.y() == w.dirY()) {
				wallBounds.add(new Rectangle(Math.min(w.x(), w.dirX()), w.y(), Math.max(w.x(), w.dirX()) - Math.min(w.x(), w.dirX()), 10));
			}
		}
		
	}
	
	// sets the target to bullets
	@Override
	public void setTarget(HashMap<Integer, ObjectSerialize> bullets) {
		BulletCheck.bullets = bullets;
	}
	
	// sets the world tanks
	@Override
	public void setTanks(HashMap<Integer, ObjectSerialize> tanks) {
		BulletCheck.tanks = tanks;
	}
	
	// sets my id
	@Override
	public void myID(int id) {
		BulletCheck.id = id;
	}

}
