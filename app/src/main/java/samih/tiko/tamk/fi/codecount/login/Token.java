package samih.tiko.tamk.fi.codecount.login;

public class Token {

    public static String accessToken;

    private String refreshToken;

    private long expireTime;

    private static boolean checkRefreshToken = false;

    public Token(String accessToken, String refreshToken, long expires) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = expires;
    }

    public Token() {}


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public static boolean isCheckRefreshToken() {
        return checkRefreshToken;
    }

    public static void setCheckRefreshToken(boolean checkRefreshToken) {
        checkRefreshToken = checkRefreshToken;
    }
}

