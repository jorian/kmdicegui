package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import model.Bet;
import model.Table;
import model.TableList;
import util.KomodoRPC;

import java.math.BigDecimal;
import java.math.RoundingMode;



public class Main {
//    @FXML public Slider oddsSlider;
    @FXML public Label betAmountLabel;
    @FXML public Label oddsLabel;
    @FXML public TextField betAmount;
    @FXML public TextField odds;
    @FXML public Label winOrLose;

    Table currentTable;

    public void initialize() {

        TableList tableList = new TableList();


        // select table in GUI:
        // getSelected()



        currentTable = tableList.getTables().get(0);
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

    public void bet() {
        winOrLose.setText("Pending...");

        Bet bet = new Bet(
                currentTable.getName(),
                currentTable.getFundingTx(),
                new BigDecimal(betAmount.getText()).setScale(8, RoundingMode.DOWN),
                Integer.valueOf(odds.getText())
        );
        bet = currentTable.placeBet(bet);

        while (bet.getStatus() == Bet.Status.ONGOING) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (bet.getStatus() == Bet.Status.SUCCESS) {
            boolean result = bet.won();
            if (result) {
                winOrLose.setText("WON " + bet.getPrize());
                winOrLose.setTextFill(Color.LIGHTGREEN);
                System.out.println("main: won: " + bet.getPrize());
            } else {
                winOrLose.setText("LOST :(");
                System.out.println("main: lost");
            }
        }


    }
}
