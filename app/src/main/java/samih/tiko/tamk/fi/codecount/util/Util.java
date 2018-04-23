package samih.tiko.tamk.fi.codecount.util;

/**
 * Utils of project
 */
public class Util {

    /**
     * returns random number, used for developing and testing
     * @param range range of random number
     * @param startsfrom first possible number of random value
     * @return random number
     */
    public static float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }
}
