package samih.tiko.tamk.fi.codecount.util;

public class Util {

    public static String apiUrl = "https://wakatime.com/api/v1";

    public static float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }
}
