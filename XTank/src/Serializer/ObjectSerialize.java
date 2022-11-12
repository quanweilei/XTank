package Serializer;

import java.io.Serializable;
import javax.print.attribute.PrintServiceAttribute;
/*
 * Quanwei Lei
 * ObjectSerialize serializes all needed for information for every object needed in the game.
 */
public class ObjectSerialize implements Serializable{
    private String name;
    private int x;
    private int y;
    private int color;
    private int gun;
    private int dirX;
    private int dirY;
    private int id;
    private int width;
    private int height;
    private int status;


    public ObjectSerialize(String name, int x, int y, int color, int gun, int dirX, int dirY, int id, int width, int height, int status) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = color;
        this.gun = gun;
        this.dirX = dirX;
        this.dirY = dirY;
        this.id = id;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "ID: " + id + ", " + name + " at {" + x + ", " + y + "}, Dimensions: {" + width + ", " + height + "}, Direction: {" + dirX + ", " + dirY + "}";
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

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
    
    // returns status of object, if 0, indicates death of this object
    public int getStatus() {
    	return this.status;
    }
    
    public void setID(int id) {
    	this.id = id;
    }
    
    public void setXY(int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    public void setDir(int x, int y) {
    	this.dirX = x;
    	this.dirY = y;
    }
    
    public void setStatus(int status) {
    	this.status = status;
    }
    
    public void set(ObjectSerialize copy) {
    	this.x = copy.x();
    	this.y = copy.y();
    	this.name = copy.name();
    	this.color = copy.color();
    	this.dirX = copy.dirX;
    	this.dirY = copy.dirY;
    	this.status = copy.getStatus();
    	this.id = copy.id();
    	this.width = copy.width();
    	this.height = copy.height();
    }

}