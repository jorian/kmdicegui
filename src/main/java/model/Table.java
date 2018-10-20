package model;

import util.KomodoRPC;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Table {
    String name;
    String fundingTx;
    double minBet;
    double maxBet;
    int maxOdds;
    int timeoutBlocks;

    ArrayList<Bet> bets;

    public Table(String name, String fundingTx, double minbet, double maxbet, int maxOdds, int timeoutBlocks) {
        this.name = name;
        this.fundingTx = fundingTx;
        this.minBet = minbet;
        this.maxBet = maxbet;
        this.maxOdds = maxOdds;
        this.timeoutBlocks = timeoutBlocks;
        this.bets = new ArrayList<>();
    }

    public BigDecimal getCurrentFunding() {
        return KomodoRPC.getCurrentFunding(this.fundingTx);
    }

    @Override
    public String toString() {
        return name + " - " + getCurrentFunding();
    }

    public void placeBet(int amount, int odds) {
        if (amount >= minBet && amount <= maxBet) {
            Bet bet = new Bet(this.name, amount, odds);

            bet = KomodoRPC.placeBet(bet, fundingTx);
        }

    }


}
