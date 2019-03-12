package player;

import main.Turn;

import java.util.*;

public class Computer extends BasicPlayer {
    public Turn makeTurne(int[][] d, int value) {
        copyDesk(d);
        List<Turn> bestTurns = findBestTurnes(value - 1), someTurnes = new ArrayList<>();
        for(Turn turn : bestTurns) {
            if(turn.first.value < 3) {
                someTurnes.add(turn);
            }
        }
        List<Turn> list = someTurnes.isEmpty()? bestTurns : someTurnes;
        return list.get(new Random().nextInt(list.size()));
    }

    private List<Turn> findBestTurnes(int value) {
        int best[] = new int[10];
        List<Turn> bestTurns = new ArrayList<>();
        result = 0;
        best[0] = -100;
        List turne0 = countTurnes(value + 1);
        for(Object o0 : turne0) {
            Turn t0 = (Turn) o0;
            forward(t0);
            best[1] = 100;
            List turne1 = countTurnes(2 - value);
            for(Object o1 : turne1) {
                Turn t1 = (Turn) o1;
                forward(t1);
                best[2] = -100;
                List turne2 = countTurnes(value + 1);
                for(Object o2 : turne2) {
                    Turn t2 = (Turn) o2;
                    forward(t2);
                    best[3] = 100;
                    List turne3 = countTurnes(2 - value);
                    for(Object o3 : turne3) {
                        Turn t3 = (Turn) o3;
                        forward(t3);
                        best[4] = -100;
                        List turne4 = countTurnes(value + 1);
                        for(Object o4 : turne4) {
                            Turn t4 = (Turn) o4;
                            forward(t4);
                            best[5] = 100;
                            List turne5 = countTurnes(2 - value);
                            for(Object o5 : turne5) {
                                Turn t5 = (Turn) o5;
                                forward(t5);
                                result = value != 0? result : -result;
                                best[5] = Math.min(best[5], result);
                                result = value != 0? result : -result;
                                back(t5);
                            }
                            best[4] = Math.max(best[4], best[5]);
                            back(t4);
                        }
                        best[3] = Math.min(best[3], best[4]);
                        back(t3);
                    }
                    best[2] = Math.max(best[2], best[3]);
                    back(t2);
                }
                best[1] = Math.min(best[1], best[2]);
                back(t1);
            }
            if(best[0] <= best[1]) {
                if(best[0] < best[1]) {
                    bestTurns.clear();
                }
                best[0] = best[1];
                bestTurns.add(t0);
            }
            back(t0);
        }
//        System.out.println(best[0]);
        return bestTurns;
    }
}
