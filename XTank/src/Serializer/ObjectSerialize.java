package Serializer;

import java.io.Serializable;

public class ObjectSerialize implements Serializable{
    String name;
    int x;
    int y;
    int color;
    int gun;
    int dirX;
    int dirY;
    public int playerID;

    public ObjectSerialize(String name, int x, int y, int color, int gun, int dirX, int dirY) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = color;
        this.gun = gun;
        this.dirX = dirX;
        this.dirY = dirY;
    }
    
    @Override
    public String toString() {
        return name + " at {" + x + ", " + y + "}";
    }


}