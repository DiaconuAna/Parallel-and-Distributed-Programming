import Messages.*;
import mpi.MPI;

public class Listener implements Runnable {

    private final DSM dsm;

    public Listener(DSM dsm) {
        this.dsm = dsm;
    }

    @Override
    public void run() {

        while (true) {
            System.out.printf("Rank %d waiting...\n", MPI.COMM_WORLD.Rank());
            Object[] receivedMessage = new Object[1];

            MPI.COMM_WORLD.Recv(receivedMessage, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
            Message msg = (Message) receivedMessage[0];

            if (msg instanceof CloseMessage) {
                System.out.printf(ConsoleColors.CYAN + "Rank %d stopped listening ...\n" + ConsoleColors.RESET, MPI.COMM_WORLD.Rank());
                return;
            } else if (msg instanceof SubscribeMessage) {
                SubscribeMessage subscribeMsg = (SubscribeMessage) msg;
                System.out.println(ConsoleColors.GREEN + "Subscribe message received" + ConsoleColors.RESET);
                System.out.printf(ConsoleColors.GREEN + "Rank %d received: Rank %d subscribes to %s\n" + ConsoleColors.RESET, MPI.COMM_WORLD.Rank(), subscribeMsg.rank, subscribeMsg.var);
                dsm.synchronizeSubcription(subscribeMsg.var, subscribeMsg.rank);
            } else if (msg instanceof UpdateMessage) {
                UpdateMessage updateMsg = (UpdateMessage) msg;
                System.out.println(ConsoleColors.PURPLE + "Update message received" + ConsoleColors.RESET);
                System.out.printf(ConsoleColors.PURPLE + "Rank %d received: %s -> %d\n" + ConsoleColors.RESET, MPI.COMM_WORLD.Rank(), updateMsg.var, updateMsg.val);
                dsm.setVar(updateMsg.var, updateMsg.val);
            } else if (msg instanceof ErrorMessage) {
                ErrorMessage errorMsg = (ErrorMessage) msg;
                System.out.println(ConsoleColors.RED);
                System.out.println("Error message received");
                System.out.printf("Rank %d received: Process %d tried to update a variable to which he was not subscribed: %s\n", MPI.COMM_WORLD.Rank(), errorMsg.rank, errorMsg.var);
                System.out.println(ConsoleColors.RESET);

            }
        }

    }
}
