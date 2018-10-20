package model;

public class Bet {
    int amount;
    int odds;
    Result result;

    public Bet(int amount, int odds) {
        this.amount = amount;
        this.odds = odds;
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
