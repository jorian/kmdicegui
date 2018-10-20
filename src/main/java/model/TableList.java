package model;

import util.KomodoRPC;

import java.util.ArrayList;

public class TableList {
    ArrayList<Table> tables;

    public TableList() {
        tables = KomodoRPC.fetchDiceList();
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
