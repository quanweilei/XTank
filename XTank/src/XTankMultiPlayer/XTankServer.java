package XTankMultiPlayer;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.management.timer.Timer;

import Mazes.Maze;
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
    private static Maze maze;
    private static HashMap<Socket, Integer> sockets;
    // TODO: implement different spawning, will be done with maze generation
    private static ArrayList<Integer[]> spawnable;
    
    private static volatile ObjectSerialize reset;
    private static ObjectSerialize started;
    private static ObjectSerialize endwall;
    private static Random random;
    private static ExecutorService pool;
    
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
        ser = Serializer.getInstance();
        tanks = new HashMap<>();
        spawnable = new ArrayList<>();
        
        maze = new Maze();
        
        sockets = new HashMap<>();
        random = new Random();
        // Temporary spawns
        spawnable = maze.spawns();
        System.out.println("Generated Maze");
        System.out.println(spawnable.size());
        // reset protocol, informs of leaving players
        reset = new ObjectSerialize("null", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        // start protocol
        started = new ObjectSerialize("plyr", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        endwall = new ObjectSerialize("endw", -1, - 1, -1, -1, -1, -1, -1, -1, -1, 1);
        
        pool = Executors.newFixedThreadPool(20);
        
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
                
                if ((tanks.size() > 20) || (spawnable.size() == 0)){
                    System.out.println("Too many players, denying connection");
                    curr.close();
                }
                else 
                {
                	int id = getAvailableID();
                	curr.getOutputStream().write(id);
                	for (ObjectSerialize wall: maze.walls()) {
                		curr.getOutputStream().write(ser.obToByte(wall));
                	}
                	curr.getOutputStream().write(ser.obToByte(endwall));
                	curr.getOutputStream().flush();
                	
                	pool.execute(new XTankManager(curr, id));
                }
            }
        }
    }
    
    /*
     * XTankManager is used to manage tanks, sends and recieves all data needed for tanks, bullets, and game status
     */
    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private int id;
        private ArrayList<DataOutputStream> mySers;
        private ObjectSerialize myStat;
        private Integer mySpawn[];
        private boolean left;
        
        XTankManager(Socket socket, int id) { this.socket = socket; mySers = new ArrayList<>(); this.id = id;}

		@Override
        public void run() 
        {
            System.out.println("Connected: " + socket);
            try 
            {	
               while (execute()) {
            	   
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
		
		private boolean execute() throws ClassNotFoundException, IOException, InterruptedException {
        	sockets.put(socket, id);
        	tanks.put(id, null);
        	Integer[] mySpawn = randomSpawn();
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	out.flush();
            out.write(ser.obToByte(started));
            System.out.println("Started Status sent to Client");
            ObjectSerialize spawn = new ObjectSerialize("spwn", mySpawn[0], mySpawn[1], id, id, id, id, id, id, id, id);
            out.write(ser.obToByte(spawn));
            System.out.println("Spawn Location sent to Client");
            out.flush();
        	myStat = ser.byteToOb(in.readNBytes(189));
        	mySers.add(out);
            sq.add(out);
            left = false;

            
            // starting screen
            while (true) {
            	
            	if (in.available() > 0) {
            		myStat = ser.byteToOb(in.readNBytes(189));
            		System.out.println("Recieved Status: "+ myStat);
            	}
            	if (started.getStatus() == 1) {
            		myStat.setStatus(1);
            		out.write(ser.obToByte(myStat));
            		System.out.println("Sending Status");
            		out.flush();
        			break;
        		}
            	if (myStat.getStatus() == 1) {
            		started.setStatus(1);
            	}
            }
            
            
            System.out.println("Player " + id + " in game");
            // player should be in game by here
            while (true)
            {
    			ObjectSerialize obj = ser.byteToOb(in.readNBytes(189));
    			System.out.println(obj);
    			if (obj.name().equals("endg")) {
    				// end of game
    				System.out.println(tanks.size());
    				Thread.sleep(3000 + id * 40);
    				started.setStatus(0);
    				spawnable.add(mySpawn);
    				tanks.clear();
    				System.out.println("Ending the Game");
    				for (DataOutputStream o :sq) {
    					o.write(ser.obToByte(obj));
    					o.flush();
    				}
    				sq.remove(out);
    				out.flush();
    				return true;
    			}
    			System.out.println("Accepting Object: " + obj);
    			//System.out.println(obj);
    			// send out tanks
                if (obj.name().contains("Tank")) {
                	tanks.put(obj.id(), obj);
                	for (DataOutputStream o: sq) {
                		o.write(ser.obToByte(obj));
                	}
                }
                
                if (obj != null  && obj.name().equals("bull")) {
                	// send out bullets
        			for (DataOutputStream o: sq) {
        				o.write(ser.obToByte(obj));
        			}
        		}
                
                
                // send out reset id for leaving 
            	for (DataOutputStream o: sq)
            	{
                    if (reset.id() != -1) {
                    	System.out.println("LEFT");
                    	o.write(ser.obToByte(reset));
                    	o.flush();
                    }

            	}
            	// reset reset
            	if (left == true) {
            		reset.setID(-1);
            		left = false;
            	}
            	Thread.sleep(150);
            }

            
		}
		
		
		private void leave() throws Exception {
			try { spawnable.add(mySpawn); sockets.remove(socket); reset.setID(id); tanks.remove(id); sq.removeAll(mySers); socket.close(); } 
            catch (Exception e) {}
            System.out.println("Closed: " + socket);
            if (sockets.size() == 0) {
            	started.setStatus(0);
            	System.out.println("Reseting Server");
            }
		}
    }
    
    
    
    /*
     * returns an ID that is not in use by tanks
     */
    private static int getAvailableID() {
        for (int i = 1; i < 21; i++) {
            if (!tanks.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }
    
    /*
     * returns a random spawn from spawns
     */
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


