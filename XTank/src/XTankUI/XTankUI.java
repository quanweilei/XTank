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

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 300;
	private int y = 500;
	private int directionX = 0;
	private int directionY = -10;
	private int color;
	private int gun;

	private Canvas canvas;
	private Display display;
	
	DataInputStream in; 
	DataOutputStream out;

	private Serializer ser;

	private Command moveHandler;
	
	public XTankUI(DataInputStream in, DataOutputStream out)
	{
		this.in = in;
		this.out = out;
		color = SWT.COLOR_DARK_GREEN;
		gun = SWT.COLOR_BLACK;
		ser = Serializer.getInstance();
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
			event.gc.fillRectangle(canvas.getBounds());
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(x, y, 50, 100);
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(x, y+25, 50, 50);
			event.gc.setLineWidth(4);
			event.gc.drawLine(x+25, y+25, x+25, y-15);
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
					ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionY, directionX);
					out.write(ser.obToByte(obj));
					System.out.println(ser.obToByte(obj).length);
				}
				catch(IOException ex) {
					System.out.println("The server did not respond (write KL).");
				}

				canvas.redraw();
			}
			public void keyReleased(KeyEvent e) {}
		});

		try {
			ObjectSerialize obj = new ObjectSerialize("Tank", x, y, color, gun, directionY, directionX);
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
					System.out.println("\n");
					canvas.redraw();
				}
			}
			catch(IOException ex) {
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


