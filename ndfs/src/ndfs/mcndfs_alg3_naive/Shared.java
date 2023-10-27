package ndfs.mcndfs_alg3_naive;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import graph.State;

public class Shared {

    private static Shared instance;
    private static volatile HashMap<State, Integer> countMap = new HashMap<State, Integer>();
    private static volatile HashMap<State, Boolean> redColor = new HashMap<State, Boolean>();

    private static final ReentrantLock countLock = new ReentrantLock();
    private static final ReentrantLock redLock = new ReentrantLock();

    private static boolean result = false;

    
    private Shared() {
        // private constructor to prevent instantiation
    }
    
    public static Shared getInstance() {
        if (instance == null) {
            instance = new Shared();
        }
        return instance;
    }

    public static boolean getResult() {
        return result;
    }

    public static void setResult(boolean value) {
        result = value;
    }

    public static int getCounter(State s) {
        countLock.lock();
        try {
            if (countMap.containsKey(s)) {
                return countMap.get(s);
            } else {
                System.out.println("Counter not initialized");
                throw new Error("Counter not initialized");
            }
        } finally {
            countLock.unlock();
        }
    }

    public static void incrementCounter(State s) {
        countLock.lock();
        try {
            if (countMap.containsKey(s)) {
                countMap.put(s, countMap.get(s) + 1);
            } else {
                countMap.put(s, 1);
            }
        } finally {
            countLock.unlock();
        }
    }

    public static void decrementCounter(State s){
        countLock.lock();
        try {
            if (countMap.containsKey(s)) {
                countMap.put(s, countMap.get(s) - 1);
            } else {
                countMap.put(s, 0);                  // This if condition should not be reached
            }
        } finally {
            countLock.unlock();
        }
    }

    public static void setRed(State s, boolean value) {
        redLock.lock();
        try {
            redColor.put(s, value);
        } finally {
            redLock.unlock();
        }
    }

    public static boolean isRed(graph.State t) {
        redLock.lock();
        try {
            return redColor.containsKey(t);
        } finally {
            redLock.unlock();
        }
    }


}
