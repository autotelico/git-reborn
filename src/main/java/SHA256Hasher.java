import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class SHA256Hasher {
    public static String encode(byte[] byteArray) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            byte[] hash = digester.digest(byteArray);
            String encodedHash = Base64.getEncoder().encodeToString(hash);
            return encodedHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
