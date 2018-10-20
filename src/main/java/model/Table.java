package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.KomodoRPC;

import java.math.BigDecimal;
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
                this.minBet = new BigDecimal(tableObject.get("minbet").getAsString());
                this.maxBet = new BigDecimal(tableObject.get("maxbet").getAsString());
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

    @Override
    public String toString() {
        return name + " - " + getCurrentFunding();
    }
}
