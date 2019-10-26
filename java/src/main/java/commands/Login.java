package commands;

import com.google.common.hash.Hashing;
import org.json.JSONObject;
import org.quteshell.Command;
import org.quteshell.Elevation;
import org.quteshell.Quteshell;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

@Elevation(Elevation.DEFAULT)
public class Login implements Command {

    private static ArrayList<String[]> keys = new ArrayList<>();

    @Override
    public void execute(Quteshell shell, String arguments) {
        if (arguments == null) {
            shell.writeln("login usage - 'login sessionID'");
        } else {
            try {
                String id = findUID(arguments);
                if (id != null) {
                    shell.writeln("Login as uid:" + id);
                    keys.add(new String[]{shell.getIdentifier(), id, findKey(id)});
                    shell.setElevation(2);
                }
            } catch (Exception e) {
            }
        }
    }

    private static String findUID(String sid) throws IOException {
        File sessions = new File("/var/www/html/files/authenticate/sessions.json");
        JSONObject sess = new JSONObject(new String(Files.readAllBytes(sessions.toPath())));
        for (String key : sess.keySet()) {
            if (key.equals(auth_hash(sid, sess.getString(key), 1024))) {
                return sess.getString(key);
            }
        }
        return null;
    }

    private static String findKey(String uid) throws IOException {
        File keyf = new File("/var/www/html/files/mmm/keys/" + uid + ".key");
        if (keyf.exists())
            return new String(Files.readAllBytes(keyf.toPath()));
        return null;
    }

    private static String auth_hash(String secret, String salt, int onion) {
        if (onion == 0)
            return Hashing.sha256().hashString(secret + salt, StandardCharsets.UTF_8).toString();
        String layer = auth_hash(secret, salt, onion - 1);
        return Hashing.sha256().hashString(onion % 2 == 0 ? layer + salt : salt + layer, StandardCharsets.UTF_8).toString();
    }

    public static String getKey(String shid) {
        for (String[] a : keys) {
            if (a[0].equals(shid))
                return a[2];
        }
        return null;
    }

    public static String getID(String shid) {
        for (String[] a : keys) {
            if (a[0].equals(shid))
                return a[1];
        }
        return null;
    }
}
