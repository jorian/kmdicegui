package controller;

import model.Bet;
import model.DiceFund;
import model.DiceList;
import util.KomodoRPC;

import java.util.Arrays;
import java.util.Collections;

public class Main {
    public void initialize() {
        KomodoRPC komodoRPC = new KomodoRPC();

        DiceList diceList = new DiceList();
        System.out.println(diceList.toString());
    }
}
