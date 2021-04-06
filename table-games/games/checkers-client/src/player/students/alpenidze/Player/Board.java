package player.students.alpenidze.Player;

public class Board {
    public String from;
    public String to;
    public int point;

    public Board(int[][] from, int[][] to, int point) {
        this.from = gridToString(from);
        this.to = gridToString(to);
        this.point = point;
    }

    public Board(String from, String to, int point) {
        this.from = from;
        this.to = to;
        this.point = point;
    }

    private String gridToString(int[][] grid) {
        String s = "";
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++)
                s += grid[i][j];
        return s;
    }


}
