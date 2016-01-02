package ow.micropos.client.desktop.presenter.dinein;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.target.Seat;

import java.math.BigDecimal;

public class DineInPromptPresenter extends ItemPresenter<Seat> {

    @FXML public TextField tfPartySize;

    @FXML public StackPane btn0;
    @FXML public StackPane btn1;
    @FXML public StackPane btn2;
    @FXML public StackPane btn3;
    @FXML public StackPane btn4;
    @FXML public StackPane btn5;
    @FXML public StackPane btn6;
    @FXML public StackPane btn7;
    @FXML public StackPane btn8;
    @FXML public StackPane btn9;
    @FXML public StackPane btnCancel;
    @FXML public StackPane btnOk;

    @Override
    public void initialize() {
        tfPartySize.setText("0");
        btn0.setOnMouseClicked(event -> updatePartySize(0));
        btn1.setOnMouseClicked(event -> updatePartySize(1));
        btn2.setOnMouseClicked(event -> updatePartySize(2));
        btn3.setOnMouseClicked(event -> updatePartySize(3));
        btn4.setOnMouseClicked(event -> updatePartySize(4));
        btn5.setOnMouseClicked(event -> updatePartySize(5));
        btn6.setOnMouseClicked(event -> updatePartySize(6));
        btn7.setOnMouseClicked(event -> updatePartySize(7));
        btn8.setOnMouseClicked(event -> updatePartySize(8));
        btn9.setOnMouseClicked(event -> updatePartySize(9));
        btnCancel.setOnMouseClicked(event -> Platform.runLater(App.main::backRefresh));
        btnOk.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (getItem() == null) {
                App.notify.showAndWait("Unexpected Error.");
                App.main.backRefresh();
            }

            try {
                int partySize = Integer.parseInt(tfPartySize.getText());

                if (partySize >= App.properties.getInt("gratuity-party-size")) {
                    App.main.setSwapRefresh(
                            App.orderEditorPresenter,
                            SalesOrder.forSeat(
                                    App.employee,
                                    getItem(),
                                    App.properties.getBd("tax-percent"),
                                    App.properties.getBd("gratuity-percent")
                            )
                    );
                } else {
                    App.main.setSwapRefresh(
                            App.orderEditorPresenter,
                            SalesOrder.forSeat(
                                    App.employee,
                                    getItem(),
                                    App.properties.getBd("tax-percent"),
                                    BigDecimal.ZERO
                            )
                    );
                }

            } catch (NumberFormatException e) {
                App.notify.showAndWait("Invalid Party Size.");
                refresh();
            }
        }));
    }

    @Override
    public void refresh() {
        tfPartySize.setText("0");
    }

    @Override
    protected void updateItem(Seat currentItem, Seat newItem) {
        // Nothing to update yet.
    }

    @Override
    public void dispose() {

    }

    private void updatePartySize(int input) {
        Platform.runLater(() -> {
            try {
                int partySize = Integer.parseInt(tfPartySize.getText() + input);
                tfPartySize.setText(Integer.toString(partySize));
            } catch (NumberFormatException e) {
                // Ignore
            }
        });
    }
}
