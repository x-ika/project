package oldprog;

import java.util.Random;

public class read {
    static class ListNode {
        int value;
        ListNode next;
    }

    static ListNode list() {
        Random random = new Random();
        ListNode listnode = new ListNode(),
                 firstnode = listnode,
                 middlenode = null;
        listnode.value = random.nextInt(100);

        int length = random.nextInt(100), j = -1;
        boolean cyclic = random.nextBoolean() & length > 1;
        if(cyclic) {
            j = random.nextInt(length);
        }

        for(int i = 0; i < length; i++) {
            if(i == j) {
                middlenode = listnode;
            }
            listnode.next = new ListNode();
            listnode = listnode.next;
            listnode.value = random.nextInt();
        }
        if(cyclic) {
            listnode.next = middlenode;
        }
        return(firstnode);
    }
}