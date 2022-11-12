package Mazes;

import java.util.ArrayList;

import Serializer.ObjectSerialize;

public class Maze implements MazeFactory {

	private ArrayList<Integer[]> wallspawns;
	
	public Maze() {
		wallspawns = new ArrayList<Integer[]>();
	}
	@Override
	public ArrayList<Integer[]> spawns() {
		return wallspawns;
	}

	@Override
	// generates spawn points for walls
	public void rGenerate() {
		Integer[] wallpoints;
		//generate ints for wallpoints for each wall and add them to wallspawns
	}

	@Override
	public ObjectSerialize walls() {
		// TODO Auto-generated method stub
		return null;
	}

}
