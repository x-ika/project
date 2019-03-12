package ika.games.base.controller;

import com.simplejcode.commons.misc.ArrayUtils;
import com.simplejcode.commons.misc.structures.MersenneTwister;

import java.util.*;

public class GameRandom extends Random {

    protected final Random rnd;
    protected final MersenneTwister twister;

    protected String secret;
    protected int serverSeed;

    public GameRandom() {
        this(new Random(), new MersenneTwister());
    }

    public GameRandom(Random rnd) {
        this(rnd, new MersenneTwister());
    }

    public GameRandom(Random rnd, MersenneTwister twister) {
        this.rnd = rnd;
        this.twister = twister;
    }

    //-----------------------------------------------------------------------------------
    // BASIC

    public void reset() {
        secret = Long.toHexString(rnd.nextLong()) + Long.toHexString(rnd.nextLong());
        serverSeed = rnd.nextInt();
    }

    public int getServerSeed() {
        return serverSeed;
    }

    public String getSecret() {
        return secret;
    }


    public void setClientSeed(int clientSeed) {
        twister.setSeed(new int[] {serverSeed, clientSeed});
    }

    public void setClientSeeds(int[] clientSeeds) {
        int[] seedArray = new int[clientSeeds.length + 1];
        System.arraycopy(clientSeeds, 0, seedArray, 1, clientSeeds.length);
        seedArray[0] = serverSeed;
        twister.setSeed(seedArray);
    }


    public boolean nextBoolean() {
        return twister.nextBoolean();
    }

    public short nextShort() {
        return twister.nextShort();
    }

    public int nextInt() {
        return twister.nextInt();
    }

    public long nextLong() {
        return twister.nextLong();
    }

    public boolean nextBoolean(double v) {
        return twister.nextBoolean(v);
    }

    public boolean nextBoolean(float v) {
        return twister.nextBoolean(v);
    }

    public int nextInt(int n) {
        return (nextInt() % n + n) % n;
    }

    public long nextLong(long n) {
        return twister.nextLong(n);
    }

    //-----------------------------------------------------------------------------------
    // GAMES

    public int[] createComplect(int n) {
        int[] complect = new int[n];
        for (int i = 0; i < complect.length; i++) {
            complect[i] = i + 1;
        }
        ArrayUtils.shuffle(complect, n, twister);
        return complect;
    }

    public int[] shuffleArray(int[] array) {
        ArrayUtils.shuffle(array, array.length, twister);
        return array;
    }

    public <T> T[] shuffleArray(T[] array) {
        ArrayUtils.shuffle(array, array.length, twister);
        return array;
    }

    public int pickRandom(int[] array) {
        return array[nextInt(array.length)];
    }

    public <T> T pickRandom(T[] array) {
        return array[nextInt(array.length)];
    }

    public <T> T pickRandom(List<T> list) {
        return list.get(nextInt(list.size()));
    }

    public void cutArray(int[] array) {
        int n = array.length;
        int cutoff = nextInt(n);
        int[] copy = Arrays.copyOfRange(array, 0, cutoff);
        System.arraycopy(array, cutoff, array, 0, n - cutoff);
        System.arraycopy(copy, 0, array, n - cutoff, cutoff);
    }

}
