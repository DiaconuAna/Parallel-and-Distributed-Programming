import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerBuffer {
    private final Queue<Integer> buffer_queue = new LinkedList<>();
    private final int capacity = 3;

    private final Lock bufferMutex = new ReentrantLock();
    private final Condition bufferCondition = bufferMutex.newCondition();

    public void feed(Integer product) throws InterruptedException {
        this.bufferMutex.lock();
        try {
            while (this.buffer_queue.size() == capacity) {
                System.out.printf("%s: queue is full --- waiting ---\n", Thread.currentThread());
                this.bufferCondition.await(); // wait until the consumer consumes the data
            }
            this.buffer_queue.add(product);
            this.bufferCondition.signal(); // all is good - let the consumer know
            System.out.printf("%s: added %d into the queue\n", Thread.currentThread(), product);
        }
        finally {
            this.bufferMutex.unlock();
        }
    }

    public int consume() throws InterruptedException{
        this.bufferMutex.lock();
        try{
            while (this.buffer_queue.size() == 0){
                System.out.printf("%s: queue is empty --- waiting ---\n", Thread.currentThread());
                this.bufferCondition.await(); // wait until the consumer consumes the data
            }
            Integer product = buffer_queue.poll(); // get first element from queue
            if(product != null){
                System.out.printf("%s: %d taken from the queue\n", Thread.currentThread(), product);
                this.bufferCondition.signal();
            }
            return product;
        }
        finally {
            this.bufferMutex.unlock();
        }
    }
}
