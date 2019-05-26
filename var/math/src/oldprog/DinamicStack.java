package oldprog;

import java.io.*;

public class DinamicStack {
    public static class Stack {
        static final int SIZE = 10;
        private static int cursor, tail, head,
                memory[] = new int[SIZE];

        Stack() {
            head = 0;
            cursor = 0;
            tail = 0;
        }

        public void push(int item) throws IOException {
            if (head == tail) {
                RandomAccessFile raf = new RandomAccessFile("memory.txt", "rwd");
                raf.seek(cursor);
                while (Math.abs(head - tail) != SIZE / 2) {
                    raf.writeInt(memory[tail]);
                    tail = (tail + 1) % SIZE;
                }
                cursor += 2 * SIZE;
                raf.close();
            }
            memory[head] = item;
            head = (head + 1) % SIZE;
        }

        public int pop() throws IOException {
            if (tail == head) {
                RandomAccessFile raf = new RandomAccessFile("memory.txt", "rwd");
                cursor -= 2 * SIZE;
                raf.seek(cursor);
                while (Math.abs(head - tail) != SIZE / 2) {
                    try {
                        memory[head] = raf.readInt();
                        head = (head + 1) % SIZE;
                    } catch (IOException e) {
                        System.out.println("Error");
                    }
                }
                raf.setLength(cursor);
                raf.close();
            }
            head = (head + SIZE - 1) % SIZE;
            return memory[head];
        }
    }

    public static void main(String[] args) throws IOException {
        Stack stack = new Stack();
        for (int n = 0; n < 99; n++) {
            stack.push(n);
        }
        for (int n = 0; n < 99; n++) {
            System.out.println(stack.pop());
        }
    }
}
