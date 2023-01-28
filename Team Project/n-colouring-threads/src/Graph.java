/**
 * Graph coloring is a problem in graph theory where the goal is to assign colors to the vertices of a graph in such a
 * way that no two adjacent vertices have the same color. The number of colors used is known as the chromatic number of
 * the graph. In the case of an "n-graph coloring" problem, the chromatic number is limited to "n" colors. The graph
 * coloring problem is NP-complete, meaning that there is no known efficient algorithm for solving it for all possible
 * inputs, but there are approximate and heuristic algorithms that can be used to solve specific instances of the problem.
 */

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Graph {

    private int edgeCount;
    private int nodeCount;
    private final Set<Integer> independentSet;
    private final Map<Integer, NodeInfo> nodesInfo;
    private Set<Integer> colors;
    private Map<Integer, Set<Integer>> graph;
    private ExecutorService exec;

    public Graph(int verticesCount) {

        this.nodeCount = verticesCount;
        graph = new HashMap<>();
        nodesInfo = new HashMap<>();

        for (int i = 0; i < verticesCount; i++) {
            graph.put(i, new HashSet<>());
            nodesInfo.put(i, new NodeInfo(-1));
        }

        colors = new TreeSet<>();
        independentSet = new HashSet<>(graph.keySet());

        for (int i = 0; i < 100; i++) {
            colors.add(i);
        }

        exec = Executors.newFixedThreadPool(4);

    }

    public static Graph generateGraph(int size, int edgeCount){
        Graph graph = new Graph(size);
        Random random = new Random();

        while(edgeCount > 0){
            int sourceVertex = random.nextInt(size);
            int destinationVertex = random.nextInt(size);

            while(destinationVertex == sourceVertex){
                destinationVertex = random.nextInt(size);
            }

            graph.addEdge(sourceVertex, destinationVertex);
            edgeCount--;
        }

        return graph;
    }

    @Override
    public String toString(){
        for(int i=0;i<this.nodeCount;i++){
            System.out.println("Node : "+ i);
            System.out.println(getAllNeighbours(i));
        }
        return "";
    }

    public void addEdge(int src, int dest) {
        edgeCount++;
        graph.get(src).add(dest);
        graph.get(dest).add(src);
    }

    public Integer getColor(int v) {
        return nodesInfo.get(v).color;
    }

    /**
     * Color a node using the smallest available color
     * @param v
     */
    public void setColor(int v) {
        nodesInfo.get(v).color = getSmallestColor(v);
    }

    /**
     * Get the smallest color not contained by the neighbouring nodes. - the lowest available color
     * @param node
     * @return
     */
    public Integer getSmallestColor(int node)
    {
        Set<Integer> neighboursColors = getNeighboursColors(node);

        for (Integer c : colors) {
            if (!neighboursColors.contains(c)) {
                return c;
            }
        }
        return 0;
    }

    /**
     * Get all neighbours of the node
     * @param node
     * @return
     */
    public Set<Integer> getAllNeighbours(int node) {
        return graph.get(node);
    }

    /**
     * Get the colours corresponding to the colored neighbours of the node
     * @param v
     * @return
     */
    public Set<Integer> getNeighboursColors(int v) {
        return getAllNeighbours(v).stream().filter(
                (node) -> {
                    return getColor(node) != -1;
                }
        ).map(this::getColor).collect(Collectors.toSet());
    }

    /**
     * Get the uncoloured neighbours of the node
     * @param v
     * @return
     */
    private List<Integer> getNodeNeighbours(int v) {
        return graph.get(v).stream().filter(
                (node) -> {
                    return getColor(node) == -1;
                }
        ).collect(Collectors.toList());
    }

    /**
     * Color the vertices based on the independent sets they are in.
     */
    public void colorGraph() {
        while (!independentSet.isEmpty()) {
            Set<Integer> set = getIndependentSet();

            for (Integer node : set) {
                exec.submit(
                        () -> setColor(node)
                );
            }
            independentSet.removeAll(set); // delete the nodes that have already been coloured
        }

        exec.shutdown();
    }

    public void printColors() {
        for (Integer v : nodesInfo.keySet())
            System.out.printf("Node %d has color: %d\n", v, getColor(v));
    }

    /**
     * Check if a node has the smallest value among its neighbours
     * @param v
     * @return
     */
    private boolean checkNode(int v) {
        for (Integer neighbour : getNodeNeighbours(v)) {
            if (v > neighbour) {
                return false;
            }
        }
        return true;
    }

    /**
     * Based on the simple observation that any independent set of vertices can be colored in parallel,
     * independent set is a set of vertices that no two of them are neighbors.
     * In our implementation, the independent set consists of the smallest uncoloured nodes which are not adjacent to
     * one another.
     * @return
     */
    public Set<Integer> getIndependentSet() {
        ArrayList<Future<Boolean>> list = new ArrayList<>();
        Set<Integer> res = new HashSet<>();
        List<Integer> listOfNodes = new ArrayList<>(independentSet);

        for (Integer node : listOfNodes) {
            Future<Boolean> f = exec.submit(() -> checkNode(node)); // the current node is the smallest among its neighbours (or not => false)
            list.add(f);
        }

        for (int i = 0; i < independentSet.size(); i++) {
            try {
                if (list.get(i).get()) { // if the node is the smallest among its neighbours
                    res.add(listOfNodes.get(i)); // add it to the set
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
