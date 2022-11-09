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

    @Override
    public void set(KeyEvent e) {
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
