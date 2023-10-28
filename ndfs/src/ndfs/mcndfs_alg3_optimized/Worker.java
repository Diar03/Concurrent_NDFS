package ndfs.mcndfs_alg3_optimized;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import graph.Graph;
import graph.GraphFactory;
import graph.State;


public class Worker implements Callable<Void> {

    private final Graph graph;
    private int threadID;
    private final Colors colors = new Colors();

    private int cntRed  = 0;
    private int cntBlue = 0;
    private int cntCyan = 0;
    private int cntPink = 0;

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Constructs a Worker object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * 
     * @param threadID
     *            the thread number.
     * 
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(File promelaFile, int threadID) throws FileNotFoundException {
        this.graph = GraphFactory.createGraph(promelaFile);
        this.threadID = threadID;
    }

    @Override
    public Void call() {
        try {
            mc_ndfs(graph.getInitialState());
        } catch (CycleFoundException e) {
            Shared.setResult(true);
        } catch (InterruptedException e){
            // Do nothing
        }
        System.out.println("cyan " + cntCyan + " blue " + cntBlue + " red " + cntRed + " pink " + cntPink);
        return null;
    }


    private void dfsRed(State s) throws CycleFoundException, InterruptedException {

        colors.color(s, Color.PINK);
        ++cntPink;

        for(State t : mcPost(s)){

            if(Thread.interrupted())
                throw new InterruptedException();
            

            if(colors.hasColor(t, Color.CYAN)){
                throw new CycleFoundException();
            }

            if(!colors.hasColor(t, Color.PINK) && !Shared.hashRed.containsKey(t)){
                dfsRed(t);
            }

            if(Thread.interrupted())
                throw new InterruptedException();


        }

        if(s.isAccepting()){
            Shared.countMap.get(s).getAndDecrement();
            while(Shared.countMap.get(s).get() != 0){
                if(Thread.interrupted())
                    throw new InterruptedException();
            }
        }

        Shared.hashRed.putIfAbsent(s, true);
        ++cntRed;


    }

    private void dfsBlue(State s) throws CycleFoundException, InterruptedException {

        boolean allred = true;

        colors.color(s, Color.CYAN);
        ++cntCyan;

        for(State t : mcPost(s)){

            if(Thread.interrupted())
                throw new InterruptedException();
            
            if(colors.hasColor(t, Color.CYAN) 
                && (t.isAccepting() || s.isAccepting() ) ){
                throw new CycleFoundException();
            }

            if(colors.hasColor(t, Color.WHITE) && !Shared.hashRed.containsKey(t)){
                dfsBlue(t);
            }

            if(!Shared.hashRed.containsKey(t)){
                allred = false;
            }

        }

        if(Thread.interrupted())
                throw new InterruptedException();

        if(allred){
            Shared.hashRed.putIfAbsent(s, true);
        } else if(s.isAccepting()){
            Shared.countMap.putIfAbsent(s, new AtomicInteger(0));
            Shared.countMap.get(s).incrementAndGet();
            dfsRed(s);
        }
        if(Thread.interrupted())
                throw new InterruptedException();

        colors.color(s, Color.BLUE);
        ++cntBlue;

    }

    private List<State> mcPost(State s){
        List<State> states = graph.post(s);
        // Collections.rotate(states, threadID);
        Collections.shuffle(states);
        return states;
    }

    private void mc_ndfs(State s) throws CycleFoundException, InterruptedException {
        dfsBlue(s);
    }

}
