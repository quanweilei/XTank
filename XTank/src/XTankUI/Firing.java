package XTankUI;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import Serializer.ObjectSerialize;
/*
 * Ellie Martin Quanwei Lei
 * Firing handles all of the firing comands done by the client.
 */
public class Firing implements Command {
	private XTankUI ui;
	private static Firing f = null;
	private int x;
	private int y;
	
	// returns an instance of Firing
	public static Command getInstance() {
		if (f == null) {
			f = new Firing();
		}
		return f;
	}
	
	// connect to UI
	@Override
	public void connect(XTankUI ui) {
		this.ui = ui;
	}
	
	// set Keyevent
	@Override
	public void set(KeyEvent e) {
		if ((e.character == 'f') || (e.character == 'F') || (e.character == SWT.SPACE)){
			this.execute("fire");
		}
	}
	
	// set mouseevent
	public void set(MouseEvent e) {
		System.out.println("mouseDown in canvas at " + e.x + ", " + e.y);
		this.execute("fire");
	}
	
	// execute firing
	@Override
	public void execute(String command) {
		int[] l = ui.getLoc();
		x = l[0];
		y = l[1];
		int[] d = ui.getDir();
		int dirX = d[0];
		int dirY = d[1];
		int[] s = ui.getDim();
		int width = s[0];
		int height = s[1];
		ObjectSerialize bullobj = new ObjectSerialize("bull", x, y, ui.getColor(), 0, dirX, dirY, -1, width, height, 1);
		bullobj.setStatus(1);
		try {
			ui.fired(bullobj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(command);
	}
	
}
