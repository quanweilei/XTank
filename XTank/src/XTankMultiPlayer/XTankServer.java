package XTankMultiPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Serializer.ObjectSerialize;
import Serializer.Serializer;

import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class XTankServer 
{
	static ArrayList<DataOutputStream> sq;
    private static Serializer ser;
    private static HashMap<Integer, ObjectSerialize> tanks;
    private static HashMap<Integer, ObjectSerialize> bullets;
    private static HashMap<Socket, Integer> sockets;
    // TODO: implement different spawning, will be done with maze generation
    private static ArrayList<Integer[]> spawnable;
    private static ObjectSerialize reset;

	
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
        ser = Serializer.getInstance();
        tanks = new HashMap<>();
        spawnable = new ArrayList<>();
        sockets = new HashMap<>();
        bullets = new HashMap<>();
        reset = new ObjectSerialize("null", -1, - 1, -1, -1, -1, -1, -1, -1, -1);
		
        try (var listener = new ServerSocket(59896)) 
        {
            System.out.println("The XTank server is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) 
            {
            	reset.setID(-1);
                Socket curr = listener.accept();
                int id = getAvailableID();
                if (id == -1) {
                    System.out.println("Too many players, denying connection");
                    curr.close();
                }
                else 
                {
                    curr.getOutputStream().write(id);
                    sockets.put(curr, id);
                    //curr.getOutputStream().write(startx);
                    //curr.getOutputStream().write(starty);
                    curr.getOutputStream().flush();
                    pool.execute(new XTankManager(curr, id));
                }
            }
        }
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private int id;
        private ArrayList<DataOutputStream> mySers;
        
        XTankManager(Socket socket, int id) { this.socket = socket; this.id = id; mySers = new ArrayList<>();}

		@Override
        public synchronized void run() 
        {
            System.out.println("Connected: " + socket);
            try 
            {
            	DataInputStream in = new DataInputStream(socket.getInputStream());
            	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            	mySers.add(out);
                sq.add(out);
                while (true)
                {   
                    ObjectSerialize obj = ser.byteToOb(in.readNBytes(176));
                    if (obj.name().contains("Tank")) {
                    	tanks.put(obj.id(), obj);
                    }
                    // TODO: bullet hitting terrain and other players, need some way to delete bullets
                    if (obj.name().equals("bull")) {
                    	if (obj.id() == -1) {
                    		int i = getBulletID();
                        	obj.setID(i);
                        	bullets.put(getBulletID(), obj);
                    	}
                    }
                    
                    System.out.println(bullets);
                	
                	for (DataOutputStream o: sq)
                	{
                        for (Integer j: tanks.keySet()) {
                            o.write(ser.obToByte(tanks.get(j)));
                            o.flush();
                        }
                        for (Integer c: bullets.keySet()) {
                        	o.write(ser.obToByte(bullets.get(c)));
                        	o.flush();
                        }
                        if (reset.id() != -1) {
                        	System.out.println("LEFT");
                        	o.write(ser.obToByte(reset));
                        }
                        o.flush();
                        bulletIterate();
                        Thread.sleep(100);
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
                try { reset.setID(id); tanks.remove(id); sq.removeAll(mySers); socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }

    private static int getAvailableID() {
        for (int i = 1; i < 21; i++) {
            if (!tanks.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }
    
    private static void bulletIterate() {
    	for (int i = 0; i < bullets.size(); i++) {
    		if (bullets.containsKey(i)) {
    			ObjectSerialize curr = bullets.get(i);
        		curr.setXY(curr.x() + curr.dirX() * 1, curr.y() + curr.dirY() * 1);
    		}
    	}
    }
    
    private static int getBulletID() {
    	int i = 0;
    	while (true) {
    		if (!bullets.containsKey(i)) {
    			return i;
    		}
    		i++;
    	}
    }
}


