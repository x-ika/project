package util;

public class Member implements Comparable<Member> {

    public static final Member IKA = new Member("ika", 2295, 280, 23);

    protected String name;
    protected int num;
    protected int rating;
    protected int volatility;

    public Member(String name, int rating, int volatility, int n) {
        this.name = name;
        this.rating = rating;
        this.volatility = volatility;
        this.num = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getVolatility() {
        return volatility;
    }

    public void setVolatility(int volatility) {
        this.volatility = volatility;
    }

    public Member copy() {
        return new Member(name, rating, volatility, num);
    }

    public int compareTo(Member o) {
        return name.compareTo(o.name);
    }

    public String toString() {
        return name + " " + rating + " " + volatility + " " + num;
    }
}
