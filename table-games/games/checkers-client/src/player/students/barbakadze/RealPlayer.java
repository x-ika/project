package player.students.barbakadze;

import main.*;

import java.util.*;

public class RealPlayer implements Player {
    public void gameOver() {
    }

    static int[][] up = {{-1, -1}, {1, -1}};
    static int[][] down = {{1, 1}, {1, -1}};
    static int[][] direction = {
            {-1, -1, 1, 1}, {1, -1, 1, -1}
    };
    //int counter = 0;
    static int[][] pasuxi = new int[8][8];
    static int depth = 10;

    public Turn makeTurne(int[][] d, int value) {

        int a = -Integer.MAX_VALUE;
        int b = Integer.MAX_VALUE;


        int ans = alphabeta(d, depth, a, b, value);
//		System.out.println(ans);
        print(d);
        print(pasuxi);

        ArrayList<Checker> lst = new ArrayList<>();
        Checker start = new Checker(-1, -1, -1);
        ;
        Checker end = new Checker(-1, -1, -1);

        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[0].length; j++) {
                if (d[i][j] == value && pasuxi[i][j] == 0) {
                    start = new Checker(i, j, value);
                }
                if (d[i][j] == value + 3 && pasuxi[i][j] == 0) {
                    start = new Checker(i, j, value + 3);

                }

                if (d[i][j] == 0 && pasuxi[i][j] == value) {
                    end = new Checker(i, j, value);
                }
                if (d[i][j] == 0 && pasuxi[i][j] == value + 3) {
                    end = new Checker(i, j, value + 3);
                }


                if (d[i][j] == not(value) && pasuxi[i][j] == 0) {
                    lst.add(new Checker(i, j, not(value)));
                }

                if (d[i][j] == not(value) + 3 && pasuxi[i][j] == 0) {
                    lst.add(new Checker(i, j, not(value) + 3));
                }


            }
        }

        Turn answer = new Turn(start, end, lst);

        return answer;
    }

    public ArrayList<int[][]> neighbour(int[][] d, int value) {

        ArrayList<Checker> moves = findAllCell(d, value);

        int counter = 0;
        ArrayList<int[][]> arrList = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).value == 1) counter++;
        }


        for (int i = 0; i < moves.size(); i++) {
            if (counter != 0) {
                if (moves.get(i).value == 1) {

                    int x = moves.get(i).row;
                    int y = moves.get(i).col;
                    if (d[x][y] == value) generateNextMovesCheckerKiller(d, x, y, arrList, value);
                    else generateNextMovesQueenKiller(d, x, y, arrList, value);
                }
            } else {
                int x = moves.get(i).row;
                int y = moves.get(i).col;
                if (d[x][y] == value) {

                    generateNextMovesCheckerNotKiller(d, moves.get(i).row, moves.get(i).col, arrList, value);
                } else {

                    generateNextMovesQueenNotKiller(d, x, y, arrList, value);
                }
            }
        }

        //for (int i = 0; i < arrList.size(); i++) {
        //print(arrList.get(i));
        //}

        //counter = 0;

        return arrList;
    }


    public int alphabeta(int[][] board, int depth, int a, int b, int player) {
        //print(board);
        int ans = e(board, player);
        ArrayList<int[][]> arr = neighbour(board, player);

        if (depth == 0 || isFinish(board)) return ans;
        if (arr.size() == 0) {
            if (player == 1) return -1000;
            else return 1000;
        }


        if (player == 1) {

            for (int i = 0; i < arr.size(); i++) {
                int k = alphabeta(arr.get(i), depth - 1, a, b, not(player));
                //System.out.println(k);
                if (k > a) {

                    a = k;
                    if (this.depth == depth) pasuxi = copy(arr.get(i));
                }

                if (b <= a) {
                    break;
                }
            }
            return a;

        } else {
            for (int i = 0; i < arr.size(); i++) {
                int k = alphabeta(arr.get(i), depth - 1, a, b, not(player));
                if (k < b) {

                    b = k;
                    if (this.depth == depth) pasuxi = copy(arr.get(i));
                }
                if (b <= a) {
                    break;
                }
            }
            return b;

        }
    }


    public int not(int player) {
        if (player == 1) return 2;
        else return 1;
    }

    public boolean isFinish(int[][] board) {
        int w = 0, b = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 1) w++;
                if (board[i][j] == 4) w += 4;
                if (board[i][j] == 2) b++;
                if (board[i][j] == 5) b += 4;
            }
        }

        if (w == 0 || b == 0) return true;


        return false;
    }

    public void generateNextMovesCheckerKiller(int[][] board, int x, int y, ArrayList<int[][]> arrList, int value) {
        //System.out.println(x + " " + y);
        for (int k = 0; k < direction[0].length; k++) {
            int x1 = x + direction[0][k], y1 = y + direction[1][k];
            int x2 = x1 + direction[0][k], y2 = y1 + direction[1][k];

            if (!isInBound(x1, y1)) continue;
            if (!isInBound(x2, y2)) continue;

            if ((board[x1][y1] == not(value) || board[x1][y1] == not(value) + 3) && board[x2][y2] == 0) {

                board[x][y] = 0;
                board[x1][y1] = 0;
                if (value == 1 && x2 == 0) board[x2][y2] = value + 3;
                else if (value == 2 && x2 == 7) board[x2][y2] = value + 3;
                else board[x2][y2] = value;
                if (board[x2][y2] == value + 3) {
                    if (isKillerOneQueen(board, x2, y2, value))
                        generateNextMovesQueenKiller(board, x2, y2, arrList, value);
                    else {
                        int[][] copio = copy(board);
                        arrList.add(copio);
                    }

                } else {

                    boolean newKilling = isKillerOneChecker(board, x2, y2, value) ? true : false;
                    if (newKilling) generateNextMovesCheckerKiller(board, x2, y2, arrList, value);
                    else {
                        int[][] copio = copy(board);
                        arrList.add(copio);
                    }
                }
                board[x][y] = value;
                board[x1][y1] = not(value);
                board[x2][y2] = 0;
            }
        }
    }

    public void generateNextMovesCheckerNotKiller(int[][] board, int x, int y, ArrayList<int[][]> arrList, int value) {
        if (value == 1) {
            for (int k = 0; k < up[0].length; k++) {
                int x1 = x + up[0][k], y1 = y + up[1][k];
                if (!isInBound(x1, y1)) continue;
                if (board[x1][y1] == 0) {
                    board[x][y] = 0;
                    board[x1][y1] = value;
                    int[][] copio = copy(board);
                    arrList.add(copio);
                    board[x][y] = value;
                    board[x1][y1] = 0;
                }
            }
        } else {
            for (int k = 0; k < down[0].length; k++) {
                int x1 = x + down[0][k], y1 = y + down[1][k];
                if (!isInBound(x1, y1)) continue;
                if (board[x1][y1] == 0) {
                    board[x][y] = 0;
                    board[x1][y1] = value;
                    int[][] copio = copy(board);
                    arrList.add(copio);
                    board[x][y] = value;
                    board[x1][y1] = 0;
                }
            }


        }


    }


    public boolean isKillerOneChecker(int[][] board, int x, int y, int value) {

        for (int k = 0; k < direction[0].length; k++) {
            int x1 = x + direction[0][k], y1 = y + direction[1][k];
            int x2 = x1 + direction[0][k], y2 = y1 + direction[1][k];
            if (!isInBound(x1, y1)) continue;
            if (!isInBound(x2, y2)) continue;
            if (board[x1][y1] == not(value) && board[x2][y2] == 0) return true;

        }


        return false;
    }

    public boolean isKillerOneQueen(int[][] board, int x, int y, int value) {
        for (int k = 0; k < direction[0].length; k++) {
            int x1 = x + direction[0][k], y1 = y + direction[1][k];

            while (isInBound(x1, y1)) {
                if (board[x1][y1] % 3 == value) break;
                if (board[x1][y1] % 3 == not(value)) {
                    int secX = x1 + direction[0][k], secY = y1 + direction[1][k];
                    if (!isInBound(secX, secY) || board[secX][secY] == not(value)) break;
                    if (board[secX][secY] == 0) return true;

                }
                x1 = x1 + direction[0][k];
                y1 = y1 + direction[1][k];
            }
        }
        return false;
    }

    public void generateNextMovesQueenKiller(int[][] board, int x, int y, ArrayList<int[][]> arrList, int value) {

        for (int k = 0; k < direction[0].length; k++) {
            int x1 = x + direction[0][k], y1 = y + direction[1][k];
            boolean bool = true;
            while (isInBound(x1, y1) && bool) {
                if (board[x1][y1] % 3 == value) break;
                if (board[x1][y1] % 3 == not(value)) {
                    int secX = x1 + direction[0][k], secY = y1 + direction[1][k];
                    if (!isInBound(secX, secY) || board[secX][secY] == not(value)) break;
                    bool = false;
                    while (isInBound(secX, secY) && board[secX][secY] == 0) {

                        int mteri = board[x1][y1];
                        board[x][y] = 0;
                        board[x1][y1] = 0;
                        board[secX][secY] = value + 3;
                        if (isKillerOneQueen(board, secX, secY, value)) {
                            generateNextMovesQueenKiller(board, secX, secY, arrList, value);

                        } else {
                            int[][] copio = copy(board);
                            arrList.add(copio);
                        }
                        board[x][y] = value + 3;
                        board[x1][y1] = mteri;
                        board[secX][secY] = 0;
                        secX = secX + direction[0][k];
                        secY = secY + direction[1][k];
                    }


                }
                x1 = x1 + direction[0][k];
                y1 = y1 + direction[1][k];
            }
        }
    }

    public void generateNextMovesQueenNotKiller(int[][] board, int x, int y, ArrayList<int[][]> arrList, int value) {

        for (int k = 0; k < direction[0].length; k++) {
            int x1 = x + direction[0][k], y1 = y + direction[1][k];
            if (!isInBound(x1, y1)) continue;
            while (isInBound(x1, y1) && board[x1][y1] == 0) {

                board[x][y] = 0;
                board[x1][y1] = value + 3;
                int[][] copio = copy(board);
                arrList.add(copio);
                board[x][y] = value + 3;
                board[x1][y1] = 0;
                x1 = x1 + direction[0][k];
                y1 = y1 + direction[1][k];

            }
        }
    }


    public ArrayList<Checker> findAllCell(int[][] board, int value) {
        HashSet<String> set = new HashSet<>();
        ArrayList<Checker> moves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == value) {
                    if (value == 1) {
                        for (int k = 0; k < direction[0].length; k++) {
                            int x = i + direction[0][k], y = j + direction[1][k];
                            if (!isInBound(x, y)) continue;
                            if (board[x][y] == 0) {
                                if (direction[0][k] != -1) continue;
                                String str = i + " " + j + " " + 0;
                                if (set.contains(str)) continue;
                                set.add(str);
                                moves.add(new Checker(i, j, 0));

                            }
                            if (board[x][y] % 3 == not(value)) {
                                int secX = x + direction[0][k], secY = y + direction[1][k];
                                if (!isInBound(secX, secY)) continue;
                                if (board[secX][secY] == 0) {
                                    String str = i + " " + j + " " + 1;
                                    if (set.contains(str)) continue;
                                    set.add(str);
                                    moves.add(new Checker(i, j, 1));

                                    //counter ++;
                                }
                            }
                        }

                    } else if (value == 2) {

                        for (int k = 0; k < direction[0].length; k++) {
                            int x = i + direction[0][k], y = j + direction[1][k];
                            if (!isInBound(x, y)) continue;
                            if (board[x][y] == 0) {
                                if (direction[0][k] != 1) continue;
                                String str = i + " " + j + " " + 0;
                                if (set.contains(str)) continue;
                                set.add(str);
                                moves.add(new Checker(i, j, 0));
                            }
                            if (board[x][y] % 3 == not(value)) {
                                int secX = x + direction[0][k], secY = y + direction[1][k];
                                if (!isInBound(secX, secY)) continue;
                                if (board[secX][secY] == 0) {
                                    String str = i + " " + j + " " + 1;
                                    if (set.contains(str)) continue;
                                    set.add(str);
                                    moves.add(new Checker(i, j, 1));
                                    //counter ++;
                                }
                            }
                        }
                    }
                } else if (board[i][j] == value + 3) {
                    boolean bool = true;
                    for (int k = 0; k < direction[0].length; k++) {

                        int x = i + direction[0][k], y = j + direction[1][k];
                        while (isInBound(x, y)) {
                            if (board[x][y] == 0 && bool) {
                                String str = i + " " + j + " " + 0;

                                if (set.contains(str)) continue;
                                set.add(str);
                                moves.add(new Checker(i, j, 0));
                                bool = false;
                            }
                            if (board[x][y] % 3 == value) break;
                            if (board[x][y] % 3 == not(value)) {
                                int secX = x + direction[0][k], secY = y + direction[1][k];
                                if (!isInBound(secX, secY)) break;
                                if (board[secX][secY] == 0) {

                                    String str = i + " " + j + " " + 1;
                                    if (set.contains(str)) break;
                                    set.add(str);
                                    moves.add(new Checker(i, j, 1));
                                    //counter ++;
                                    break;
                                }
                                break;
                            }
                            x = x + direction[0][k];
                            y = y + direction[1][k];
                        }
                    }
                }

            }
        }
        return moves;
    }


    private boolean isInBound(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return false;
        return true;
    }

    public void print(int[][] d) {
//		for (int i = 0; i < d.length; i++) {
//			for (int j = 0; j < d[0].length; j++) {
//				System.out.print(d[i][j] + " ");
//
//			}
//			System.out.println();
//		}
//		System.out.println();
    }

    public int[][] copy(int[][] d) {
        int[][] arr = new int[d.length][d[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                if (d[i][j] == 1 && i == 0) arr[i][j] = d[i][j] + 3;
                else if (d[i][j] == 2 && i == 7) arr[i][j] = d[i][j] + 3;
                else arr[i][j] = d[i][j];
            }
        }
        return arr;
    }

    public int e(int[][] board, int player) {
        int w = 0, b = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 1) w++;
                if (board[i][j] == 4) w += 4;
                if (board[i][j] == 2) b++;
                if (board[i][j] == 5) b += 4;
            }
        }


        if (w == 0) return -1000;
        if (b == 0) return 1000;


        return w - b;
    }

