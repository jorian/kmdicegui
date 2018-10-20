package controller;

import model.TableList;
import util.KomodoRPC;

public class Main {
    public void initialize() {
        KomodoRPC komodoRPC = new KomodoRPC();

        TableList tableList = new TableList();
        System.out.println(tableList.toString());
    }
}
