package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import util.KomodoRPC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Table {
    String name;

    String fundingTx;
    BigDecimal minBet;
    BigDecimal maxBet;
    int maxOdds;
    int timeoutBlocks;
    ArrayList<Bet> bets;

    public Table() { }

    public Table(String name, String fundingTx, BigDecimal minbet, BigDecimal maxbet, int maxOdds, int timeoutBlocks) {
        this.name = name;
        this.fundingTx = fundingTx;
        this.minBet = minbet;
        this.maxBet = maxbet;
        this.maxOdds = maxOdds;
        this.timeoutBlocks = timeoutBlocks;
        this.bets = new ArrayList<>();
    }

    public void getInfo() {
        JsonElement rpcResponseJson = KomodoRPC.POST("diceinfo " + this.fundingTx);

        // returns an object with 9 fields:
        JsonObject tableObject = rpcResponseJson.getAsJsonObject();

        switch (tableObject.get("result").getAsString()) {
            case "success":
                this.name = tableObject.get("name").getAsString();
                this.minBet = new BigDecimal(tableObject.get("minbet").getAsString()).setScale(8, RoundingMode.DOWN);
                this.maxBet = new BigDecimal(tableObject.get("maxbet").getAsString()).setScale(8, RoundingMode.DOWN);
                this.maxOdds = tableObject.get("maxodds").getAsInt();
                this.timeoutBlocks = tableObject.get("timeoutblocks").getAsInt();
                break;
            case "error":
                System.err.println("Something went wrong in table.getInfo(): " + tableObject.get("error").getAsString());
                break;
            default:
                System.err.println("An unknown error occurred: " + tableObject.toString());
                break;
        }
    }

    public BigDecimal getCurrentFunding() {
        JsonElement rpcResponseJson = KomodoRPC.POST("diceinfo " + this.fundingTx);
        JsonObject tableObject = rpcResponseJson.getAsJsonObject();

        switch (tableObject.get("result").getAsString()) {
            case "success":
                return new BigDecimal(tableObject.get("funding").getAsString());
            case "error":
                System.err.println("Something went wrong in table.getCurrentFunding(): " + tableObject.get("error").getAsString());
                break;
            default:
                System.err.println("An unknown error occurred: " + tableObject.toString());
                break;
        }
        return new BigDecimal(0);
        // todo something else than 0 should be returned
    }

    public Bet placeBet(Bet bet) {
        // todo check odds
        if (bet.amount.compareTo(this.minBet) < 0) {
            System.out.println("this bet is lower than the minbet");
        } else if (bet.amount.compareTo(this.maxBet) > 0) {
            System.out.println("this bet is higher than the maxbet");
        } else {
            System.out.println("this bet is ok: " + bet.amount + ", minbet: " + minBet + ", maxbet: " + maxBet);

            JsonElement rpcResponseJson = KomodoRPC.POST("dicebet " + this.name + " " + this.fundingTx + " " + bet.amount + " " + bet.odds);
            JsonObject betResultObject = rpcResponseJson.getAsJsonObject();

            switch (betResultObject.get("result").getAsString()) {
                case "success":
                    rpcResponseJson = KomodoRPC.POST("sendrawtransaction " + betResultObject.get("hex").getAsString());
                    // this returns a JSON with response:txid
                    JsonPrimitive txID = rpcResponseJson.getAsJsonPrimitive(); // the txID string is a JsonPrimitive.

                    bet.betTx = txID.getAsString();
                    System.out.println("bet.betTx:" + bet.betTx);
//
                    // todo what if it isn't a txid? like when input is spent or whatever?
                    // signrawtransaction still returns just strings, not json
                    break;
                case "error":
                    if (rpcResponseJson.isJsonObject()) {
                        JsonObject errorObject = new JsonObject();
                        System.out.println(errorObject.get("error").getAsString());
                    }
                    //Caused by: java.lang.IllegalStateException: Not a JSON Primitive: {"result":"error","error":"cant find dice entropy inputs"}
                    break;
                    // DiceINIT means the fundingtx is not confirmed, or doesn't exist.
            }
        }

        return bet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFundingTx() {
        return fundingTx;
    }

    public void setFundingTx(String fundingTx) {
        this.fundingTx = fundingTx;
    }

    public BigDecimal getMinBet() {
        return minBet;
    }

    public void setMinBet(BigDecimal minBet) {
        this.minBet = minBet;
    }

    public BigDecimal getMaxBet() {
        return maxBet;
    }

    public void setMaxBet(BigDecimal maxBet) {
        this.maxBet = maxBet;
    }

    public int getMaxOdds() {
        return maxOdds;
    }

    public void setMaxOdds(int maxOdds) {
        this.maxOdds = maxOdds;
    }

    public int getTimeoutBlocks() {
        return timeoutBlocks;
    }

    public void setTimeoutBlocks(int timeoutBlocks) {
        this.timeoutBlocks = timeoutBlocks;
    }

    public ArrayList<Bet> getBets() {
        return bets;
    }

    public void setBets(ArrayList<Bet> bets) {
        this.bets = bets;
    }

    @Override
    public String toString() {
        return name + " - " + getCurrentFunding();
    }
}
