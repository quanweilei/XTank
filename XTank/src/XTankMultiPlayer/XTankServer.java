package XTankMultiPlayer;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.management.timer.Timer;

import Serializer.ObjectSerialize;
import Serializer.Serializer;

import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class XTankServer 
{
	static ArrayList<DataOutputStream> sq;
    private static Serializer ser;
    private static volatile HashMap<Integer, ObjectSerialize> tanks;
    private static HashSet<ObjectSerialize> walls;
    private static HashMap<Socket, Integer> sockets;
    // TODO: implement different spawning, will be done with maze generation
    private static ArrayList<Integer[]> spawnable;
    private static volatile ObjectSerialize reset;
    private static ObjectSerialize started;
    private static ObjectSerialize win;
    private static ObjectSerialize loss;
    private static Random random;
    
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
        ser = Serializer.getInstance();
        tanks = new HashMap<>();
        spawnable = new ArrayList<>();
        walls = new HashSet<>();
        sockets = new HashMap<>();
        random = new Random();
        // Temporary spawns
        Integer[] one = new Integer[2];
        one[0] = 300;
        one[1] = 800;
        
        Integer[] two = new Integer[2];
        two[0] = 300;
        two[1] = 200;
        
        spawnable.add(one);
        spawnable.add(two);
        
        // reset protocol, informs of leaving players
        reset = new ObjectSerialize("null", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        // start protocol
        started = new ObjectSerialize("strt", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        // win protocol
        win = new ObjectSerialize("iWon", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 9);
        // loss protocol
        loss = new ObjectSerialize("lost", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 8);
        var pool = Executors.newFixedThreadPool(20);
        
        try (var listener = new ServerSocket(59896)) 
        {
            System.out.println("The XTank server is running...");
            
            while (true) 
            {

            	if (sockets.size() == 0) {
            		started.setStatus(0);
            	}
            	reset.setID(-1);
                Socket curr = listener.accept();
                int id = getAvailableID();
                Integer[] spawn = randomSpawn();
                
                if ((id == -1) || (spawn == null)){
                    System.out.println("Too many players, denying connection");
                    curr.close();
                }
                else 
                {
                	System.out.println("Spawning Player " + id + " at " + String.valueOf(spawn[0]) + ", " + String.valueOf(spawn[1]));
                    curr.getOutputStream().write(id);
                    curr.getOutputStream().write(started.getStatus());
                    sockets.put(curr, id);
                    curr.getOutputStream().write(spawn[0]);
                    curr.getOutputStream().write(spawn[1]);
                    curr.getOutputStream().flush();
                	pool.execute(new XTankManager(curr, id, spawn));
                }
            }
        }
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private int id;
        private ArrayList<DataOutputStream> mySers;
        private ObjectSerialize myStat;
        private Integer mySpawn[];
        private boolean left;
        
        XTankManager(Socket socket, int id, Integer[] mySpawn) { this.socket = socket; this.id = id; mySers = new ArrayList<>(); this.mySpawn = mySpawn;}

		@Override
        public synchronized void run() 
        {
            System.out.println("Connected: " + socket);
            try 
            {
            	DataInputStream in = new DataInputStream(socket.getInputStream());
            	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            	myStat = ser.byteToOb(in.readNBytes(189));
            	mySers.add(out);
                sq.add(out);
                left = false;
                tanks.put(id, null);
                
                while (true) {
                	//System.out.println("Player " + id + " waiting");
                	if (in.available() > 0) {
                		myStat = ser.byteToOb(in.readNBytes(189));
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
                    	tanks.put(obj.id(), obj);
                    	for (DataOutputStream o: sq) {
                    		o.write(ser.obToByte(obj));
                    	}
                    }
                    
                    if (obj != null  && obj.name().equals("bull")) {
            			for (DataOutputStream o: sq) {
            				o.write(ser.obToByte(obj));
            			}
            		}
                    
            	
                	for (DataOutputStream o: sq)
                	{
                        if (reset.id() != -1) {
                        	System.out.println("LEFT");
                        	o.write(ser.obToByte(reset));
                        	o.flush();
                        }

                	}
                	if (left == true) {
                		reset.setID(-1);
                		left = false;
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
			try { spawnable.add(mySpawn); sockets.remove(socket); reset.setID(id); tanks.remove(id); sq.removeAll(mySers); socket.close(); } 
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

    private static Integer[] randomSpawn() {
    	// TODO: May need to implement check for if tank is in existing spot
    	if (spawnable.size() == 0) {
    		return null;
    	}
    	int seed = random.nextInt(spawnable.size());
        Integer[] ret = spawnable.get(seed);
        spawnable.remove(seed);
        return ret;
    }
}


