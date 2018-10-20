package model;

import util.KomodoRPC;

import java.math.BigDecimal;

public class DiceFund {
    String name;
    String fundingTx;
    double minBet;
    double maxBet;
    int maxOdds;
    int timeoutBlocks;

    public DiceFund(String name, String fundingTx, double minbet, double maxbet, int maxOdds, int timeoutBlocks) {
        this.name = name;
        this.fundingTx = fundingTx;
        this.minBet = minbet;
        this.maxBet = maxbet;
        this.maxOdds = maxOdds;
        this.timeoutBlocks = timeoutBlocks;
    }

    DiceFund(String name, int initialFunding, double minBet, double maxBet, int maxOdds, int timeoutBlocks) {
        this.name = name;
        this.minBet = minBet;
        this.maxBet = maxBet;
        this.maxOdds = maxOdds;
    }

    public BigDecimal getCurrentFunding() {
        return KomodoRPC.getCurrentFunding(this.fundingTx);
    }

    @Override
    public String toString() {
        return name + " - " + getCurrentFunding();
    }
}
