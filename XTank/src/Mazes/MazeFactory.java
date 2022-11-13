package Mazes;

import java.util.ArrayList;
import java.util.Iterator;

import Serializer.ObjectSerialize;
/*
 * Interface for mazefactory
 */
public interface MazeFactory {
    public ArrayList<Integer[]> spawns();
    public void rGenerate();
    public ArrayList<ObjectSerialize> walls();
}