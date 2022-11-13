package XTankMultiPlayer;

import java.net.Socket;

import Mazes.Maze;
import XTankUI.XTankUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class XTank 
{
	public static void main(String[] args) throws Exception 
    {
        try (var socket = new Socket("127.0.0.1", 59896)) 
        {
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            int id = in.read();
            if (id == -1) {
            	System.out.println("ERROR: Game Full, Join at another time");
            	return;
            }
            int start = in.read();
            int startx = in.read();
            int starty = in.read();
            Maze maze = new Maze(); //soon change this to read from server instead
            var ui = new XTankUI(in, out, id, start,startx, starty, maze);
        }
    
    }
}
