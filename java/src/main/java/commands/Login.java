package commands;

import org.quteshell.Command;
import org.quteshell.Elevation;
import org.quteshell.Quteshell;

@Elevation(Elevation.DEFAULT)
public class Login implements Command {
    @Override
    public void execute(Quteshell shell, String arguments) {
        shell.writeln("Login time baby! lets go!", Quteshell.Color.LightRed);
    }
}
