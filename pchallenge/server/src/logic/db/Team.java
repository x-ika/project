package logic.db;

public class Team extends Record {

    private String name;
    private String password;

    public Team(String name, String password) {
        super(name);
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
