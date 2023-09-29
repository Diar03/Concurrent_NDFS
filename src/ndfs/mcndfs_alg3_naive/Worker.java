package ndfs.mcndfs_alg3_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker {

    private final Graph graph;
    private final Colors colors = new Colors();
    private boolean result = false;
    private int cntRed  = 0;
    private int cntBlue = 0;
    private int cntCyan = 0;

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

    private void dfsRed(State s) throws CycleFoundException {

        for (State t : graph.post(s)) {
            if (colors.hasColor(t, Color.CYAN)) {
                throw new CycleFoundException();
            } else if (colors.hasColor(t, Color.BLUE)) {
                colors.color(t, Color.RED);
                cntRed++;
                dfsRed(t);
            }
        }
    }

    private void dfsBlue(State s) throws CycleFoundException {

        colors.color(s, Color.CYAN);
        cntCyan++;
        for (State t : graph.post(s)) {
            if (colors.hasColor(t, Color.WHITE)) {
                dfsBlue(t);
            }
        }
        if (s.isAccepting()) {
            dfsRed(s);
            colors.color(s, Color.RED);
            cntRed++;
        } else {
            colors.color(s, Color.BLUE);
            cntBlue++;
        }
    }

    private void nndfs(State s) throws CycleFoundException {
        dfsBlue(s);
    }

    public void run() {
        try {
            nndfs(graph.getInitialState());
        } catch (CycleFoundException e) {
            result = true;
        }
        System.out.println("cyan " + cntCyan + " blue " + cntBlue + " red " + cntRed);
    }

    public boolean getResult() {
        return result;
    }
}
