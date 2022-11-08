package XTankUI;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

public class Firing implements Command {
	private XTankUI ui;
	private static Firing f = null;
	
	public static Command getInstance() {
		if (f == null) {
			f = new Firing();
		}
		return f;
	}
	
	@Override
	public void connect(XTankUI ui) {
		this.ui = ui;
	}

	@Override
	public void set(KeyEvent e) {
	}
	
	public void set(MouseEvent e) {
		
	}

	@Override
	public void execute(String command) {
		// TODO Auto-generated method stub
		
	}
	
}
