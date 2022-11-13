package Mazes;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.graphics.Rectangle;

import Serializer.ObjectSerialize;

public class Maze implements MazeFactory {

	private ArrayList<Integer[]> spawns;
	private ArrayList<ObjectSerialize> wallObj;
	private ArrayList<Rectangle> wallBound;
	
	public Maze() {
		wallObj = new ArrayList<>();
		spawns = new ArrayList<Integer[]>();
		wallBound = new ArrayList<>();
		rGenerate();
	}
	@Override
	public ArrayList<Integer[]> spawns() {
		return spawns;
	}

	@Override
	// generates spawn points for 10 walls throughout the map
	public void rGenerate() {
		Random rand = new Random();
		for (int i = 0 ; i < 10 ; i++) {
			Integer[] wallpoints = {0, 0, 0, 0};
			if (i > 5) {
				int x = rand.nextInt(0, 1440);
				wallpoints[0] = x;
				wallpoints[1] = rand.nextInt(0, 750);
				wallpoints[2] = x;
				wallpoints[3] = rand.nextInt(0, 750);
				wallBound.add(new Rectangle(x, Math.min(wallpoints[1], wallpoints[3]), 10, Math.max(wallpoints[1], wallpoints[3]) - Math.min(wallpoints[1], wallpoints[3])));
			} else {
				int y = rand.nextInt(0, 750);
				wallpoints[0] = rand.nextInt(0, 1440);
				wallpoints[1] = y;
				wallpoints[2] = rand.nextInt(0, 1440);
				wallpoints[3] = y;
				wallBound.add(new Rectangle(Math.min(wallpoints[0], wallpoints[2]), y, Math.max(wallpoints[0], wallpoints[2]) - Math.min(wallpoints[0], wallpoints[2]), 10));
			}
			ObjectSerialize wall = new ObjectSerialize("wall", wallpoints[0], wallpoints[1], 0, 0, wallpoints[2], wallpoints[3], 0, 0, 0, 0);
			
			wallObj.add(wall);
		}
		
		//generate ints for wallpoints for each wall and add them to wallspawns
		boolean works = true;
		// calculate all possible spawns
		System.out.println(wallBound);
		for (int i = 0; i < 1904/200; i++) {
			for (int j = 0; j < 967/200; j++) {
				Rectangle temp = new Rectangle(i*200, j*200, 200, 200);
				for (Rectangle r: wallBound) {
					if (temp.intersects(r)) {
						works = false;
					}
				}
				if (works == true) {
					Integer[] spawn = new Integer[2];
					spawn[0] = i*200 + 50;
					spawn[1] = j*200 + 50;
					spawns.add(spawn);
				}
				works = true;
			}
		}
		
		
		
	}

	@Override
	public ArrayList<ObjectSerialize> walls() {
		return wallObj;
	}

}
