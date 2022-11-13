package XTankUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/*
 * Quanwei Lei
 */
public class Movement implements Command{
    private XTankUI ui;
    private static Movement processor = new Movement();

    // get command
    public static Command get() {
        return processor;
    }
    
    // connect ui
    @Override
    public void connect(XTankUI ui) {
        this.ui = ui;
    }
    
    // executes command
    @Override
    public void execute(String command) {
        // TODO: possibly add diagonal movement, currently there is only rigid left right down up movement
        if (command.equals("nothing")) {
            System.out.println("False Input");
            return;
        }
        int[] dir = ui.getDir();
        int[] dim = ui.getDim();
        if (command.equals("right")) {
            if ((dir[0] != 10) && (dir[0] != -10)) {
                ui.setDimensions(dim[1], dim[0]);
            }
            ui.setDir(10, 0);
        }
        if (command.equals("left")) { 
            if ((dir[0] != 10) && (dir[0] != -10)) {
                ui.setDimensions(dim[1], dim[0]);
            }
            ui.setDir(-10, 0);
        }
        if (command.equals("up")) {
            if ((dir[1] != 10) && (dir[1] != -10)) {
                ui.setDimensions(dim[1], dim[0]);
            }
            ui.setDir(0, -10);
        }
        if (command.equals(("down"))) {
            if ((dir[1] != 10) && (dir[1] != -10)) {
                ui.setDimensions(dim[1], dim[0]);
            }
            ui.setDir(0, 10);
        }
    }
    
    // sets the keyevent, processes into command, then executes
    @Override
    public void set(KeyEvent e) {
        String command = "nothing";
        if ((e.keyCode == 16777220) || (e.character == 'd') || (e.character == 'D')){
            command = "right";
        }
        if ((e.keyCode == 16777219) || (e.character == 'a') || (e.character == 'A')){
            command = "left";  
        }
        if ((e.keyCode == 16777217) || (e.character == 'w') || (e.character == 'W')){
            command = "up";
        }
        if ((e.keyCode == 16777218) || (e.character == 's') || (e.character == 'S')){
            command = "down";
        }
        this.execute(command);

    }
}
