package model;

import util.KomodoRPC;

import java.util.ArrayList;

public class DiceList {
    ArrayList<DiceFund> diceFunds;

    public DiceList() {
        diceFunds = KomodoRPC.fetchDiceList();
    }

    public void add(DiceFund diceFund) {
        this.diceFunds.add(diceFund);
    }

    public ArrayList<DiceFund> getDiceFunds() {
        return new ArrayList<>(diceFunds);
    }

    @Override
    public String toString() {
        StringBuilder fund = new StringBuilder();
        for (DiceFund diceFund : this.diceFunds) {
            fund.append(diceFund.toString()).append("\n");
        }
        return fund.toString();
    }
}
