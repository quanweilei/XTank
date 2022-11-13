package BoundCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.graphics.Rectangle;

import Serializer.ObjectSerialize;
/*
 * Quanwei Lei
 * TankCheck checks the bounds of the tank and whether or not it is touching the border or another tank, and checks for wall collision
 */
public class TankCheck implements BoundCalc{
	private static TankCheck tCheck = null;
	private static ObjectSerialize obj;
	private static HashMap<Integer, ObjectSerialize> tanks;
	private static ArrayList<ObjectSerialize> walls;
	private static ArrayList<Rectangle> wallBounds;
	private static int uiHeight;
	private static int uiWidth;
	private int id;
	
	// Returns instance of TankCheck
	public static TankCheck getInstance() {
		if (tCheck == null) {
			tCheck = new TankCheck();
		}
		return tCheck;
	}
	
	// Set the ObjectSerialize to given
	@Override
	public void setObj(ObjectSerialize obj) {
		TankCheck.obj = obj;
	}
	
	// returns the object after processing
	@Override
	public ObjectSerialize getObj() {
		return obj;
	}
	
	// checks the object
	@Override
	public void check() {
		ObjectSerialize og = tanks.get(obj.id());
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
		
		System.out.println(uiHeight);
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
			obj.setXY(currx, (((-cDirY*14) - cHeight)/2));
			return;
		}
		
		// If gun goes through bottom
		if (gunY > uiHeight) {
			obj.setXY(currx, ((2*(uiHeight-cDirY*7) - cHeight)/2));
			return;
		}
		Rectangle currRec = new Rectangle(currx, curry, cWidth, cHeight);
		// Check if touching wall
		for (Rectangle r: wallBounds) {
			if (currRec.intersects(r)) {
				obj.set(og);
				return;
			}
		}

		System.out.println("CHECK");
		// Now Check if tank is touching another tank, if it is denies movement
		for (Integer i: tanks.keySet()) {
			if ((i != curr.id()) && (tanks.get(i) != null)){
				System.out.println("Checking other Tank: " + tanks.get(i));
				ObjectSerialize other = tanks.get(i);
				int otherX = other.x();
				int otherY = other.y();
				int oWidth = other.width();
				int oHeight = other.height();

				Rectangle oRec = new Rectangle(otherX, otherY, oWidth, oHeight);
				
				System.out.println("Other Tank Coordinates: " + otherX + ", " + otherY + " to " + (otherX + oWidth) + ", " + (otherY + oHeight));
				if (currRec.intersects(oRec)) 
				{
					obj.set(og);
				}
			}
		}
		
		
		System.out.println("Bound Checking Tank: " + obj);
	}
	
	// setUIBounds sets the bounds for the ui so that tank can be seen when border 
	@Override
	public void setUIBounds(int height, int width) {
		uiHeight = height;
		uiWidth = width;
	}
	
	// sets the walls to the given walls, generates rectangles for every wall
	@Override
	public void setWalls(ArrayList<ObjectSerialize> walls) {
		TankCheck.walls = walls;
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
	
	
	// sets the target to a given hashmap of tanks
	@Override
	public void setTarget(HashMap<Integer, ObjectSerialize> tanks) {
		TankCheck.tanks = tanks;
	}
	
	// sets the tanks 
	@Override
	public void setTanks(HashMap<Integer, ObjectSerialize> tanks) {
		TankCheck.tanks = tanks;
	}
	
	// my id
	@Override
	public void myID(int id) {
		this.id = id;
	}
	
	

}
