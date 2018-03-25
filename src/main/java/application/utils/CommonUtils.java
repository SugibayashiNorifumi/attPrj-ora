package application.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * <p>
 * Common utilities
 * </p>
 */
public final class CommonUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** タイムゾーン：日本。 */
    public static final TimeZone JST = TimeZone.getTimeZone("JST");

    /**
     * 非公開コンストラクタ。
     */
    private CommonUtils() {
    }

    public static String getToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token;
    }

    /**
     * 本日をyyyyMMdd形式で取得する。
     * @return 変換後文字列
     */
    public static String toYyyyMmDd() {
        return DateFormatUtils.format(new Date(), "yyyyMMdd", JST);
    }

    /**
     * 日時をM/d H:mm形式で取得する。
     * @param datetime 変換する日時
     * @return 変換後文字列
     */
    public static String toMDhMm(Date datetime) {
        return DateFormatUtils.format(datetime, "M/d H:mm", JST);
    }

    /**
     * 日時をH:mm形式で取得する。
     * @param time 変換する時刻
     * @return 変換後文字列
     */
    public static String toHMm(Date time) {
        return DateFormatUtils.format(time, "H:mm", JST);
    }
}
