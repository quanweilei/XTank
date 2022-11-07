package XTankUI;

import org.eclipse.swt.events.KeyEvent;

public interface Command {
    public void connect(XTankUI ui);
    public void set(KeyEvent e);
    public void execute();
}
