import Messages.*;
import mpi.MPI;

import javax.imageio.ImageTranscoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DSM {

    public Map<String, Set<Integer>> subscribers;

    // There shall be a predefined number of communicating processes.
    public int a, b, c;
    public static final Lock lock = new ReentrantLock();

    public DSM() {

        // The DSM mechanism shall provide a predefined number of integer variables residing on each of the processes.
        a = 0;
        b = 1;
        c = 2;
        subscribers = new ConcurrentHashMap<>();
        subscribers.put("a", new HashSet<>());
        subscribers.put("b", new HashSet<>());
        subscribers.put("c", new HashSet<>());
    }

    public void close() {
        this.sendAll(new CloseMessage());
    }

    public void setVar(String var, int value) {
        switch (var) {
            case "a" -> {
                this.a = value;
            }
            case "b" -> {
                this.b = value;
            }
            case "c" -> {
                this.c = value;
            }
        }
    }

    // a "compare and exchange" operation, that compares a variable with a given value and,
    // if equal, it sets the variable to another given value
    public void compareExchange(String var, int oldVal, int newVal) {
        // first check if the current node is subscribed to that variable
        if(!this.subscribers.get(var).contains(MPI.COMM_WORLD.Rank())){
            this.sendToSubscribers(var, new ErrorMessage(var, MPI.COMM_WORLD.Rank()));
        }

        if(var.equals("a") && this.a == oldVal){
            updateVariable("a", newVal);
        }
        if(var.equals("b") && this.b == oldVal){
            updateVariable("b", newVal);
        }
        if(var.equals("c") && this.c == oldVal){
            updateVariable("c", newVal);
        }

    }

    public void updateVariable(String var, int value) {
        lock.lock();
        this.setVar(var, value);
        Message updateMsg = new UpdateMessage(var, value);
        this.sendToSubscribers(var, updateMsg);
        lock.unlock();
    }

    public void subscribeTo(String var) {
        Set<Integer> varSubs = this.subscribers.get(var);
        varSubs.add(MPI.COMM_WORLD.Rank()); // add the current process to the subscribers list
        this.subscribers.put(var, varSubs);
        this.sendAll(new SubscribeMessage(var, MPI.COMM_WORLD.Rank()));

    }

    public void synchronizeSubcription(String var, int rank) {
        Set<Integer> varSubs = this.subscribers.get(var);
        varSubs.add(rank);
        this.subscribers.put(var, varSubs);
    }

    public void sendToSubscribers(String var, Message msg) {
        for (int i = 0; i < MPI.COMM_WORLD.Size(); i++) {
            // Only nodes that subscribe to a variable will receive notifications about changes of that variable
            if (MPI.COMM_WORLD.Rank() == i || !subscribers.get(var).contains(i))
                continue;

            MPI.COMM_WORLD.Send(new Object[]{msg}, 0, 1, MPI.OBJECT, i, 0);
        }
    }

    private void sendAll(Message msg) {
        for (int i = 0; i < MPI.COMM_WORLD.Size(); i++) {
            if (MPI.COMM_WORLD.Rank() == i && !(msg instanceof CloseMessage))
                continue;
            MPI.COMM_WORLD.Send(new Object[]{msg}, 0, 1, MPI.OBJECT, i, 0);
        }
    }
}
