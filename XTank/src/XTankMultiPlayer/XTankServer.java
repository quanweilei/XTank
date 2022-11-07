package XTankMultiPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class XTankServer 
{
	static ArrayList<DataOutputStream> sq;
    static HashMap<Socket, HashMap<Integer, Integer>> coords;
	
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
        coords = new HashMap<>();
		
        try (var listener = new ServerSocket(59896)) 
        {
            System.out.println("The XTank server is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) 
            {
                pool.execute(new XTankManager(listener.accept()));
            }
        }
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;

        XTankManager(Socket socket) { this.socket = socket; }

        @Override
        public void run() 
        {
            System.out.println("Connected: " + socket);
            try 
            {
            	DataInputStream in = new DataInputStream(socket.getInputStream());
            	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                sq.add(out);
                int ycoord;
                int xcoord;

                while (true)
                {
                	ycoord = in.readInt();
                    xcoord = in.readInt();
                	//System.out.println("ycoord = " + ycoord);
                	for (DataOutputStream o: sq)
                	{
    					o.writeInt(ycoord);
                        o.writeInt(xcoord);
                	}
                }
            } 
            catch (Exception e) 
            {
                System.out.println("Error:" + socket);
                System.out.println(e);
            } 
            finally 
            {
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
    
}