//	public static void main(String[] args) {
//		int[][] arr = {
//				{0, 0, 0, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0, 0, 0},
//				{0, 2, 0, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0, 1, 0},
//				{0, 2, 0, 0, 0, 0, 0, 0},
//				{0, 0, 1, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0, 0, 0},
//				{0, 0, 0, 0, 0, 0, 0, 0}
//				
//		};
//		
//		
//		
//		RealPlayer player = new RealPlayer();
//		Turn tr = player.makeTurne(arr, 1);
//		System.out.println(tr.first.x + " " + tr.first.y + " " + tr.first.num);
//		System.out.println(tr.last.x + " " + tr.last.y + " " + tr.last.num);
//		for (int i = 0; i < tr.killed.size(); i++) {
//			System.out.println(tr.killed.get(i).x + " " + tr.killed.get(i).y + " " + tr.killed.get(i).num);
//			
//		}
//		
//		
////		ArrayList<int[][]> arrList = new ArrayList<int[][]>();
////		arrList = player.neighbour(arr, 1);
////		System.out.println(arrList.size());
////		for (int i = 0; i < arrList.size(); i++) {
////			player.print(arrList.get(i));
////		}
//		//ArrayList<int[][]> array = player.neighbour(arr, 1);
//		//for (int i = 0; i < array.size(); i++) {
//		//	player.print(array.get(i));
//		//}
//		
//		
//		
//		
////		for (int i = 0; i < list.size(); i++) {
////			System.out.println(list.get(i).x + " " + list.get(i).y + " " + list.get(i).num);
////		}
//		
//		
//	}

}
