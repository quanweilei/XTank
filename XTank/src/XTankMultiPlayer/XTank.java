package XTankMultiPlayer;

import java.net.Socket;

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
            int start = in.read();
            //int startx = in.read();
            //int starty = in.read();
            var ui = new XTankUI(in, out, id, start /*,startx, starty*/);
        }
    
    }
}


