package oldprog;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

    public class Hanoy extends Panel implements ActionListener  {

        Hanoy() {
        }

        static int N_DISK = 4, moveNumber = 0,
                   w[][], h[] = new int[3];

        public Dimension getPreferredSize() {
            return new Dimension(900, 600);
        }

        public void paint(Graphics g){
            for(int i = 0; i < 3; i++)
            for(int j = 0; j < N_DISK; j++)
            g.drawRect(150 + 300 * i - 10 * w[i][j], 500 - (j - 1) * 12, w[i][j] * 20, 10);
        }

        public static void main(String[] args){
            int i;
            w = new int[3][];
            for(i = 0; i < 3; i++)
                w[i] = new int[N_DISK];

            h[0] = N_DISK - 1;
            h[1] = h[2] = -1;

            for(i = 0; i < N_DISK; i++)
                w[0][i] = N_DISK - i;

            Frame f = new Frame("Hanoy");

            f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent ev){
                    System.exit(0);
                }

            });

            Hanoy hanoy = new Hanoy();
            f.add(hanoy);
            Button button = new Button("step");
            button.addActionListener(hanoy);
            f.add(button, BorderLayout.NORTH);
            f.pack();
            f.setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            int i, diskSize;
            moveNumber++;

            i = (moveNumber & (moveNumber - 1)) % 3;
            diskSize = w[i][h[i]];
            w[i][h[i]] = 0;
                h[i]--;

            i = ((moveNumber | (moveNumber - 1)) + 1) % 3;
            h[i]++;
            w[i][h[i]] = diskSize;

            repaint();
        }
    }

