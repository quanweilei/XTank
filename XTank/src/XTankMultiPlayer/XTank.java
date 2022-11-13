package XTankMultiPlayer;

import java.net.Socket;
import java.util.ArrayList;

import Mazes.Maze;
import Serializer.ObjectSerialize;
import Serializer.Serializer;
import XTankUI.XTankUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/*
 * Client Starter for XTank, takes in a unique ID and then starts game if not full
 */
public class XTank 
{
	public static void main(String[] args) throws Exception 
    {
        try (var socket = new Socket("127.0.0.1", 59896)) 
        {
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	int id = in.read();
        	Serializer ser = new Serializer();
        	// Fill Maze
        	ArrayList<ObjectSerialize> walls = new ArrayList<>();
        	ObjectSerialize mazeComp = ser.byteToOb(in.readNBytes(189));
        	
        	while (!mazeComp.name().equals("endw")) {
        		walls.add(mazeComp);
        		mazeComp = ser.byteToOb(in.readNBytes(189));
        	}

            var ui = new XTankUI(in, out, id, walls);
        }
    
    }
}
