package XTankMultiPlayer;

import java.net.Socket;

import Mazes.Maze;
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
        	
        	// Fill Maze

            var ui = new XTankUI(in, out, id);
        }
    
    }
}
