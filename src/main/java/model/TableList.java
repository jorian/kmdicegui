package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.KomodoRPC;

import java.util.ArrayList;

public class TableList {
    ArrayList<Table> tables;

    public TableList() {
        tables = new ArrayList<>();
        getCurrentTables();
    }

    public void getCurrentTables() {
        // dicelist returns a JsonArray of FundingTxnIDs
        JsonElement response = KomodoRPC.POST("dicelist");

        if (response != null) {
            if (response.isJsonArray()) {
                JsonArray fundingTxnIDs = response.getAsJsonArray();
                for (JsonElement jsonElement: fundingTxnIDs) {
                    Table table = new Table();
                    table.fundingTx = fundingTxnIDs.getAsString();
                    table.getInfo();
                    tables.add(table);
                }
            } else {
                JsonObject error = response.getAsJsonObject();
                System.err.println("Something went wrong in getCurrentTables(): " + error.get("error").getAsString());
            }
        } else {
            System.err.println("error getting data");
        }
    }

    public void add(Table table) {
        this.tables.add(table);
    }

    public ArrayList<Table> getTables() {
        return new ArrayList<>(tables);
    }

    @Override
    public String toString() {
        StringBuilder fund = new StringBuilder();
        for (Table table : this.tables) {
            fund.append(table.toString()).append("\n");
        }
        return fund.toString();
    }
}
