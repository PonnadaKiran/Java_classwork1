import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TinyURLGenerator {
    private static final String BASE_URL = "http://tinyurl.com/";
    private static final String HASH_ALGORITHM = "SHA-256";

    public static String generateTinyURL(String longURL) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(longURL.getBytes());
            String hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
            return BASE_URL + hash.substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String longURL = "https://www.example.com/some/long/url/that/we/want/to/shorten";
        String tinyURL = generateTinyURL(longURL);
        System.out.println("Tiny URL for " + longURL + ": " + tinyURL);
    }
}
