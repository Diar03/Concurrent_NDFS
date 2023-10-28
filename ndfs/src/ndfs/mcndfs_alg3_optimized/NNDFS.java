package ndfs.mcndfs_alg3_optimized;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private ExecutorService pool;

    private CompletionService<Void> ecs;

    private Worker[] workers;

    /**
     * Constructs an NDFS object using the specified Promela file.
     *
     * @param promelaFile
     * @param nrWorkers
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public NNDFS(File promelaFile, int nrWorkers) throws FileNotFoundException {
        pool = Executors.newFixedThreadPool(nrWorkers);
        ecs = new ExecutorCompletionService<Void>(pool);

        this.workers = new Worker[nrWorkers];
        for (int i = 0; i < nrWorkers; i++) {
            workers[i] = new Worker(promelaFile, i);
        }
        Shared.initConcurrentMaps(nrWorkers);   // Initialize the shared concurrent maps
        Worker.endLatch = new CountDownLatch(nrWorkers);
    }

    @Override
    public boolean ndfs() {
        for (int i = 0; i < this.workers.length; i++){
            ecs.submit(workers[i]);
        }

        try {
            ecs.take();                     // wait for the first completed task
        } catch (InterruptedException e) {
                                            // Do nothing
        } finally {
            pool.shutdownNow();             // shutdown the thread pool
        }

        try {
            // Wait for the pool to actually terminate.
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }


        return Shared.getResult();
    }
}
