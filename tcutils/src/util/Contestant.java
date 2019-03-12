package util;

public class Contestant extends Member {

    private int rank;

    public Contestant(String name, int rating, int volatility, int n, int rank) {
        super(name, rating, volatility, n);
        this.rank = rank;
    }

    public Contestant(Member member, int rank) {
        super(member.getName(), member.getRating(), member.getVolatility(), member.getNum());
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Contestant copy() {
        return new Contestant(name, rating, volatility, num, rank);
    }

}
