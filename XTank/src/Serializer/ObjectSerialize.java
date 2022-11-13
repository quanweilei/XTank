package Serializer;

import java.io.Serializable;
import javax.print.attribute.PrintServiceAttribute;
/*
 * Quanwei Lei
 * ObjectSerialize serializes all needed for information for every object needed in the game.
 */
public class ObjectSerialize implements Serializable{

	private static final long serialVersionUID = 13505695996255119L;
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

    /*
     * Initiates ObjectSerialize, sets up all info needed to be sent accross
     */
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
        this.status = status;
    }

	@Override
    public String toString() {
        return "ID: " + id + ", " + name + " at {" + x + ", " + y + "}, Dimensions: {" + width + ", " + height + "}, Direction: {" + dirX + ", " + dirY + "}, Status: " + status;
    }
	
	// ret name
    public String name() {
        return this.name;
    }
    
    // ret x
    public int x(){
        return this.x;
    }
    
    // ret y
    public int y() {
        return this.y;
    }
    
    // ret color
    public int color() {
        return this.color;
    }
    
    // ret gun
    public int gun() {
        return this.gun;
    }
    
    // ret direction (x)
    public int dirX() {
        return this.dirX;
    }

    // ret direction (y)
    public int dirY() {
        return this.dirY;
    }

    // ret id
    public int id() {
        return this.id;
    }
    
    // ret width
    public int width() {
        return this.width;
    }
    
    // ret height
    public int height() {
        return this.height;
    }
    
    // returns status of object, if 0, indicates death of this object
    public int getStatus() {
    	return this.status;
    }
    
    
    // sets id of the given object
    public void setID(int id) {
    	this.id = id;
    }
    
    // sets the x y coord of given object
    public void setXY(int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    // sets the direction of given object
    public void setDir(int x, int y) {
    	this.dirX = x;
    	this.dirY = y;
    }
    
    // sets the status of the given object
    public void setStatus(int status) {
    	this.status = status;
    }
    
    // sets the object to another
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