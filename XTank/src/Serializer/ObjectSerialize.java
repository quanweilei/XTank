package Serializer;

import java.io.Serializable;

import javax.print.attribute.PrintServiceAttribute;

public class ObjectSerialize implements Serializable{
    private String name;
    private int x;
    private int y;
    private int color;
    private int gun;
    private int dirX;
    private int dirY;
    private int id;


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

    public String name() {
        return this.name;
    }

    public int x(){
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int color() {
        return this.color;
    }

    public int gun() {
        return this.gun;
    }

    public int dirX() {
        return this.dirX;
    }

    public int dirY() {
        return this.dirY;
    }

    public int id() {
        return this.id;
    }




}