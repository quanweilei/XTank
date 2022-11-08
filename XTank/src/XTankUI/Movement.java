package XTankUI;

import org.eclipse.swt.SWT;
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
    public void execute(String command) {
        // TODO: possibly add diagonal movement, currently there is only rigid left right down up movement
        if (command.equals("nothing")) {
            System.out.println("False Input");
            return;
        }
        if (command.equals("right")) {
            System.out.println("Right");
        }
        if (command.equals("left")) {
            System.out.println("Left"); 
        }
        if (command.equals("up")) {
            System.out.println("Up");
        }
        if (command.equals(("down"))) {
            System.out.println("Down");
        }
    }

    @Override
    public void set(KeyEvent e) {
        System.out.println(e.keyCode);
        String command = "nothing";
        if ((e.keyCode == 16777220) || (e.character == 'd')){
            command = "right";
        }
        if ((e.keyCode == 16777219) || (e.character == 'a')){
            command = "left";  
        }
        if ((e.keyCode == 16777217) || (e.character == 'w')){
            command = "up";
        }
        if ((e.keyCode == 16777218) || (e.character == 's')){
            command = "down";
        }
        this.execute(command);
    }
}
