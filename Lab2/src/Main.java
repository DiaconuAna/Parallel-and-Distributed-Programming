import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static ArrayList<Integer> populateList1(int size){
        ArrayList<Integer> list1 = new ArrayList<>();
        for(int i=0;i<size;i++){
            list1.add(1);
        }
        return list1;
    }

    private static ArrayList<Integer> populateList2(int size){
        ArrayList<Integer> list2 = new ArrayList<>();
        for(int i=0;i<size;i++){
//            list2.add((int) (Math.random() * 100));
            list2.add(i+1);
        }
        return list2;
    }

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Enter lists size: ");
        int list_size = keyboard.nextInt();

        ArrayList<Integer> list1 = populateList1(list_size);//new ArrayList<>(Arrays.asList(1,1,1,1,1));
        ArrayList<Integer> list2 = populateList2(list_size);//new ArrayList<>(Arrays.asList(5,10,15,20,25));

        ProducerConsumerBuffer buffer = new ProducerConsumerBuffer();

        Producer producer = new Producer(buffer, list1, list2);
        Consumer consumer = new Consumer(buffer, list1.size());

//        float start = System.nanoTime() / 1000000;

        producer.start();
        consumer.start();

//        float end = System.nanoTime() / 1000000;
//        System.out.printf("\n%f seconds elapsed%n", (end - start) / 1000);
    }
}
