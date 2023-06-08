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
        super.run();
        for (int i = 0; i < 5; i++) {
            actInTurn();
        }
    }

    private void actInTurn() {
        synchronized (data) {
            while (data.getState() != id) {
                try {
                    data.wait();
                } catch (InterruptedException ignore) {} // nothing to finish
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
