package ndfs.mcndfs_alg3_naive;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import graph.State;

public class Shared {
    
    // Prevent new instances from being created.
    private Shared(){}

    private static boolean result = false;
    private static HashMap<State, Boolean> hashRed = new HashMap<State, Boolean>();
    private static HashMap<State, Integer> countMap = new HashMap<State, Integer>();
    private static Shared instance = new Shared();
    private static ReentrantLock countLock, redLock = new ReentrantLock();

    public static Integer getCount(State state) {
        try{
            
            countLock.lock();
            int count = countMap.get(state);
            return count;
        } finally {
            countLock.unlock();
        }
    }

    public static void increment(State state) {

        try{
            countLock.lock();
            if(countMap.get(state) == null) {
                countMap.put(state, 1);
            } else {
                countMap.put(state, countMap.get(state) + 1);
            }
            
        } finally{
            countLock.unlock();
        }
    }

    public static void decrement(State state) {
        try{
            countLock.lock();
            if(countMap.get(state) == null) { // Not possible in correct implementation
                System.out.println("Error: decrementing state that has not been incremented"); 
            } else {
                countMap.put(state, countMap.get(state) - 1);
            }
        } finally {
            countLock.unlock();
        }
    }

    public static boolean isRed(State state) {
        try{
            redLock.lock();
            if(hashRed.get(state) == null) {
                setRed(state, false);
                return false;
            } else {
                boolean res = Shared.hashRed.get(state);
                return res;
             }
        } finally {
            redLock.unlock();
        }
        
    }

    public static void setRed(State state, boolean value) {
        try{
            redLock.lock();
            Shared.hashRed.put(state, value);
        } finally {
            redLock.unlock();
        }
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
