package model;

import util.KomodoRPC;

public class Bet {
    public String tableName;
    public int amount;
    public int odds;
    public String betTx;
    Result result;

    public Bet(String tableName, int amount, int odds) {
        this.tableName = tableName;
        this.amount = amount;
        this.odds = odds;
    }

    void getStatus() {
        if (!this.betTx.isEmpty()) {
            // do a dicestatus and get the result updated here
            KomodoRPC.getBetStatus();
        }
    }

    void setBetTx(String txId) {
        this.betTx = txId;
    }

    private enum Result {
        WON,
        LOST
    }

    private enum Status {
        ONGOING,
        SUCCESS,
        FAILED
    }
}
