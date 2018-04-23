package samih.tiko.tamk.fi.codecount.leaderboard;

public class LeaderboardDataUnit {

    /**
     * Rank of user
     */
    private String rank;
    /**
     * Name of user
     */
    private String name;
    /**
     * Coding time of user
     */
    private String codingtime;


    /**
     * Constructor that inits LeaderboardDataUnit
     * @param rank rank of user
     * @param name name of user
     * @param codingtime coding time of user
     */
    public LeaderboardDataUnit(String rank, String name, String codingtime) {
        this.rank = rank;
        this.name = name;
        this.codingtime = codingtime;
    }


    /**
     *
     * @return rank of user
     */
    public String getRank() {
        return rank;
    }

    /**
     *
     * @param rank rank of user
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     *
     * @return name of user
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name of user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return coding time of user
     */
    public String getCodingtime() {
        return codingtime;
    }

    /**
     *
     * @param codingtime coding time of user
     */
    public void setCodingtime(String codingtime) {
        this.codingtime = codingtime;
    }
}
