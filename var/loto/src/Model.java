import java.util.*;

public class Model {

    private int n, k;

    private List<Integer> numbers;

    private List<int[][]> data;

    private List<Integer> idle;

    public Model() {
        numbers = new ArrayList<>();
        data = new ArrayList<>();
        idle = new ArrayList<>();
    }

    //-----------------------------------------------------------------------------------
    // query operations

    public int getSize() {
        return numbers.size();
    }

    public int getN() {
        return n;
    }

    public int getK() {
        return k;
    }

    public int getNumber(int time) {
        return numbers.get(time);
    }

    public int getNumOfRareNums(int time) {
        return data.get(time)[0].length;
    }

    public int getRareNumber(int time, int ind) {
        return data.get(time)[0][ind];
    }

    public int getTimePassed(int time, int ind) {
        return data.get(time)[1][ind];
    }

    public int getIdleTime(int time) {
        return idle.get(time);
    }

    public int[][] getRow(int time) {
        return data.get(time);
    }

    //-----------------------------------------------------------------------------------
    // update operations

    public void setN(int n) {
        this.n = n;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void replace(int time, int num) {
        numbers.set(time, num);
        update();
    }

    public void add(int num) {
        numbers.add(num);
        update();
        Loto.instance.setMax();
    }

    public void clear() {
        numbers.clear();
        update();
    }

    public void update() {
        boolean rowAdded = numbers.size() == idle.size() + 1;

        Map<Integer, Integer> timeByNumber = new HashMap<>();
        TreeMap<Integer, Integer> numberByTime = new TreeMap<>();
        data.clear();
        idle.clear();
        for (int i = 0; i < numbers.size(); i++) {
            // 1) read the next number
            int nextNumber = numbers.get(i);

            // 2) update maps
            Integer lastOccurenceTime = timeByNumber.get(nextNumber);
            timeByNumber.put(nextNumber, i);

            boolean isRare = lastOccurenceTime == null;
            if (lastOccurenceTime != null) {
                numberByTime.remove(lastOccurenceTime);
            }
            numberByTime.put(i, nextNumber);

            int[][] row = new int[2][Math.max(k, n - timeByNumber.size())];
            int ind = 0;
            for (int num = 1; num <= n; num++) {
                if (!timeByNumber.containsKey(num)) {
                    row[0][ind] = num;
                    row[1][ind++] = i + 1;
                }
            }
            int key = -1;
            for (; ind < row[0].length; ind++) {
                key = numberByTime.higherKey(key);
                row[0][ind] = numberByTime.get(key);
                row[1][ind] = i - key;
            }
            data.add(row);

            // 3) update idle time
            if (i == 0) {
                isRare = true;
            } else {
                for (int j = 0; j < k; j++) {
                    if (nextNumber == getRareNumber(i - 1, j)) {
                        isRare = true;
                        break;
                    }
                }
            }
            idle.add(isRare ? 1 : idle.get(i - 1) + 1);

        }

        if (rowAdded) {
            Loto.instance.rowAdded();
        } else {
            Loto.instance.updateTable();
        }
    }

}
