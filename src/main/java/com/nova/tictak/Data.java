package com.nova.tictak;

public class Data {
    public static final int TIC_STATE = 1;
    public static final int TAK_STATE = 2;
    public static final int TOY_STATE = 3;
    private int state = TIC_STATE;

    public int getState() {
        return state;
    }

    public void Tic() {
        System.out.print("Tic-");
        state = TAK_STATE;
    }

    public void Tak() {
        System.out.print("Tak-");
        state = TOY_STATE;
    }

    public void Toy() {
        System.out.println("Toy");
        state = TIC_STATE;
    }
}
