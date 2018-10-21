package controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
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
    @FXML public GridPane pane;
//    @FXML public Slider oddsSlider;
    @FXML public Label betAmountLabel;
    @FXML public Label oddsLabel;
    @FXML public TextField betAmount;

    IntField odds;
    @FXML public Label winOrLose;
    @FXML public Button betBtn;
    @FXML public Slider oddsSlider;

    Table currentTable;
    private Service<Void> backgroundThread;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableList tableList = new TableList();
        currentTable = tableList.getTables().get(0);

        odds = new IntField(1,currentTable.getMaxOdds(),1);
        GridPane.setHalignment(odds, HPos.CENTER);
        GridPane.setColumnIndex(odds, 3);
        GridPane.setRowIndex(odds, 1);
        pane.getChildren().add(odds);
//        <IntField fx:id="odds" GridPane.columnIndex="3" GridPane.rowIndex="1" GridPane.halignment="CENTER"/>


        oddsSlider.setMin(1.0);
        oddsSlider.setMax(currentTable.getMaxOdds());
        oddsSlider.setShowTickLabels(true);
        oddsSlider.setMajorTickUnit(currentTable.getMaxOdds());
//        oddsSlider.tick

        oddsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1 == null) {
                    odds.setText("0");
                }

                odds.setText(Math.round(t1.intValue()) + "");
            }
        });

        odds.valueProperty().bindBidirectional(oddsSlider.valueProperty());
    }

    @FXML public void bet(ActionEvent event) {
        backgroundThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        updateMessage("Pending...");
                        winOrLose.setTextFill(Color.BLACK);

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

    class IntField extends TextField {
        final private IntegerProperty value;
        final private int minValue;
        final private int maxValue;

        // expose an integer value property for the text field.
        public int  getValue()                 { return value.getValue(); }
        public void setValue(int newValue)     { value.setValue(newValue); }
        public IntegerProperty valueProperty() { return value; }

        IntField(int minValue, int maxValue, int initialValue) {
            if (minValue > maxValue)
                throw new IllegalArgumentException(
                        "IntField min value " + minValue + " greater than max value " + maxValue
                );
            if (maxValue < minValue)
                throw new IllegalArgumentException(
                        "IntField max value " + minValue + " less than min value " + maxValue
                );
            if (!((minValue <= initialValue) && (initialValue <= maxValue)))
                throw new IllegalArgumentException(
                        "IntField initialValue " + initialValue + " not between " + minValue + " and " + maxValue
                );

            // initialize the field values.
            this.minValue = minValue;
            this.maxValue = maxValue;
            value = new SimpleIntegerProperty(initialValue);
            setText(initialValue + "");

            final IntField intField = this;

            // make sure the value property is clamped to the required range
            // and update the field's text to be in sync with the value.
            value.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                    if (newValue == null) {
                        intField.setText("");
                    } else {
                        if (newValue.intValue() < intField.minValue) {
                            value.setValue(intField.minValue);
                            return;
                        }

                        if (newValue.intValue() > intField.maxValue) {
                            value.setValue(intField.maxValue);
                            return;
                        }

                        if (newValue.intValue() == 0 && (textProperty().get() == null || "".equals(textProperty().get()))) {
                            // no action required, text property is already blank, we don't need to set it to 0.
                        } else {
                            intField.setText(newValue.toString());
                        }
                    }
                }
            });

            // restrict key input to numerals.
            this.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
                @Override public void handle(KeyEvent keyEvent) {
                    if (!"0123456789".contains(keyEvent.getCharacter())) {
                        keyEvent.consume();
                    }
                }
            });

            // ensure any entered values lie inside the required range.
            this.textProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if (newValue == null || "".equals(newValue)) {
                        value.setValue(0);
                        return;
                    }

                    final int intValue = Integer.parseInt(newValue);

                    if (intField.minValue > intValue || intValue > intField.maxValue) {
                        textProperty().setValue(oldValue);
                    }

                    value.set(Integer.parseInt(textProperty().get()));
                }
            });
        }
    }
}
