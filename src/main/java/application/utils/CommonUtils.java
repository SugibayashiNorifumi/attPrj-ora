package application.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p>Common utilities</p>
 */
public final class CommonUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    private CommonUtils() { };

    public static String getToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token;
    }
}
