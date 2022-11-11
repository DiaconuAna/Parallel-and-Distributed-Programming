package model;

import java.util.TimerTask;
public class ProductChecker extends TimerTask {
    private final AcquisitionService acquisitionService;

    public ProductChecker(AcquisitionService as){
        this.acquisitionService = as;
    }

    @Override
    synchronized public void run() {
        AcquisitionService.priceCheckerLock.lock();
        System.out.println("---- On going product check ---- ");

        System.out.printf("Product total quantity: %d - sold: %d - remain - %d%n", acquisitionService.getTotalQuantity(), acquisitionService.getSoldQuantity(), acquisitionService.getRemainingQuantity());

        if(acquisitionService.getTotalQuantity() != acquisitionService.getSoldQuantity() + acquisitionService.getRemainingQuantity()){
            System.err.println("Quantity check failed!");
        }

        if(acquisitionService.getBillsProfit() != acquisitionService.getProfit()){
            System.err.println("Profit check failed");
        }

        System.out.println("---- Finished product check ---- ");
        AcquisitionService.priceCheckerLock.unlock();
    }
}
