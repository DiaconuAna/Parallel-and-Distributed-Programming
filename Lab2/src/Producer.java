import java.util.ArrayList;

public class Producer extends Thread {
    public ProducerConsumerBuffer buffer;
    public int length;
    public ArrayList<Integer> list1;
    public ArrayList<Integer> list2;

    public Producer(ProducerConsumerBuffer buffer, ArrayList<Integer> l1, ArrayList<Integer> l2) {
        super("Producer thread");
        this.buffer = buffer;
        this.list1 = l1;
        this.list2 = l2;
        this.length = l1.size();
    }

    @Override
    public void run(){
        for(int i=0;i<length;i++){
            System.out.printf("Producer sends >>> %d * %d\n", this.list1.get(i), this.list2.get(i));
            try {
                this.buffer.feed(this.list1.get(i) * this.list2.get(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
