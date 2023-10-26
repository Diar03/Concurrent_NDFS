package ndfs.mcndfs_alg3_naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Callable;

import graph.Graph;
import graph.GraphFactory;
import graph.State;



public class Worker implements Callable<Void> {

private int threadNr;

public void setThreadNr(int threadNr) {
    this.threadNr = threadNr;
}
    
@Override
public Void call() throws Exception {
    try {
        mc_ndfs(graph.getInitialState());
    } catch (CycleFoundException e) {
        Shared.setResult(true);
    } catch (InterruptedException e) {
        // Ignore 
    }
        System.out.println("cyan " + cntCyan + " blue " + cntBlue + " red " + cntRed + " pink " + cntPink);

    return null;
}
    private final Graph graph;
    private final Colors colors = new Colors();
    private int cntRed  = 0;
    private int cntBlue = 0;
    private int cntCyan = 0;
    private int cntPink = 0;
    private boolean allred = true;

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
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(File promelaFile) throws FileNotFoundException {

        this.graph = GraphFactory.createGraph(promelaFile);
    }

    private void dfsRed(State s) throws CycleFoundException, InterruptedException {
        colors.color(s, Color.PINK);
        cntPink++;
        
        for(State t : mcPost(s)){

            if (Thread.interrupted())               
                    throw new InterruptedException();

            if(colors.hasColor(t, Color.CYAN)){
                throw new CycleFoundException();
            }

            if(!colors.hasColor(t, Color.PINK) && !Shared.isRed(t)){
                dfsRed(t);
            }

        }
        if(s.isAccepting()){
            Shared.decrement(s);
            while(Shared.getCount(s) == 0){            // Wait
                if (Thread.interrupted())              
                    throw new InterruptedException();
            }
        }
        colors.color(s, Color.RED);
        cntRed++;    

    }

    private void dfsBlue(State s) throws CycleFoundException, InterruptedException {
        allred = true;
        colors.color(s, Color.CYAN);
        cntCyan++;
        for (State t : mcPost(s)) {

            if (Thread.interrupted())               
                    throw new InterruptedException();

            if (colors.hasColor(t, Color.CYAN) && (s.isAccepting() || t.isAccepting())) {
                throw new CycleFoundException();
            }

            if(colors.hasColor(t, Color.WHITE) && (!Shared.isRed(t))){
                dfsBlue(t);
            }

            if(!Shared.isRed(t)){
                allred = false;
            }
            
        }

        if (Thread.interrupted())             
                    throw new InterruptedException();

        if(allred){
            Shared.setRed(s, true);
        }
        else if(s.isAccepting()){
            Shared.increment(s);
            dfsRed(s);
        }

        colors.color(s, Color.BLUE);
        cntBlue++;
    }

    private void mc_ndfs(State s) throws CycleFoundException, InterruptedException {
        dfsBlue(s);
        // The result is set to no cycle initially, 
        // so we can just return when we have traversed through the whole state space
    }

    private List<State> mcPost(State s){
        List<State> states = graph.post(s);
        for(int i = 0; i < states.size() && i < threadNr; i++){
            State state = states.remove(0);
            states.add(state);
        }
        return states;
    }
    
}
