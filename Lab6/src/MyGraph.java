import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyGraph {
    private List<List<Integer>> container;
    private List<Integer> nodes;

    MyGraph(int numberNodes){
        this.container = new ArrayList<>(numberNodes);
        this.nodes = new ArrayList<>();

        for(int i=0;i<numberNodes;i++){
            this.container.add(new ArrayList<>()); // adjacency list
            this.nodes.add(i);
        }
    }

    public List<Integer> getNodes(){
        return nodes;
    }

    public int size(){
        return this.container.size();
    }

    public void addEdge(int startNode, int endNode){
        this.container.get(startNode).add(endNode);
    }

    public List<Integer> getNeighbours(int node){
        return this.container.get(node);
    }

    public static MyGraph generateGraph(int size, boolean isHamiltonian){
        MyGraph graph = new MyGraph(size);
        List<Integer> nodes = graph.getNodes();

        Random random = new Random();

        for (int i = 0; i < size; i++) {
            int randomIndexToSwap = random.nextInt(size);
            int temp = nodes.get(randomIndexToSwap);//array[randomIndexToSwap];
            nodes.set(randomIndexToSwap, nodes.get(i));//array[randomIndexToSwap] = array[i];
            nodes.set(i, temp);//array[i] = temp;
        }

        if(isHamiltonian){
            for(int i=1;i<nodes.size();i++){
                graph.addEdge(nodes.get(i-1), nodes.get(i));
            }
            graph.addEdge(nodes.get(nodes.size()-1), nodes.get(0));
        }

        for(int i=0;i<size/2;i++) {
            int start = random.nextInt(size - 1);
            int end = random.nextInt(size - 1);
            if(start != end)
                graph.addEdge(start, end);
        }

        return graph;
    }

    @Override
    public String toString(){
        for(int i=0;i< nodes.size();i++){
            int node = nodes.get(i);
            System.out.println("Node : "+ node);
            for(int j=0;j<this.getNeighbours(node).size();j++){
                System.out.println("-> " + this.getNeighbours(node).get(j));
            }
        }
        return "";
    }

}
