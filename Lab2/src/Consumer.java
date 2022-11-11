public class Consumer extends Thread{
    public int scalarProduct = 0;
    public ProducerConsumerBuffer buffer;
    public int length;

    public Consumer(ProducerConsumerBuffer buffer, int length) {
        super("Consumer thread");
        this.buffer = buffer;
        this.length = length;
    }

    @Override
    public void run(){
        for(int i=0;i<this.length; i++){
            try {
                scalarProduct += buffer.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Partial scalar product >> %d\n", this.scalarProduct);
        }
        System.out.printf("Scalar product >> %d\n", this.scalarProduct);
    }
}
