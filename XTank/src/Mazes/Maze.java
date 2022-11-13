package Mazes;

import java.util.ArrayList;
import java.util.Random;

import Serializer.ObjectSerialize;

public class Maze implements MazeFactory {

	private ArrayList<Integer[]> wallspawns;
	
	public Maze() {
		wallspawns = new ArrayList<Integer[]>();
		rGenerate();
	}
	@Override
	public ArrayList<Integer[]> spawns() {
		return wallspawns;
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
			} else {
				int y = rand.nextInt(0, 750);
				wallpoints[0] = rand.nextInt(0, 1440);
				wallpoints[1] = y;
				wallpoints[2] = rand.nextInt(0, 1440);
				wallpoints[3] = y;
			}
			wallspawns.add(wallpoints);
		}
		//generate ints for wallpoints for each wall and add them to wallspawns
	}

	@Override
	public ObjectSerialize walls() {
		// TODO Auto-generated method stub
		return null;
	}

}
