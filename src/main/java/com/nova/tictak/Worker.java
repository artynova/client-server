package com.nova.tictak;

public class Worker extends Thread {
    private final int id;
    private final Data data;

    public Worker(int id, Data data) {
        this.id = id;
        this.data = data;
        this.start();
    }

    @Override
    public void run() {
        try {
            super.run();
            for (int i = 0; i < 5; i++) {
                actInTurn();
            }
        } catch (InterruptedException ignore) {} // nothing else to do
    }

    private void actInTurn() throws InterruptedException {
        synchronized (data) {
            while (data.getState() != id) {
                data.wait();
            }
            act();
            data.notifyAll();
        }
    }

    private void act() {
        switch (id) {
            case Data.TIC_STATE -> data.Tic();
            case Data.TAK_STATE -> data.Tak();
            case Data.TOY_STATE -> data.Toy();
        }
    }
}
