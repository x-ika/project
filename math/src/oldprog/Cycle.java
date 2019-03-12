package oldprog;

import java.lang.*;

public class Cycle {
    static boolean chackcycle(read.ListNode node1) {
        if(node1.next == null) {
            return false;
        }
        read.ListNode node2 = node1.next;
        boolean forward = true;
        while(node1 != node2 && node2 != null) {
            forward = !forward;
            node2 = node2.next;
            if(forward) {
                node1 = node1.next;
            }
        }
        return node1 == node2;
    }

    public static void main(String args[]) {
        if(chackcycle(read.list())) {
            System.out.println("Cyclic");
        } else {
            System.out.println("Not cyclic");
        }
    }
}
