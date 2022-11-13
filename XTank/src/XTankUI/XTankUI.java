package XTankUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import BoundCheck.Bounds;
import Serializer.ObjectSerialize;
import Serializer.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class XTankUI
{
	// The location and direction of the "tank"
	private int x;
	private int y;
	private int directionX = 0;
	private int directionY = -10;
	private int color;
	private int gun;
	private int width;
	private int height;
	private int hp;

	private int hop;
	private int start;
	private static ObjectSerialize myStat;
	private static int id = 0;

	private static Canvas canvas;
	private static Serializer ser = Serializer.getInstance();
	private Display display;
	private Shell shell;
	
	private DataInputStream in; 
	private DataOutputStream out;


	private Command moveHandler;
	private Firing fireHandler;

	private HashMap<Integer, ObjectSerialize> tanks;
	private static HashMap<Integer, ObjectSerialize> bullets;
	private HashSet<ObjectSerialize> walls;
	
	private boolean win;
	private boolean loss;
	private boolean ended;
	
	private Bounds bounds;
	
	
	public XTankUI(DataInputStream in, DataOutputStream out, int id) throws IOException, InterruptedException, ClassNotFoundException
	{
		ended = false;
		win = false;
		loss = false;
		this.in = in;
		this.out = out;
		XTankUI.id = id;
		tanks = new HashMap<>();
		bullets = new HashMap<>();
		
		reset();
		
		color = SWT.COLOR_DARK_GREEN;
		gun = SWT.COLOR_BLACK;
		
		width = 50;
		height = 100;
		// TODO: OPTION FOR HP
		hp = 3;
		hop = hp;

		System.out.println("Sending out status");
		display = new Display();
		
		if (start == 0) {
			this.waiting();
		}
		else {
			this.start();
		}
		//moveHandler = Movement.get();
	}
	
	public void reset() throws IOException, ClassNotFoundException {
        System.out.println("This Client is Player " + id);
		ObjectSerialize temp = ser.byteToOb(in.readNBytes(189));
		while (!temp.name().equals("plyr")) {
			temp = ser.byteToOb(in.readNBytes(189));
		}
		start = temp.getStatus();
		ObjectSerialize spawn = ser.byteToOb(in.readNBytes(189));
		while (!spawn.name().equals("spwn")) {
			spawn = ser.byteToOb(in.readNBytes(189));
		}
		int startx = spawn.x();
        int starty = spawn.y();
		x = startx;
		y = starty;
		directionX = 0;
		directionY = -10;
		tanks.clear();
		bullets.clear();
		hp = hop;
		width = 50;
		height = 100;
		bounds = Bounds.getInstance();
		bounds.setBounds(967, 1904);
		bounds.tanks(tanks);
		bounds.walls(walls);
		bounds.setID(id);
		moveHandler = Movement.get();
		fireHandler = (Firing) Firing.getInstance();
		moveHandler.connect(this);
		fireHandler.connect(this);
	}
	
	public void start() throws InterruptedException
	{
		Shell shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());
		this.shell = shell;
		
		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		
		canvas.addPaintListener(event -> {
			// display all tanks
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			event.gc.fillRectangle(canvas.getBounds());
			for (Integer id: tanks.keySet()) {
				ObjectSerialize curr = tanks.get(id);
				int currx = curr.x();
				int curry = curr.y();
				int color = curr.color();
				int black = curr.gun();
				int cDirX = curr.dirX();
				int cDirY = curr.dirY();
				int cWidth = curr.width();
				int cHeight = curr.height();
				event.gc.setBackground(shell.getDisplay().getSystemColor(color));
				event.gc.fillRectangle(currx, curry, cWidth, cHeight);
				event.gc.setBackground(shell.getDisplay().getSystemColor(black));
				// Find middle point
				int midX = ((2 * currx + cWidth)/2) - 25;
				int midY = ((2 * curry + cHeight)/2) - 25;
				event.gc.fillOval(midX, midY, 50, 50);
				event.gc.setLineWidth(4);
				// Draw gun
				event.gc.drawLine(midX + 25, midY + 25, midX + cDirX*7 + 25, midY + cDirY*7 + 25);
			}
			
			BulletIterator BI = new BulletIterator(bullets);
			@SuppressWarnings("rawtypes")
			Iterator bIt = BI.iterator();
			
			// Ellie Martin
			while (bIt.hasNext()) {
				ObjectSerialize bull = (ObjectSerialize) bIt.next();
				int currx = bull.x();
				int curry = bull.y();
				int cDirX = bull.dirX();
				int cDirY = bull.dirY();
				int cWidth = bull.width();
				int cHeight = bull.height();
				int midX = ((2 * currx + cWidth)/2) - 25;
				int midY = ((2 * curry + cHeight)/2) - 25;
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(midX + cDirX*9 + 25, midY + cDirY*9 + 25, 10, 10);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
			
			// Print out name tags
			for (Integer id: tanks.keySet()) {
				ObjectSerialize curr = tanks.get(id);
				int currx = curr.x();
				int curry = curr.y();
				int cWidth = curr.width();
				int cHeight = curr.height();
				int midX = ((2 * currx + cWidth)/2) - 25;
				int midY = ((2 * curry + cHeight)/2) - 25;
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.drawText("Player " + String.valueOf(id), midX, midY + cHeight);
			}
			
			if (loss == true) {
				Font font = new Font(display,"Arial",32,SWT.BOLD | SWT.ITALIC);
				event.gc.setFont(font);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				Rectangle c = canvas.getBounds();
				event.gc.drawText("YOU DIED", c.width/2, c.height/2);
			}
			
			if (win == true) {
				Font font = new Font(display,"Arial",32,SWT.BOLD | SWT.ITALIC);
				event.gc.setFont(font);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				Rectangle c = canvas.getBounds();
				event.gc.drawText("YOU WON!", c.width/2, c.height/2);
			}
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				if (ended == false || (win) || (loss)) {
					fireHandler.set(e);
				}
			}
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if ((ended == false) || (win) || (loss)){
					if ((e.character == 'f') || (e.character == 'F') || (e.character == ' ')){
						fireHandler.set(e);
					}
					else {
						moveHandler.set(e);
						// update tank location
						try {
							ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
							out.write(ser.obToByte(bounds.check(obj)));
							out.flush();
						}
						catch(IOException ex) {
							System.out.println("The server did not respond (write KL).");
						}
					}
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		
		settings(shell);
		
		try {
			ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
			System.out.println("Sending out Object of Length: " + ser.obToByte(obj).length);
			out.write(ser.obToByte(obj));
		}
		catch(IOException ex) {
			System.out.println("The server did not respond (initial write).");
			System.out.println(ex);
		}				
		Runnable runnable = new Runner();
		Runnable bulIt = new Bullets();
		display.asyncExec(runnable);
		display.asyncExec(bulIt);

		shell.open();
		while (!shell.isDisposed()) { 
			if (!display.readAndDispatch())
				display.sleep();
			
		}

		display.dispose();		
	}
	
	/*
	 * Quanwei Lei
	 * Menu when waiting for players
	 */
	public void waiting() throws IOException, InterruptedException {
		out.flush();
		Shell start = new Shell(display);
		start.setBounds(900 , 600, 400, 400);
		start.setText("Waiting for Game to Start...");
		settings(start);
		Button button = new Button(start, SWT.PUSH);
		button.setText("Start Game");
		System.out.println("waiting...");
		myStat = new ObjectSerialize("plyr", -1, -1, -1, -1, -1, -1, -1, -1, -1, this.start);
		out.write(ser.obToByte(myStat));
		out.flush();
		button.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				System.out.println("Pressed Start");
				myStat.setStatus(1);
			}
			
		});
		
		button.setBounds(145, 200, 100, 30);
		start.open();
		while (!start.isDisposed()) {
			  try {
				if (in.available() > 0) {
					ObjectSerialize ob = ser.byteToOb(in.readNBytes(189));
					if (ob.name().equals("plyr")) {
						myStat = ob;
						System.out.println(ob);
					}
				}
				out.write(ser.obToByte(myStat));
				out.flush();
				if (myStat.getStatus() == 1) {
					ended = false;
					win = false;
					loss = false;
					System.out.println("Moving to Canvas");
					start.dispose();;
					start();
					break;
				}
			  } catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
		      if (!display.readAndDispatch()) {
		        // If no more entries in event queue
		        display.sleep();
		      }
		}

		display.dispose();
		
	}
	
	public void settings(Shell shell) {
		Menu menuBar, helpMenu, gameRules, tankColor, gunType; 
		MenuItem helpMenuHeader, helpGetHelpItem, gameRulesHeader;
		MenuItem tankColorHeader, gunTypeHeader;
		menuBar = new Menu(shell, SWT.BAR);

	    helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
	    helpMenuHeader.setText("Help");
	    helpMenu = new Menu(shell, SWT.DROP_DOWN);
	    helpMenuHeader.setMenu(helpMenu);

	    helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
	    helpGetHelpItem.setText("Get Help");

		helpGetHelpItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("User asked for Help");
				System.out.println("So far no popup implemented, {w a s d} or arrow keys for movement");
				System.out.println("f or left mouse click for firing.");
			}
			
		});

		gameRulesHeader = new MenuItem(menuBar, SWT.CASCADE);
	    gameRulesHeader.setText("Game Rules");
	    gameRules = new Menu(shell, SWT.DROP_DOWN);
	    gameRulesHeader.setMenu(gameRules);

		tankColorHeader = new MenuItem(menuBar, SWT.CASCADE);
	    tankColorHeader.setText("Color");
	    tankColor = new Menu(shell, SWT.DROP_DOWN);
	    tankColorHeader.setMenu(tankColor);

		Colors red = new Colors("Red", SWT.COLOR_RED);
		red.menu(tankColor);
		Colors blue = new Colors("Blue", SWT.COLOR_BLUE);
		blue.menu(tankColor);
		Colors green = new Colors("Green", SWT.COLOR_GREEN);
		green.menu(tankColor);
		Colors grey = new Colors("Gray", SWT.COLOR_GRAY);
		grey.menu(tankColor);
		Colors cyan = new Colors("Cyan", SWT.COLOR_CYAN);
		cyan.menu(tankColor);
		Colors darkGreen = new Colors("Default", SWT.COLOR_DARK_GREEN);
		darkGreen.menu(tankColor);

		gunTypeHeader = new MenuItem(menuBar, SWT.CASCADE);
	    gunTypeHeader.setText("Gun");
	    gunType = new Menu(shell, SWT.DROP_DOWN);
	    gunTypeHeader.setMenu(gunType);

		Guns fast = new Guns("Fast Cannon", SWT.COLOR_BLACK);
		fast.menu(gunType);
		Guns slow = new Guns("Slow Cannon", SWT.COLOR_DARK_GRAY);
		slow.menu(gunType);

		shell.setMenuBar(menuBar);
	}

	public void setDir(int dirX,int dirY) {
		directionX = dirX;
		directionY = dirY;
		x += directionX;
		y += directionY;
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int[] getDim() {
		int[] d = new int[2];
		d[0] = width;
		d[1] = height;
		return d;
	}

	public int[] getDir() {
		int[] d = new int[2];
		d[0] = directionX;
		d[1] = directionY;
		return d;
	}
	
	public int[] getLoc() {
		int[] d = new int[2];
		d[0] = x;
		d[1] = y;
		return d;
	}
	
	public int getColor() {
		return this.color;
	}
	
	void fired(ObjectSerialize bull) throws IOException {
		out.write(ser.obToByte(bull));
		out.flush();
	}
	
	void gotHit() {
		hp--;
	}
	
	
	class Runner implements Runnable
	{
		public void run() 
		{
			try {
				if ((in.available() > 0) && (ended == false))
				{
					ObjectSerialize obj = ser.byteToOb(in.readNBytes(189));
					System.out.println(obj);
					if (obj.name().equals("endg")) {
						ended = true;
						System.out.println("Game ended");
						start = 0;
						shell.close();
						reset();
						waiting();
					}
					if (obj.name().equals("Tank") || (obj.name().equals("bull"))) {
						if (obj.name().equals("Tank")) {
							if (obj.id() == id) {
								x = obj.x();
								y = obj.y();
							}
							System.out.println(obj.getStatus());
							if (obj.getStatus() <= 0) {
								tanks.remove(obj.id());
								if (tanks.size() == 1 && tanks.containsKey(id) && hp > 0) {
									win = true;
									canvas.redraw();
								}
								ObjectSerialize end = new ObjectSerialize("endg", color, color, color, color, color, color, color, color, color, color);
								out.write(ser.obToByte(end));
								out.flush();
							}
							else {
								tanks.put(obj.id(), obj);
							}
							canvas.redraw();
						}
						
						if ((obj.name().equals("bull"))) {
							bullets.put(obj.hashCode(), obj);
						}
					}
					else {
						if (obj.name().equals("null")) {
							System.out.println("Player " + obj.id() + " Disconnected");
							tanks.remove(obj.id());
							canvas.redraw();
						}
					}
				}
			}
			catch(IOException | ClassNotFoundException | InterruptedException ex) {
				System.out.println("The server did not respond (async).");
			}
            display.timerExec(100, this);
		}
	};	
	
    private class Bullets implements Runnable {
    	
		@Override
		public void run() {
			if (ended == false) {
				@SuppressWarnings("rawtypes")
				Iterator bIt = new BulletIterator(bullets).iterator();
				
				while (bIt.hasNext()) {
					ObjectSerialize curr = (ObjectSerialize) bIt.next();
					curr.setXY(curr.x() + curr.dirX() * 2, curr.y() + curr.dirY() * 2);
					bounds.check(curr);
					if (curr.getStatus() == 0){
						System.out.println("Removing Bullet: " + curr);
						bullets.remove(curr.hashCode());

					}
					if (curr.getStatus() == -1) {
						bullets.remove(curr.hashCode());
						hp--;
					}
				}
				if (hp == 0) {
					loss = true;
					tanks.remove(id);
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
					try {
						System.out.println("Sending out Object of Length: " + ser.obToByte(obj).length);
						out.write(ser.obToByte(obj));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				
				canvas.redraw();
				display.timerExec(100, this);
			}
		}
    	
    	
    }

	// Color factory for menu
	// Quanwei Lei
	private class Colors {
		private String name = "Color Name";
		private int c;
		
		private Colors(String name, int color) {
			this.name = name;
			this.c = color;
		}
		
		private void menu(Menu colorMenu) {
			MenuItem item = new MenuItem(colorMenu, SWT.PUSH);
			item.setText(name);
			item.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					System.out.println("Changing tank color to: " + name);
					color = c;
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
					try {
						out.write(ser.obToByte(obj));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	// Gun factory for menu
	// Quanwei Lei
	private class Guns {
		private String name = "Color Name";
		private int c;
		
		private Guns(String name, int color) {
			this.name = name;
			this.c = color;
		}
		
		private void menu(Menu colorMenu) {
			MenuItem item = new MenuItem(colorMenu, SWT.PUSH);
			item.setText(name);
			item.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					System.out.println("Changing tank gun to: " + name);
					gun = c;
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
					try {
						out.write(ser.obToByte(obj));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	@SuppressWarnings("rawtypes")
	private class BulletIterator implements Iterable{
		private List<Object> state;
		private int currentSize;
		
		public BulletIterator(HashMap<Integer, ObjectSerialize> b) {
			state = Arrays.asList(b.values().toArray());
			currentSize = state.size();
		}
		
		
		@Override
	    public Iterator iterator() {
	        Iterator it = new Iterator() {
	            private int currentIndex = 0;

	            @Override
	            public boolean hasNext() {
	                return currentIndex < currentSize && state.get(currentIndex) != null;
	            }

	            @Override
	            public ObjectSerialize next() {
	            	currentIndex++;
	                return (ObjectSerialize) state.get(currentIndex - 1);
	            }

	            @Override
	            public void remove() {
	                throw new UnsupportedOperationException();
	            }
	        };
	        return it;
	    }
		
	}
	
}


