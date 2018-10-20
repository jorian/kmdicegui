package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import model.Bet;
import model.Table;
import model.TableList;
import util.KomodoRPC;

import java.math.BigDecimal;
import java.math.RoundingMode;



public class Main {
//    @FXML public Slider oddsSlider;
    public void initialize() {













//        TableList tableList = new TableList();
//
//
//        // select table in GUI:
//        // getSelected()
//
//
//
//        Table currentTable = tableList.getTables().get(0);
//
//        // BET should always have 8 decimal places, let GUI guide.
//        Bet bet = new Bet(currentTable.getName(), currentTable.getFundingTx(), new BigDecimal(1).setScale(8, RoundingMode.DOWN), 2);
//
//        bet = currentTable.placeBet(bet);
//
//        while (bet.getStatus() == Bet.Status.ONGOING) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (bet.getStatus() == Bet.Status.SUCCESS) {
//            boolean result = bet.won();
//            if (result)
//                System.out.println("main: won: " + bet.getPrize());
//        }
    }
}
