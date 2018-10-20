package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.KomodoRPC;

import java.math.BigDecimal;

public class Bet {
    public String tableName;
    public String fundingTx;
    public BigDecimal amount;
    public int odds;
    public String betTx;
    Result result;

    public Bet(String tableName, String fundingTx, BigDecimal amount, int odds) {
        this.tableName = tableName;
        this.fundingTx = fundingTx;
        this.amount = amount;
        this.odds = odds;
    }

    public Status getStatus() {

        JsonElement rpcResponseJson = KomodoRPC.POST("dicestatus " + this.tableName + " " + this.fundingTx + " " + this.betTx);
        JsonObject betStatus = rpcResponseJson.getAsJsonObject();

        switch (betStatus.get("result").getAsString()) {
            case "success":
                String status = betStatus.get("status").getAsString();

                switch (status) {
                    case "win":
                        BigDecimal won = betStatus.get("won").getAsBigDecimal();
                        this.result = new Result(true, won);

                        System.out.println("WON: " + won.toString());
                        return Status.SUCCESS;
                    case "loss":
                        this.result = new Result(false);
                        System.out.println("LOSS");
                        return Status.SUCCESS;
                    default:
                        System.out.println("bet still pending");
                        return Status.ONGOING;
                }
            default:
                System.err.println("ERROR: " + betStatus.get("error").getAsString());
                return Status.FAILED;
        }
    }



    void setBetTx(String txId) {
        this.betTx = txId;
    }

    public boolean won() {
        return this.result.win;
    }

    public BigDecimal getPrize() {
        return result.prize;
    }

    private class Result {
        Status status;
        boolean win;
        BigDecimal prize;

        Result(boolean win) {
            this.win = false;
            this.prize = new BigDecimal(0);
        }

        Result(boolean win, BigDecimal prize) {
            this.win = true;
            this.prize = prize;
        }
    }

    public enum Status {
        ONGOING,
        SUCCESS,
        FAILED
    }
}
