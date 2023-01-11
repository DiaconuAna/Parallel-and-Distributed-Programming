import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CycleManager implements Runnable{
    private final MyGraph graph;
    private final int startingNode;
    private final List<Integer> path;
    private final Lock lock;
    private final List<Integer> result;
    private final AtomicBoolean foundCycle;

    CycleManager(MyGraph graph, int node, List<Integer> result, AtomicBoolean foundCycle) {
        this.graph = graph;
        this.startingNode = node;
        this.path = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.foundCycle = foundCycle;
        this.result = result;
    }

    @Override
    public void run() {
        visitNode(startingNode);
    }

    private void visitNode(int node){
        // add starting node to the path
        path.add(node);
        if(!foundCycle.get()){ // if no cycle was found
            if(path.size() == graph.size()){
                // we reached the maximum amounts of nodes in a graph so we check if a cycle exists
                if(graph.getNeighbours(node).contains(startingNode)){
                    // we found a hamiltonian cycle
                    foundCycle.set(true);
                    this.lock.lock();
                    result.clear();
                    result.addAll(this.path);
                    if(!result.isEmpty()){
                        System.out.println(result);
                    }
                    this.lock.unlock();
                }
                    return;
            }
        graph.getNeighbours(node).forEach(neighbour ->{
            if(!this.path.contains(neighbour)){
                visitNode(neighbour);
            }
        });
        }
    }
}
