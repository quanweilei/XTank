package XTankMultiPlayer;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import BoundCheck.Bounds;
import Serializer.ObjectSerialize;
import Serializer.Serializer;

import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class XTankServer 
{
	static ArrayList<DataOutputStream> sq;
    private static Serializer ser;
    private static volatile HashMap<Integer, ObjectSerialize> tanks;
    private static volatile HashMap<Integer, ObjectSerialize> bullets;
    private static HashSet<ObjectSerialize> walls;
    private static HashMap<Socket, Integer> sockets;
    // TODO: implement different spawning, will be done with maze generation
    private static ArrayList<Integer[]> spawnable;
    private static volatile ObjectSerialize reset;
    private static ObjectSerialize started;
    private static ObjectSerialize win;
    private static ObjectSerialize loss;
    private static Bounds bCheck;
    
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		bCheck = Bounds.getInstance();
		sq = new ArrayList<>();
        ser = Serializer.getInstance();
        tanks = new HashMap<>();
        spawnable = new ArrayList<>();
        walls = new HashSet<>();
        sockets = new HashMap<>();
        bCheck.walls(walls);
        
        // ability to change bounds not implemented, but can be declared here
        bCheck.setBounds(967, 1904);
        bullets = new HashMap<>();
        // reset protocol, informs of leaving players
        reset = new ObjectSerialize("null", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        // start protocol
        started = new ObjectSerialize("strt", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        // win protocol
        win = new ObjectSerialize("iWon", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 9);
        // loss protocol
        loss = new ObjectSerialize("lost", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 8);
        var pool = Executors.newFixedThreadPool(20);
        pool.execute(new Bullets());
        try (var listener = new ServerSocket(59896)) 
        {
            System.out.println("The XTank server is running...");
            
            while (true) 
            {
            	System.out.println(sockets);
            	if (sockets.size() == 0) {
            		started.setStatus(0);
            	}
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
                    curr.getOutputStream().write(started.getStatus());
                    sockets.put(curr, id);
                    //curr.getOutputStream().write(startx);
                    //curr.getOutputStream().write(starty);
                    curr.getOutputStream().flush();
                	pool.execute(new XTankManager(curr, id));
                }
            }
        }
    }
    
    private static class Bullets implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					for (DataOutputStream o: sq) {
						for (Integer c: bullets.keySet()) {
                        	o.write(ser.obToByte(bullets.get(c)));
                        	o.flush();
                        }
					}
					bulletIterate();
					Thread.sleep(200);
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private int id;
        private ArrayList<DataOutputStream> mySers;
        private int start;
        private ObjectSerialize myStat;
        
        XTankManager(Socket socket, int id) {start = started.getStatus(); this.socket = socket; this.id = id; mySers = new ArrayList<>();}

		@Override
        public synchronized void run() 
        {
            System.out.println("Connected: " + socket);
            try 
            {
            	myStat = new ObjectSerialize(null, id, id, id, id, id, id, id, id, id, id);
            	tanks.put(id, null);
            	
            	DataInputStream in = new DataInputStream(socket.getInputStream());
            	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            	myStat = ser.byteToOb(in.readNBytes(189));
            	mySers.add(out);
                sq.add(out);
                
                while (true) {
                	//System.out.println("Player " + id + " waiting");
                	if (in.available() > 0) {
                		myStat = ser.byteToOb(in.readNBytes(189));
                		System.out.println("Receiving Status " + myStat.getStatus());
                	}
                	if (started.getStatus() == 1) {
                		myStat.setStatus(1);
                		out.write(ser.obToByte(myStat));
            			break;
            		}
                	if (myStat.getStatus() == 1) {
                		started.setStatus(1);
                		break;
                	}
                }
                
                
                System.out.println("Player " + id + " in game");
                while (true)
                {
        			ObjectSerialize obj = ser.byteToOb(in.readNBytes(189));
        			System.out.println("Accepting Object: " + obj);
        			//System.out.println(obj);
                    if (obj.name().contains("Tank")) {
                    	if (obj != tanks.get(obj.id())) {
                    		bCheck.check(obj);
                        	tanks.put(obj.id(), obj);
                    	}
                    }
                    // TODO: bullet hitting terrain and other players, need some way to delete bullets
                    if (obj.name().equals("bull")) {
                    	if (obj.id() == -1) {
                    		int i = getBulletID();
                        	obj.setID(i);
                        	bullets.put(getBulletID(), obj);
                    	}
                    }
            	
                	for (DataOutputStream o: sq)
                	{
                        for (Integer j: tanks.keySet()) {
                        	if (tanks.get(j) != null) {
                        		o.write(ser.obToByte(tanks.get(j)));
	                            o.flush();
                        	}
                        }
                        if (reset.id() != -1) {
                        	System.out.println("LEFT");
                        	o.write(ser.obToByte(reset));
                        	o.flush();
                        	reset.setID(-1);
                        }

                	}
                	Thread.sleep(100);
            }
                
            } 
            catch (Exception e) 
            {
                System.out.println("Error:" + socket);
                System.out.println(e);
            } 
            finally 
            {
                try {
					leave();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
		
		public void leave() throws Exception {
			try { sockets.remove(socket); reset.setID(id); tanks.remove(id); sq.removeAll(mySers); socket.close(); } 
            catch (Exception e) {}
            System.out.println("Closed: " + socket);
            if (sockets.size() == 0) {
            	started.setStatus(0);
            	System.out.println("Reseting Server");
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
            		curr.setXY(curr.x() + curr.dirX() * 2, curr.y() + curr.dirY() * 2);
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


