package mm;

import com.simplejcode.commons.algo.Statistics;
import com.simplejcode.commons.gui.Console;
import util.*;

import java.awt.*;
import java.util.Arrays;

public class MMStatistics {

    private static void summarizeContest() {
        Contestant[] contestants = TCProxy.parseRankings(16704, false, false).toArray(new Contestant[0]);

//        int a = 27, b = 28;
//        Contestant tt = contestants[a -1];
//        contestants[a -1] = contestants[b - 1];
//        contestants[b - 1] = tt;
//        contestants[a - 1].setRank(a);
//        contestants[b - 1].setRank(b);

//        Contestant[] tmp = new Contestant[contestants.length + 1];
//        System.arraycopy(contestants, 0, tmp, 0, 2);
//        System.arraycopy(contestants, 2, tmp, 3, contestants.length-2);
//        tmp[2] = new Contestant(Member.IKA, 2);
//        contestants = tmp;

        Console console = Console.createInstance();
        console.setFont(Font.MONOSPACED, Font.BOLD, 14);
        console.writeLine(String.format("%-22s %-11s %-11s %-11s %-11s %-15s %-11s", "Handle", "Rank", "Rating", "Expected", "New rating", "Rating Change", "New vol"));
        console.writeLine(String.format("%-22s %-11s %-11s %-11s %-11s %-15s %-11s", "______", "____", "______", "________", "__________", "_____________", "_______"));
        console.writeLine("");

        double[] expRanks = new double[contestants.length];
        Contestant[] cs = process(contestants, expRanks);

        for (int i = 0; i < contestants.length; i++) {
            Contestant t = contestants[i];

            double rating = t.getRating();
            Color c;
            final int x = 200;
            if (t.getNum() == 0) {
                c = Color.BLACK;
            } else if (rating < 900) {
                c = Color.GRAY;
            } else if (rating < 1200) {
                c = new Color(0, x, 0);
            } else if (rating < 1500) {
                c = new Color(0, 0, x);
            } else if (rating < 2200) {
                c = new Color(x, x, 0);
            } else {
                c = new Color(x, 0, 0);
            }
            console.setColor(c);
            String s = String.format("%-22s %-11d %-11d %-11.3f %-11d %-+15d %-11d",
                    t.getName(),
                    t.getRank(),
                    t.getRating(),
                    expRanks[i],
                    cs[i].getRating(),
                    cs[i].getRating() - t.getRating(), cs[i].getVolatility());
            console.writeLine(s);
        }
    }

    private static Contestant[] process(Contestant[] contestants, double[] expRanks) {

        int n = contestants.length;
        Contestant[] copy = Arrays.copyOf(contestants, n);
        Contestant[] ret = new Contestant[n];

        // extract rated competitors
        Contestant[] old = new Contestant[n];
        int[] f = new int[copy.length];
        int size = 0;
        for (int i = 0; i < n; i++) {
            if (copy[i].getNum() != 0) {
                f[size] = i;
                old[size++] = copy[i];
            }
        }
        // calculate competition factory
        double compfactory = getCompetitionFactory(old, size);

        //-------------------------------------------------------------------------------

        // calculate actual ranks
        double[] ranks = getActualRanks(old, size);
        // calculate expected ranks and new ratings
        for (int i = 0; i < size; i++) {
            expRanks[f[i]] = getExpectedRank(old[i], old, size);
            ret[f[i]] = adjust(old[i], size, compfactory, expRanks[f[i]], ranks[i]);
            copy[f[i]] = ret[f[i]];
        }

        //-------------------------------------------------------------------------------

        // assign rating and volatility to the new coders
        for (Contestant contestant : copy) {
            if (contestant.getNum() == 0) {
                contestant.setRating(1200);
                contestant.setVolatility(515);
            }
        }
        // calculate actual ranks
        ranks = getActualRanks(copy, n);
        // calculate expected ranks and new ratings
        for (int i = 0; i < n; i++) {
            if (copy[i].getNum() == 0) {
                expRanks[i] = getExpectedRank(copy[i], copy, n);
                ret[i] = adjust(copy[i], n, compfactory, expRanks[i], ranks[i]);
            }
        }

        return ret;
    }

    private static double getCompetitionFactory(Contestant[] contestants, int size) {
        double aveRating = 0;
        for (int i = 0; i < size; i++) {
            aveRating += contestants[i].getRating();
        }
        aveRating /= size;
        double sum1 = 0, sum2 = 0;
        for (int i = 0; i < size; i++) {
            sum1 += Math.pow(contestants[i].getVolatility(), 2);
            sum2 += Math.pow(contestants[i].getRating() - aveRating, 2);
        }
        return Math.sqrt(sum1 / size + sum2 / (size - 1));
    }

    private static double[] getActualRanks(Contestant[] contestants, int size) {
        double[] ranks = new double[size];
        for (int i = 0, j = 0; i < size; i = j) {
            while (j < size && contestants[i].getRank() == contestants[j].getRank()) {
                j++;
            }
            Arrays.fill(ranks, i, j, (i + j) / 2d + 0.5);
        }
        return ranks;
    }

    private static double getExpectedRank(Contestant current, Contestant[] contestants, int size) {
        double expected = 0.5;
        for (int j = 0; j < size; j++) {
            double sqrt2 = Math.sqrt(2);
            double p = contestants[j].getRating() - current.getRating();
            double q = sqrt2 * Math.hypot(current.getVolatility(), contestants[j].getVolatility());
            expected += 0.5 * (Statistics.StatUtil.erf(p / q) + 1);
        }
        return expected;
    }

    private static Contestant adjust(Contestant old, int size, double compfactory, double exp, double rank) {
        Contestant ret = old.copy();
        double ePerf = -Statistics.StatUtil.invF((exp - 0.5) / size);
        double aPerf = -Statistics.StatUtil.invF((rank - 0.5) / size);

        double perfAs = old.getRating() + compfactory * (aPerf - ePerf);

        double w = 1 / (1 - (0.42 / (old.getNum() + 1) + 0.18)) - 1;

        double newRating = (old.getRating() + w * perfAs) / (w + 1);

        double cap = 150 + 1500d / (old.getNum() + 2);
        double dr = newRating - old.getRating();
        if (Math.abs(dr) > cap) {
            dr = Math.signum(dr) * cap;
            newRating = old.getRating() + dr;
        }
        double vol = old.getVolatility();
        ret.setRating((int) Math.round(newRating));
        ret.setVolatility((int) Math.round(Math.sqrt(dr * dr / w + vol * vol / (w + 1))));

        return ret;
    }

    public static void main(String[] args) {
        summarizeContest();
    }

}
