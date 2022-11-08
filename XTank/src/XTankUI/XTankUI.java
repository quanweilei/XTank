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
	private static int id;

	private Canvas canvas;
	private Display display;
	
	DataInputStream in; 
	DataOutputStream out;

	private Serializer ser;

	private Command moveHandler;

	private static HashMap<Integer, ObjectSerialize> tanks;
	
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
		tanks = new HashMap<>();
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
			System.out.println(tanks);
			for (Integer id: tanks.keySet()) {
				ObjectSerialize curr = tanks.get(id);
				int currx = curr.x();
				int curry = curr.y();
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				event.gc.fillRectangle(currx, curry, 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(currx, curry+25, 50, 50);
				event.gc.setLineWidth(4);
				event.gc.drawLine(currx+25, curry+25, currx+25, curry-15);
			}
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				System.out.println("key " + e.character);
				// update tank location
				//moveHandler.set(e);
				x += directionX;
				y += directionY;
				try {
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionY, directionX, id);
					out.write(ser.obToByte(obj));
				}
				catch(IOException ex) {
					System.out.println("The server did not respond (write KL).");
				}

				canvas.redraw();
			}
			public void keyReleased(KeyEvent e) {}
		});

		try {
			ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionY, directionX, id);
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
	
	class Runner implements Runnable
	{
		public void run() 
		{
			try {
				if (in.available() > 0)
				{
					ObjectSerialize obj = ser.byteToOb(in.readNBytes(151));
					tanks.put(obj.id(), obj);
					canvas.redraw();
				}
			}
			catch(IOException | ClassNotFoundException ex) {
				System.out.println("The server did not respond (async).");
			}				
            display.timerExec(150, this);
		}
	};	

	public void setColor(int color) {
		this.color = color;
	}

	public void setGun(int gun) {
		this.gun = gun;
	}
}


