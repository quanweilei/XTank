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

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 300;
	private int y = 500;
	private int directionX = 0;
	private int directionY = -10;
	private int color;
	private int gun;
	private int width;
	private int height;
	private static int id;

	private Canvas canvas;
	private Display display;
	
	DataInputStream in; 
	DataOutputStream out;

	private Serializer ser;

	private Command moveHandler;
	private Firing fireHandler;

	private static HashMap<Integer, ObjectSerialize> tanks;
	private static HashMap<Integer, ObjectSerialize> bullets;
	
	
	public XTankUI(DataInputStream in, DataOutputStream out, int id /* , int startx, int starty*/)
	{
		this.in = in;
		this.out = out;
		this.id = id;
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
		//moveHandler = Movement.get();
	}
	
	public void start()
	{
		display = new Display();
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
				int midX = ((2 * currx + cWidth)/2) - 25;
				int midY = ((2 * curry + cHeight)/2) - 25;
				event.gc.fillOval(midX, midY, 50, 50);
				event.gc.setLineWidth(4);
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
						
						ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height);
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

		try {
			ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height);
			System.out.println(ser.obToByte(obj).length);
			out.write(ser.obToByte(obj));
		}
		catch(IOException ex) {
			System.out.println("The server did not respond (initial write).");
			System.out.println(ex);
		}				
		Runnable runnable = new Runner();
		display.asyncExec(runnable);

		shell.open();
		while (!shell.isDisposed()) 
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();		
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
		public synchronized void run() 
		{
			try {
				if (in.available() > 0)
				{
					ObjectSerialize obj = ser.byteToOb(in.readNBytes(176));
					if (!obj.name().equals("null")) {
						if (obj.name().equals("Tank")) {
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
            display.timerExec(150, this);
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
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height);
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
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionX, directionY, id, width, height);
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


