package oldprog;

public class Turne {
    public static void main(String args[]) {
        read.ListNode middle = read.list(),
                      first = null,
                      last = middle.next;

        while(last != null) {
            middle.next = first;
            first = middle;
            middle = last;
            last = last.next;
        }
    }
}

