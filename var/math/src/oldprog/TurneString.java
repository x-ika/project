package oldprog;

import java.io.IOException;

public class TurneString {
    public static void read(char y)throws IOException {
        char x;
        x = (char) System.in.read();
        if (x != ' ' && y != ' ') {
            read(x);
        }
        System.out.print(x);
    }

    public static void main(String[] args)throws IOException {
        char c = (char) System.in.read();
        read(c);
        System.out.print(c);
    }
}

