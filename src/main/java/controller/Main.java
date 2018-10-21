package controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import java.net.URL;
import java.util.ResourceBundle;


public class Main implements Initializable {
//    @FXML public Slider oddsSlider;
    @FXML public Label betAmountLabel;
    @FXML public Label oddsLabel;
    @FXML public TextField betAmount;
    @FXML public TextField odds;
    @FXML public Label winOrLose;
    @FXML public Button betBtn;

    Table currentTable;
    private Service<Void> backgroundThread;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        TableList tableList = new TableList();
        currentTable = tableList.getTables().get(0);


    }

    @FXML public void bet(ActionEvent event) {

        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        updateMessage("Pending...");

                        Bet bet = new Bet(
                                currentTable.getName(),
                                currentTable.getFundingTx(),
                                new BigDecimal(betAmount.getText()).setScale(8, RoundingMode.DOWN),
                                Integer.valueOf(odds.getText())
                        );
                        bet = currentTable.placeBet(bet);

                        int i = 1;
                        while (bet.getStatus() == Bet.Status.ONGOING) {
                            try {
                                Thread.sleep(1000);
                                updateMessage("Pending... " + i);
                                i++;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (bet.getStatus() == Bet.Status.SUCCESS) {
                            boolean result = bet.won();
                            if (result) {
                                updateMessage("WON " + bet.getPrize());
                                winOrLose.setTextFill(Color.LIGHTGREEN);
                                System.out.println("main: won: " + bet.getPrize());
                            } else {
                                updateMessage("LOST :(");
                                winOrLose.setTextFill(Color.RED);
                                System.out.println("main: lost");
                            }

                        }
                        return null;
                    }
                };
            }
        };

        backgroundThread.setOnSucceeded(workerStateEvent -> System.out.println("done"));

        winOrLose.textProperty().bind(backgroundThread.messageProperty());

        backgroundThread.restart();
    }
}
