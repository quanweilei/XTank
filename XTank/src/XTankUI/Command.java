package XTankUI;

import org.eclipse.swt.events.KeyEvent;
/*
 * Quanwei Lei
 * interface for command
 */
public interface Command {
    public void connect(XTankUI ui);
    public void set(KeyEvent e);
    public void execute(String command);
}
