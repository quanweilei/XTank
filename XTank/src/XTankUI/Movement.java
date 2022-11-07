package XTankUI;

import org.eclipse.swt.events.KeyEvent;

public class Movement implements Command{
    private XTankUI ui;
    private static Movement processor = new Movement();

    public static Command get() {
        return processor;
    }

    @Override
    public void connect(XTankUI ui) {
        this.ui = ui;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void set(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}
