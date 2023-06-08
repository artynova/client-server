package com.nova.tictak;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Data data = new Data();

        Worker toyWorker = new Worker(Data.TOY_STATE, data);
        Worker takWorker = new Worker(Data.TAK_STATE, data);
        Worker ticWorker = new Worker(Data.TIC_STATE, data);

        toyWorker.join();
        System.out.println("end of main...");
    }
}
