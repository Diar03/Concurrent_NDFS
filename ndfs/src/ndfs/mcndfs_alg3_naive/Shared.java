package ndfs.mcndfs_alg3_naive;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import graph.State;

public class Shared {

    private static volatile HashMap<State, Integer> countMap = new HashMap<State, Integer>();
    private static volatile HashMap<State, Boolean> redColor = new HashMap<State, Boolean>();

    private static final ReentrantReadWriteLock countLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock redLock = new ReentrantReadWriteLock();

    private static boolean result = false;

    
    private Shared() {
        // private constructor to prevent instantiation
    }


    public static boolean getResult() {
        return result;
    }

    public static void setResult(boolean value) {
        result = value;
    }

    public static int getCounter(State s) {
        countLock.readLock().lock();
        try {
            return countMap.get(s);
        } finally {
            countLock.readLock().unlock();
        }
    }

    public static void incrementCounter(State s) {
        countLock.writeLock().lock();
        try {
            countMap.putIfAbsent(s, 0);
            countMap.put(s, countMap.get(s)+1);
        } finally {
            countLock.writeLock().unlock();
        }
    }

    public static void decrementCounter(State s){
        countLock.writeLock().lock();
        try {
            int val = getCounter(s);
            countMap.put(s, val-1);
        } finally {
            countLock.writeLock().unlock();
        }
    }

    public static void setRed(State s, boolean value) {
        redLock.writeLock().lock();
        try {
            redColor.put(s, value);
        } finally {
            redLock.writeLock().unlock();
        }
    }

    public static boolean isRed(graph.State t) {
        redLock.readLock().lock();
        try {
            return redColor.containsKey(t);
        } finally {
            redLock.readLock().unlock();
        }
    }


}
