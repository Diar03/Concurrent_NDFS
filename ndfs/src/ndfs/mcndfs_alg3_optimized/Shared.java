package ndfs.mcndfs_alg3_optimized;

import java.util.concurrent.ConcurrentHashMap;

import graph.State;

public class Shared {
    
    // Prevent new instances from being created.
    private Shared(){}

    private static volatile boolean result = false;
    private static volatile ConcurrentHashMap<State, Boolean> hashRed;
    private static volatile ConcurrentHashMap<State, Integer> countMap;
    private static Shared instance = new Shared();

    public static void initConcurrentMaps(int nrWorkers) {
        // Use the constructor of ConcurrentHashMap to specify the concurrency level
        hashRed = new ConcurrentHashMap<State, Boolean>(32, 0.75f, nrWorkers);
        countMap = new ConcurrentHashMap<State, Integer>(32, 0.75f, nrWorkers);
    }

    public static Integer getCount(State state) {
        int count = countMap.get(state);
        return count;
    }

    public static void increment(State state) {
        countMap.putIfAbsent(state, 0);
        countMap.put(state, countMap.get(state) + 1);
    }

    public static void decrement(State state) {
        int temp = countMap.get(state);
        countMap.replace(state, temp - 1);
    }

    public static boolean isRed(State state) {
        if(hashRed.get(state) == null) {
            setRed(state, false);
            return false;
        } else {
            boolean res = Shared.hashRed.get(state);
            return res;
         }
        
    }

    public static void setRed(State state, boolean value) {
        Shared.hashRed.put(state, value);
    }

    public static Shared getInstance() {
        return instance;
    }

    public static void setResult(boolean result) {
        Shared.result = result;
    }
    public static boolean getResult() {
        return Shared.result;
    }
}
