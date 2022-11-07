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
    public int id;


    public ObjectSerialize(String name, int x, int y, int color, int gun, int dirX, int dirY, int id) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = color;
        this.gun = gun;
        this.dirX = dirX;
        this.dirY = dirY;
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "ID: " + id + ", " + name + " at {" + x + ", " + y + "}";
    }


}