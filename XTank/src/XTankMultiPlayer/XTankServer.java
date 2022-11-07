package XTankMultiPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import Serializer.ObjectSerialize;
import Serializer.Serializer;

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
    private static Serializer ser;
    private static HashMap<Integer, ObjectSerialize> objects;
    private static int id;

	
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
        ser = Serializer.getInstance();
        objects = new HashMap<>();
        id = 0;
		
        try (var listener = new ServerSocket(59896)) 
        {
            System.out.println("The XTank server is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) 
            {
                Socket curr = listener.accept();
                curr.getOutputStream().write(id);
                id++;
                curr.getOutputStream().flush();
                pool.execute(new XTankManager(curr));
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

                while (true)
                {   
                    ObjectSerialize obj = ser.byteToOb(in.readNBytes(151));
                    objects.put(obj.id, obj);
                    System.out.println(objects);
                	
                	for (DataOutputStream o: sq)
                	{
                        for (Integer id: objects.keySet()) {
                            o.write(ser.obToByte(objects.get(id)));
                            o.flush();
                        }
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


