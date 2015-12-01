package ow.micropos.client.desktop.presenter.dinein;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.seating.Seat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ViewSeat extends ItemPresenter<Seat> {

    @FXML Label lblSeatTag;

    @Override
    protected void updateItem(Seat currentItem, Seat newItem) {
        if (newItem == null) {
            unsetLabel(lblSeatTag);
        } else {
            setLabel(lblSeatTag, newItem.tagProperty());

            Platform.runLater(() -> {
                String style = "";
                switch (getSeatState(getItem())) {
                    case 1:
                        style = "-fx-background-color: yellow";
                        break;
                    case 2:
                        style = "-fx-background-color: purple";
                        break;
                    case 3:
                        style = "-fx-background-color: green";
                        break;
                    case 4:
                        style = "-fx-background-color: blue";
                        break;
                    default:
                        style = "-fx-background-color: #c01800";
                }
                getView().setStyle(style);
            });
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblSeatTag);
        getView().setOnMouseClicked(null);
    }

    private int getSeatState(Seat seat) {
        if (seat == null
                || seat.getSalesOrders() == null
                || seat.getSalesOrders().size() != 1)
            return 0;

        SalesOrder so = seat.getSalesOrders().get(0);
        if (so.getDate() == null)
            return 0;

        long millis = new Date().getTime() - so.getDate().getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        if (minutes < 30)
            return 1;
        else if (minutes < 60)
            return 2;
        else if (minutes < 90)
            return 3;
        else
            return 4;
    }

}
