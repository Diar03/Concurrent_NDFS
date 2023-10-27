package ndfs.mcndfs_alg3_optimized;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import graph.State;

public class Shared {

    private static Shared instance = new Shared();
    private static boolean result = false;
    public static volatile ConcurrentHashMap<State, Boolean> hashRed;
    public static volatile ConcurrentHashMap<State, AtomicInteger> countMap;

    public static void initConcurrentMaps(int nrWorkers) {
        // Use the constructor of ConcurrentHashMap to specify the concurrency level
        hashRed = new ConcurrentHashMap<State, Boolean>(128000, 0.9f, nrWorkers);
        countMap = new ConcurrentHashMap<State, AtomicInteger>(128000, 0.9f, nrWorkers);
    }

    
    private Shared() {
        // private constructor to prevent instantiation
    }

    public static void setResult(boolean result) {
        Shared.result = result;
    }
    public static boolean getResult() {
        return Shared.result;
    }


}