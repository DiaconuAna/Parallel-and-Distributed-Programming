public class Main {

    private static Graph getGraph(){

        // ---------- version 1 --------------

        int size = 5;
        int edgeCount = 7;
        Graph g = Graph.generateGraph(size, edgeCount);
        System.out.println(" >>> " + g);
//        Graph g = new Graph(size);
//        g.addEdge(0, 1);
//        g.addEdge(0, 2);
//        g.addEdge(1, 2);
//        g.addEdge(1, 3);
//        g.addEdge(2, 3);
//        g.addEdge(3, 4);

        // ---------- version 2 --------------

//        int size = 6;
//        Graph g = new Graph(size);
//
//        g.addEdge(0,1);
//        g.addEdge(0,2);
//        g.addEdge(0,3);
//        g.addEdge(2,3);
//        g.addEdge(2,4);
//        g.addEdge(3,4);
//        g.addEdge(3,5);

        return g;
    }

    public static void main(String[] args) {

        Graph g = getGraph();

        long startTime = System.nanoTime();
        g.colorGraph();
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        g.printColors();

        System.out.println("it took: " + (double)duration/1000000000 + " seconds.");

    }
}
