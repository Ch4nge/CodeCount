package samih.tiko.tamk.fi.codecount.login;

public class Token {

    /**
     * accessToken used to get data from wakatime
     */
    public static String accessToken;

    /**
     * Refresh token to refresh expired access token(NOT IMPLEMENTED)
     */
    private String refreshToken;

    /**
     * Expire time of token
     */
    private long expireTime;

    /**
     * Boolean to check if refresh token should be checked or not(NOT IMPLEMENTED)
     */
    private static boolean checkRefreshToken = false;

    /**
     * Inits Token
     * @param accessToken accessToken
     * @param refreshToken refreshToken
     * @param expires expiretime
     */
    public Token(String accessToken, String refreshToken, long expires) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = expires;
    }

    /**
     * empty constructor.
     */
    public Token() {}


    /**
     *
     * @return accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     *
     * @param accessToken accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     *
     * @return refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     *
     * @param refreshToken refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     *
     * @return expireTime
     */
    public long getExpireTime() {
        return expireTime;
    }

    /**
     *
     * @param expireTime expireTime
     */
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     *
     * @return checkRefreshToken
     */
    public static boolean isCheckRefreshToken() {
        return checkRefreshToken;
    }

    /**
     *
     * @param checkRefreshToken checkRefreshToken
     */
    public static void setCheckRefreshToken(boolean checkRefreshToken) {
        checkRefreshToken = checkRefreshToken;
    }
}

