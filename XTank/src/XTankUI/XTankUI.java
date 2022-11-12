package XTankUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import Serializer.ObjectSerialize;
import Serializer.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 1500;
	private int y = 500;
	private int directionX = 0;
	private int directionY = -10;
	private int color;
	private int gun;
	private int width;
	private int height;
	private int hp;
	private static int id;
	private static ObjectSerialize myStat;

	private static Canvas canvas;
	private Display display;
	
	private DataInputStream in; 
	private DataOutputStream out;

	private Serializer ser;

	private Command moveHandler;
	private Firing fireHandler;

	private static HashMap<Integer, ObjectSerialize> tanks;
	private static HashMap<Integer, ObjectSerialize> bullets;
	
	
	public XTankUI(DataInputStream in, DataOutputStream out, int id, int start /* , int startx, int starty*/) throws IOException, InterruptedException
	{
		System.out.println("This Client is Player " + id);
		this.in = in;
		this.out = out;
		XTankUI.id = id;
		//x = startx;
		//y = starty;
		color = SWT.COLOR_DARK_GREEN;
		gun = SWT.COLOR_BLACK;
		ser = Serializer.getInstance();
		moveHandler = Movement.get();
		fireHandler = (Firing) Firing.getInstance();
		moveHandler.connect(this);
		fireHandler.connect(this);
		tanks = new HashMap<>();
		bullets = new HashMap<>();
		width = 50;
		height = 100;
		// TODO: OPTION FOR HP
		hp = 3;
		myStat = new ObjectSerialize("plyr", -1, -1, -1, -1, -1, -1, -1, -1, -1, start);
		out.write(ser.obToByte(myStat));
		System.out.println("Sending out status");
		display = new Display();
		
		if (myStat.getStatus() == 0) {
			this.waiting();
		}
		else {
			this.start();
		}
		//moveHandler = Movement.get();
	}
	
	public void start() throws InterruptedException
	{
		Shell shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());
		
		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		
		this.canvas.addPaintListener(event -> {
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
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.drawText("Player " + String.valueOf(id), midX, midY + cHeight);
			}
			
			// Ellie Martin
			for (Integer b: bullets.keySet()) {
				ObjectSerialize bull = bullets.get(b);
				int currx = bull.x();
				int curry = bull.y();
				int cDirX = bull.dirX();
				int cDirY = bull.dirY();
				int cWidth = bull.width();
				int cHeight = bull.height();
				int midX = ((2 * currx + cWidth)/2) - 25;
				int midY = ((2 * curry + cHeight)/2) - 25;
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(midX + cDirX*7 + 25, midY + cDirY*7 + 25, 10, 10);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				fireHandler.set(e);
			}
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if ((e.character == 'f') || (e.character == 'F')){
					fireHandler.set(e);
				}
				else {
					moveHandler.set(e);
					// update tank location
					try {
						
						ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
						out.write(ser.obToByte(obj));
					}
					catch(IOException ex) {
						System.out.println("The server did not respond (write KL).");
					}
				}
				
				canvas.redraw();
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		
		settings(shell);
		
		try {
			if (tanks.get(id) == null) {
				ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, hp);
				System.out.println("Sending out Object of Length: " + ser.obToByte(obj).length);
				out.write(ser.obToByte(obj));
			}
		}
		catch(IOException ex) {
			System.out.println("The server did not respond (initial write).");
			System.out.println(ex);
		}				
		Runnable runnable = new Runner();
		display.asyncExec(runnable);

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
		Shell start = new Shell(display);
		start.setBounds(900 , 600, 400, 400);
		start.setText("Waiting for Game to Start...");
		settings(start);
		Button button = new Button(start, SWT.PUSH);
		button.setText("Start Game");
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
					myStat = ob;
					System.out.println(ob);
				}
				out.write(ser.obToByte(myStat));
				if (myStat.getStatus() == 1) {
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
	
	protected void fired(ObjectSerialize bull) throws IOException {
		out.write(ser.obToByte(bull));
		out.flush();
	}
	
	
	class Runner implements Runnable
	{
		public void run() 
		{
			try {
				if (in.available() > 0)
				{
					ObjectSerialize obj = ser.byteToOb(in.readNBytes(189));
					System.out.println(obj);
					if (!obj.name().equals("null")) {
						if (obj.name().equals("Tank")) {
							if (obj.id() == id) {
								x = obj.x();
								y = obj.y();
							}
							tanks.put(obj.id(), obj);
						}
						
						if (obj.name().equals("bull")) {
							bullets.put(obj.id(), obj);
						}
					}
					else {
						System.out.println("Player " + obj.id() + " Disconnected");
						tanks.remove(obj.id());
					}
					canvas.redraw();
				}
			}
			catch(IOException | ClassNotFoundException ex) {
				System.out.println("The server did not respond (async).");
			}
            display.timerExec(100, this);
		}
	};	

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
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, 1);
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
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height, 1);
					try {
						out.write(ser.obToByte(obj));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}


