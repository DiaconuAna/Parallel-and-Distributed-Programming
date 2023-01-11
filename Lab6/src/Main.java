import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int NR_THREADS = 5;
    private static final int NR_VERTICES = 50;
    private static final MyGraph testGraph = MyGraph.generateGraph(NR_VERTICES, true);
//    private static final MyGraph INPUT_GRAPH = MyGraph.generateGraph(NR_VERTICES, false);

    public static void main(String[] args) throws InterruptedException {
        // Given a directed graph, find a Hamiltonean cycle, if one exists. Use multiple threads to parallelize the search.
        System.out.println(testGraph);
        long startTime = System.nanoTime();
        ExecutorService threadPool = Executors.newFixedThreadPool(NR_THREADS);

        for (int currentNode = 0; currentNode < testGraph.size(); currentNode++) {
            threadPool.submit(new CycleManager(testGraph, currentNode, new ArrayList<>(testGraph.size()), new AtomicBoolean(false)));
        }

        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.SECONDS);
        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println(duration + " ms");
    }
}
