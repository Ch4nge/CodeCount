package samih.tiko.tamk.fi.codecount.leaderboard;

public class LeaderboardDataUnit {

    private String rank;
    private String name;
    private String codingtime;



    public LeaderboardDataUnit(String rank, String name, String codingtime) {
        this.rank = rank;
        this.name = name;
        this.codingtime = codingtime;
    }


    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodingtime() {
        return codingtime;
    }

    public void setCodingtime(String codingtime) {
        this.codingtime = codingtime;
    }
}
