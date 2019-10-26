import commands.Login;
import org.quteshell.Quteshell;
import org.quteshell.commands.*;

import java.net.ServerSocket;
import java.util.ArrayList;

public class Main {
    private static final int PORT = 9837;
    private static final ArrayList<Quteshell> quteshells = new ArrayList<>();

    private static boolean listening = true;

    public static void main(String[] args) {
        Quteshell.Configuration.Commands.remove(Welcome.class);
        Quteshell.Configuration.Commands.remove(Echo.class);
        Quteshell.Configuration.Commands.remove(Clear.class);
        Quteshell.Configuration.Commands.add(commands.Login.class);
        Quteshell.Configuration.Commands.add(commands.Upload.class);
        Quteshell.Configuration.Commands.add(commands.Welcome.class);
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (listening) {
                quteshells.add(new Quteshell(serverSocket.accept()));
            }
        } catch (Exception e) {
            System.out.println("Host - " + e.getMessage());
        }
    }
}
