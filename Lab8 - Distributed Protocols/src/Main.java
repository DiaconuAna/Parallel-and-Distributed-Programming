import mpi.*;

public class Main {
    private static final int ORDER = 5;

    public static void main(String[] args) throws InterruptedException {

        MPI.Init(args);

        int me = MPI.COMM_WORLD.Rank();
        DSM dsm = new DSM();

        // The subscriptions are static and each node knows, for each variable it is subscribed to, which are the
        // other subscribers for that variable.

        switch (me) {
            case 0 -> {
                Thread thread = new Thread(new Listener(dsm));
                thread.start();
                dsm.subscribeTo("a");
                dsm.subscribeTo("b");
                dsm.subscribeTo("c");

                Thread.sleep(1000);

                dsm.compareExchange("a",0,13);
//                dsm.compareExchange("b",1,14);
                dsm.compareExchange("c",2, 15);

                Thread.sleep(3000);
                dsm.close();
                thread.join();
            }
            case 1 -> {
                Thread thread = new Thread(new Listener(dsm));
                thread.start();
                dsm.subscribeTo("a");
                dsm.subscribeTo("c");
                Thread.sleep(3000);
                dsm.compareExchange("a",13,134);
                Thread.sleep(2000);
                thread.join();
            }
            case 2 -> {
                Thread thread = new Thread(new Listener(dsm));
                thread.start();
                dsm.subscribeTo("a");
                dsm.subscribeTo("c");
                dsm.compareExchange("b", 2, 100); // should generate Error Message
                Thread.sleep(3000);
                thread.join();
            }
        }

        MPI.Finalize();
    }
}