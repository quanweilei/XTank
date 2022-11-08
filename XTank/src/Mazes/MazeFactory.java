package Mazes;

import java.util.ArrayList;

import Serializer.ObjectSerialize;

public interface MazeFactory {
    public ArrayList<Integer[]> spawns();
    public void rGenerate();
    public ObjectSerialize walls();
}