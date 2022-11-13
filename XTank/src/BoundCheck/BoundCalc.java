package BoundCheck;

import java.util.ArrayList;
import java.util.HashMap;
import Serializer.ObjectSerialize;

public interface BoundCalc {
	public void setUIBounds(int height, int width);
	public void setWalls(ArrayList<ObjectSerialize> walls);
	public void setTarget(HashMap<Integer, ObjectSerialize> targ);
	public void setTanks(HashMap<Integer, ObjectSerialize> tanks);
	public void setObj(ObjectSerialize obj);
	public ObjectSerialize getObj();
	public void check();
	public void myID(int id);
}
