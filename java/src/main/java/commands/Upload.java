package commands;

import com.google.common.hash.Hashing;
import org.quteshell.Command;
import org.quteshell.Elevation;
import org.quteshell.Quteshell;
import org.quteshell.commands.Help;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

@Elevation(2)
@Help.Description("Usage - upload [File Name] [Private Digest] [Public Digest] [Message Base64] or upload [File Name] [Base64 Encoded MMM]")
public class Upload implements Command {
    @Override
    public void execute(Quteshell shell, String arguments) {
        String key = Login.getKey(shell.getIdentifier());
        String id = Login.getID(shell.getIdentifier());
        if (arguments != null) {
            if (key != null) {
                String[] split = arguments.split(" ", 4);
                if (split.length == 2) {
                    String[] parsed = mmm_parse(new String(Base64.getDecoder().decode(split[1])));
                    shell.execute("upload " + split[0] + " " + parsed[0] + " " + parsed[1] + " " + new String(Base64.getEncoder().encode(parsed[2].getBytes())));
                } else if (split.length == 4) {
                    String fileName = split[0];
                    String priDigest = split[1];
                    String pubDigest = split[2];
                    String message = new String(Base64.getDecoder().decode(split[3]));
                    if (mmm_verify(priDigest, pubDigest, message, key)) {
                        shell.writeln("Message signature verification passed", Quteshell.Color.LightGreen);
                        // TODO write some function that filters some of the fileName
                        File toWrite = new File("/var/www/html/upload/" + id + "/" + fileName);
                        shell.writeln("Writing file to '" + toWrite.getPath() + "'");
                        toWrite.mkdirs();
                        try {
                            Files.write(toWrite.toPath(), message.getBytes());
                        } catch (IOException e) {
                            shell.writeln("Error - " + e.getMessage(), Quteshell.Color.LightRed);
                        } finally {
                            shell.writeln("File written successfully.", Quteshell.Color.LightGreen);
                            shell.finish();
                        }
                    } else {
                        shell.writeln("Message signature verification failed", Quteshell.Color.LightRed);
                    }
                } else
                    shell.writeln("Parameter error", Quteshell.Color.LightRed);
            } else {
                shell.writeln("Missing key!", Quteshell.Color.LightRed);
            }
        } else {
            shell.writeln(getClass().getAnnotation(Help.Description.class).value());
        }
    }

    private static String[] mmm_parse(String mmm) {
        String[] lines = mmm.split("\n");
        if (lines.length >= 7) {
            if (lines[0].equals("---BEGIN MMM---") &&
                    lines[1].equals("---PRIVATE DIGEST---") &&
                    lines[3].equals("---PUBLIC DIGEST---") &&
                    lines[5].equals("---MESSAGE---") &&
                    lines[lines.length - 1].equals("---END MMM---")) {
                String data = "";
                for (int l = 6; l < lines.length - 1; l++) {
                    if (data.length() != 0) {
                        data += "\n";
                    }
                    data += lines[l];
                }
                return new String[]{lines[2], lines[4], data};
            }
        }
        return new String[0];
    }

    private static boolean mmm_verify(String priDig, String pubDig, String mess, String key) {
        return mmm_hmac_calculate(mess, key).equals(priDig) && mmm_hmac_calculate(mess, mmm_derive_key(key)).equals(pubDig);
    }

    private static String mmm_derive_key(String key) {
        String publicKey = "";
        for (int i = 0; i < key.length(); i += 2) {
            publicKey += key.charAt(i);
        }
        return publicKey;
    }

    public static String mmm_hmac_calculate(String message, String key) {
        return Hashing.hmacSha256(key.getBytes()).hashString(message, StandardCharsets.UTF_8).toString();
    }
}
