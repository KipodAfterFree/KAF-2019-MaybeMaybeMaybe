package commands;

import org.quteshell.Command;
import org.quteshell.Elevation;
import org.quteshell.Quteshell;

@Elevation(Elevation.ALL)
public class Welcome implements Command {
    @Override
    public void execute(Quteshell shell, String arguments) {
        shell.writeln("╔═══════════════════════════════════╗");
        shell.writeln("║       Mommy, sign my paper!       ║");
        shell.writeln("║ You can type 'help' for commands. ║");
        shell.writeln("╚═══════════════════════════════════╝");
    }
}
