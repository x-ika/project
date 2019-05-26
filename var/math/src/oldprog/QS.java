package oldprog;

import java.util.Random;

public class QS {
    static int s[];
    private static final int INVALID_INDEX = -1;

    public static int findKey(int left, int right) {
        while(s[left] == s[left + 1] & left < right) {
            left++;
        }

        if(left != right) {
            return s[left] > s[left + 1] ? left : left + 1;
        }
        return  INVALID_INDEX;
    }

    public static int reshuffle(int left, int right, int key) {
        int x;
        if(key != INVALID_INDEX) {
                while(left < right) {
                    while(left < s.length - 1 & s[left] < key) {
                        left++;
                    }
                    while(right > 0 & s[right] >= key) {
                        right--;
                    }
                    if(left < right){
                        x = s[left];
                        s[left] = s[right];
                        s[right] = x;
                    }
                }
                return left;
        }
        return INVALID_INDEX;
    }

    public static void QuickSort(int left, int right) {
        while(left < right) {
            int middle = reshuffle(left, right,
                    s[findKey(left, right)]);
            if(middle > left - 1) {
            QuickSort(left, middle - 1);
            }
            left = middle;
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        s = new int[30];

        for(int i = 0; i < s.length; i++)  {
            s[i] = random.nextInt(1000);
        }

        QuickSort(0, s.length - 1);

        for(int i = 0; i < s.length; i++) {
            System.out.print(s[i] + " ");
        }
        System.out.println();
    }
}
