package samih.tiko.tamk.fi.codecount.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import samih.tiko.tamk.fi.codecount.R;
import samih.tiko.tamk.fi.codecount.stat.TodayActivity;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWakatimeData();
        checkToken();
    }

    public void signIn(View view){
        String id = "FAQJtnVpD4yAn87UMkcGMoOQ";
        String uri = "codecount://login";

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("wakatime.com")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", id)
                .appendQueryParameter("scope", "email, read_logged_time, read_stats")
                .appendQueryParameter("redirect_uri", uri)
                .appendQueryParameter("response_type", "token");

        Intent browser = new Intent(Intent.ACTION_VIEW, uriBuilder.build());
        startActivity(browser);
    }


    private void checkToken() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("token", null);
        Token.accessToken = token;

        long expireTime = prefs.getLong("expires", 0);
        String refreshToken = prefs.getString("refreshToken", null);

        if (token != null && expireTime != 0 && refreshToken != null && new Date().getTime() < expireTime) {
            Intent intent = new Intent(this, TodayActivity.class);
            startActivity(intent);
        }
    }

    private void getWakatimeData() {
        Uri data = this.getIntent().getData();

        if (data != null && data.getScheme().equals("codecount") && data.getFragment() != null) {
            Token token = parseUrl(data.getFragment());

            if (token.getAccessToken() != null && token.getRefreshToken() != null && token.getExpireTime() != 0) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putString("token", token.getAccessToken()).apply();
                prefs.edit().putString("refreshToken", token.getRefreshToken()).apply();
                prefs.edit().putLong("expires", token.getExpireTime()).apply();

                Token.setCheckRefreshToken(false);
                Intent intent = new Intent(this, TodayActivity.class);
                startActivity(intent);
            }
        }
    }


    public static Token parseUrl(String url) {
        Token token = new Token();

        String[] urlSplitted = url.split("&");

        for (String content : urlSplitted) {
            String[] urlContent = content.split("=");

            if (urlContent[0].equals("access_token")) {
                token.setAccessToken(urlContent[1]);
            } else if (urlContent[0].equals("refresh_token")) {
                token.setRefreshToken(urlContent[1]);
            } else if (urlContent[0].equals("expires_in")) {
                String expiryString = urlContent[1];
                token.setExpireTime(getExpireDate(expiryString));
            }
        }
        return token;
    }

    public static long getExpireDate(String expireDate) {
        long expiresInMillis = TimeUnit.SECONDS.toMillis(Long.parseLong(expireDate));
        return expiresInMillis + new Date().getTime();
    }

}
