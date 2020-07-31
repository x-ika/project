package com.topcoder.marathon;

import com.simplejcode.commons.misc.DynamicMap;
import mm.MMTester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class MarathonController {
    private Parameters parseArgs(String[] args) {
        Parameters parameters = new Parameters();
        String last = null;
        for (String s : args) {
            if (!s.startsWith("-")) {
                if (last == null) {
                    System.out.println("ERROR: Invalid command line parameters!");
                    System.out.println("It should contain one or more a sequence of ");
                    System.out.println("    -key value     (a single value)");
                    System.out.println("    -key start,end (a range, from start to end, inclusive)");
                    System.out.println("    -flag          (enable a flag)");
                    System.exit(-1);
                }
                parameters.put(last, s);
                last = null;
            } else {
                if (last != null) parameters.put(last, null);
                last = s.substring(1);
                if (last.isEmpty()) last = null;
            }
        }
        if (last != null) parameters.put(last, null);
        return parameters;
    }

    private synchronized Double checkBest(File bestsFile, boolean isMaximize, double errorScore, long seed, double score) {
        if (bestsFile == null) return null;
        Double best = null;
        try {
            Map<Long, Double> bests = new TreeMap<Long, Double>();
            if (bestsFile.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(bestsFile));
                String line = null;
                while ((line = in.readLine()) != null) {
                    String[] s = line.split("=");
                    bests.put(Long.parseLong(s[0]), Double.parseDouble(s[1]));
                }
                in.close();
            }
            best = bests.get(seed);
            if (score != errorScore) {
                if (best == null || (isMaximize && score > best) || (!isMaximize && score < best)) {
                    bests.put(seed, score);
                    File tmpFile = new File(bestsFile.getPath() + ".tmp");
                    BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile));
                    StringBuilder sb = new StringBuilder();
                    for (long a : bests.keySet()) {
                        sb.delete(0, sb.length());
                        sb.append(a).append('=').append(bests.get(a));
                        out.write(sb.toString());
                        out.newLine();
                    }
                    out.close();
                    Files.move(tmpFile.toPath(), bestsFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return best;
    }

    //Called by server tester using a direct call
    public MarathonTestResult run(String className, long seed, String exec, int timeLimit) {
        MarathonTestResult result = new MarathonTestResult();
        MarathonTester tester = null;
        Constructor<?> constructor = null;
        try {
            Class<?> c = Class.forName(className);
            constructor = c.getConstructors()[0];
            tester = (MarathonTester) constructor.newInstance();
        } catch (Exception e) {
            result.setError("ERROR finding class " + className + ": " + e.getMessage());
            return result;
        }
        try {
            Parameters parameters = new Parameters();
            if (exec != null) parameters.put(Parameters.exec, exec);
            parameters.put(Parameters.noVis, null);
            parameters.put(Parameters.noOutput, null);

            result.setScore(tester.getErrorScore());
            result.setMaximize(tester.isMaximize());
            tester.setParameters(parameters);
            tester.setSeed(seed);
            tester.setTimeLimit(timeLimit);

            double score = tester.runTest();

            result.setScore(score);
            result.setRunTime(tester.getRunTime());
            result.setOutput(tester.getSolutionError());
            result.setError(tester.getExecutionsErrors());
        } catch (Exception e) {
            result.setError("ERROR calling tester " + className + ": " + e.getMessage());
        }
        return result;
    }

    //Called by local tester, passing command line parameters
    public final void run(String[] args) {
        //Find the name of the concrete class (actual MarathonTester) to be called using reflection
        String className = new Exception().getStackTrace()[1].getClassName();

        //Parse command line parameters
        Parameters parameters = parseArgs(args);

        //Get seeds range (default is seed=1)
        long startSeed = 1;
        long endSeed = 1;
        if (parameters.isDefined(Parameters.seed)) {
            long[] seedRange = parameters.getLongRange(Parameters.seed);
            startSeed = seedRange[0];
            endSeed = seedRange[1];
            parameters.remove(Parameters.seed);
        }

        //Multiple seeds?
        boolean multipleSeeds = endSeed > startSeed;

        //Check and expand saveAll parameter
        if (parameters.isDefined(Parameters.saveAll)) {
            if (parameters.isDefined(Parameters.saveSolInput)) {
                System.out.println("ERROR: Parameters " + Parameters.saveAll + " and " + Parameters.saveSolInput + " can't be used together.");
                return;
            }
            if (parameters.isDefined(Parameters.saveSolOutput)) {
                System.out.println("ERROR: Parameters " + Parameters.saveAll + " and " + Parameters.saveSolOutput + " can't be used together.");
                return;
            }
            if (parameters.isDefined(Parameters.saveSolError)) {
                System.out.println("ERROR: Parameters " + Parameters.saveAll + " and " + Parameters.saveSolError + " can't be used together.");
                return;
            }
            String folder = parameters.getStringNull(Parameters.saveAll);
            parameters.remove(Parameters.saveAll);
            parameters.put(Parameters.saveSolInput, folder);
            parameters.put(Parameters.saveSolOutput, folder);
            parameters.put(Parameters.saveSolError, folder);
        }

        //Check invalid combinations with loadSolOutput parameter
        if (parameters.isDefined(Parameters.loadSolOutput)) {
            if (parameters.isDefined(Parameters.exec)) {
                System.out.println("ERROR: Parameters " + Parameters.loadSolOutput + " and " + Parameters.exec + " can't be used together.");
                return;
            }
            if (parameters.isDefined(Parameters.saveSolOutput)) {
                System.out.println("ERROR: Parameters " + Parameters.loadSolOutput + " and " + Parameters.saveSolOutput + " can't be used together.");
                return;
            }
            if (parameters.isDefined(Parameters.saveSolError)) {
                System.out.println("ERROR: Parameters " + Parameters.loadSolOutput + " and " + Parameters.saveSolError + " can't be used together.");
                return;
            }
        }

        //Number of threads for simultaneous test execution (default is a single thread) 
        int numThreads = 1;
        if (parameters.isDefined(Parameters.threads)) {
            numThreads = parameters.getIntValue(Parameters.threads);
            numThreads = Math.max(numThreads, 1);
            numThreads = Math.min(numThreads, Runtime.getRuntime().availableProcessors());
            long numSeeds = endSeed - startSeed + 1;
            if (numSeeds < numThreads) numThreads = (int) numSeeds;
            parameters.remove(Parameters.threads);
        }

        //Put requested seeds on a queue
        LinkedList<Long> seeds = new LinkedList<Long>();
        for (long seed = startSeed; seed <= endSeed; seed++) {
            seeds.add(seed);
        }

        //Check if controlling bests is defined (default is turned off)
        File bf = null;
        if (parameters.isDefined(Parameters.controlBests)) {
            bf = new File(parameters.getString(Parameters.controlBests));
            if (bf.getParentFile() != null && !bf.getParentFile().exists()) {
                bf.getParentFile().mkdirs();
            }
        }
        File bestsFile = bf;

        //Instantiate the concrete class (actual MarathonTester) 
        Class<?> c = null;
        Constructor<?> ct = null;
        double es = -1;
        boolean im = true;
        try {
            c = Class.forName(className);
            ct = c.getConstructors()[0];
            //Create an instance to check it is accessible and get its configuration 
            MarathonTester tester = (MarathonTester) ct.newInstance();
            es = tester.getErrorScore();
            im = tester.isMaximize();
        } catch (Exception e) {
            System.out.println("ERROR finding class " + className);
            e.printStackTrace();
            System.exit(-1);
        }
        Constructor<?> constructor = ct;
        double errorScore = es;
        boolean isMaximize = im;

        //Check if showing runtime is enabled  (default is turned off)
        boolean printRuntime = parameters.isDefined(Parameters.printRuntime);

        //Get time limit, in milliseconds (default is no time limit control, timeLimit = 0)
        long tl = 0;
        if (parameters.isDefined(Parameters.timeLimit)) tl = parameters.getLongValue(Parameters.timeLimit);
        long timeLimit = tl;

        //Run tests
        // IM
        int n = 1;
        double[] sum = new double[n + 1];
        double[] cur = new double[n + 1];
        DynamicMap<Long> map = bestsFile == null ? null : new DynamicMap<>(!im, bestsFile.getName());
        // IM
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            (threads[i] = new Thread() {
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        long seed = 0;
                        synchronized (seeds) {
                            if (seeds.isEmpty()) break;
                            seed = seeds.removeFirst();
                        }
                        try {
                            MarathonTester tester = (MarathonTester) constructor.newInstance();
                            tester.setParameters(parameters);
                            tester.setSeed(seed);
                            if (timeLimit != 0) tester.setTimeLimit(timeLimit);

                            double score = tester.runTest();

                            sb.delete(0, sb.length());
                            // IM
                            if (multipleSeeds) sb.append(">>>Seed = ").append(seed).append('\n');
                            sb.append(">>>Score = ").append(score).append('\n');
                            if (printRuntime) sb.append(">>>RunTime = ").append(String.format("%.3f\n", tester.getRunTime() / 1e3));
                            if (bestsFile != null) {
                                cur[n] = map.get(seed);
                                for (int i = 0; i < n; i++) {
                                    cur[i] = score;
                                    if (cur[i] != errorScore) {
                                        map.update(seed, cur[i]);
                                    }
                                }
                                double best = map.get(seed);
                                for (int i = 0; i <= n; i++) {
                                    sum[i] += cur[i] = cur[i] == errorScore ? 0 : calc(cur[i], best, isMaximize);
                                }
                                sb.append(MMTester.getScoreString(cur));
                            }
                            // IM
                            // orig
//                            if (multipleSeeds) sb.append("Seed = ").append(seed).append(", ");
//                            sb.append("Score = ").append(score);
//                            Double best = checkBest(bestsFile, isMaximize, errorScore, seed, score);
//                            if (best != null) sb.append(", PreviousBest = ").append(best);
//                            if (printRuntime) sb.append(", RunTime = ").append(tester.getRunTime()).append(" ms");
                            // orig
                            System.out.println(sb.toString());
                            System.out.println();
                            System.out.flush();
                        } catch (Exception e) {
                            System.out.println("ERROR calling tester " + className);
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    }
                }
            }).start();
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(MMTester.getScoreString(sum));
            if (map != null) {
                map.close();
            }
        }
    }

    public static double calc(double score, double best, boolean isMaximize) {
        return isMaximize ?
                best == 0 ? 1 : score / best :
                score == 0 ? 1 : best / score;
    }

}
