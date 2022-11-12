package BoundCheck;

import java.util.HashMap;
import java.util.HashSet;

import Serializer.ObjectSerialize;

public interface BoundCalc {
	public void setUIBounds(int height, int width);
	public void setWalls(HashSet<ObjectSerialize> walls);
	public void setTarget(HashMap<Integer, ObjectSerialize> targ);
	public void setTanks(HashMap<Integer, ObjectSerialize> tanks);
	public void setObj(ObjectSerialize obj);
	public ObjectSerialize getObj();
	public void check();
}
