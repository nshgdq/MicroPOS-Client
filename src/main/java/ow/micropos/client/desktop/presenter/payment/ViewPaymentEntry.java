package ow.micropos.client.desktop.presenter.payment;


import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ow.micropos.client.desktop.model.orders.PaymentEntry;

public class ViewPaymentEntry extends ItemPresenter<PaymentEntry> {

    @FXML Label lblPaymentStatus;
    @FXML Label lblPaymentType;
    @FXML Label lblPaymentAmount;

    @Override
    protected void updateItem(PaymentEntry currentItem, PaymentEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblPaymentType);
            unsetLabel(lblPaymentAmount);
            unsetLabel(lblPaymentStatus);
        } else {
            setLabel(lblPaymentStatus, new StringBinding() {
                {bind(newItem.statusProperty());}

                @Override
                protected String computeValue() {
                    switch (newItem.getStatus()) {
                        case REQUEST_PAID:
                            return "Paying";
                        case REQUEST_VOID:
                            return "Voiding";
                        case PAID:
                            return "Paid";
                        case VOID:
                            return "Void";
                        default:
                            return "Error";
                    }
                }
            });
            setLabel(lblPaymentType, new StringBinding() {
                {bind(newItem.typeProperty());}

                @Override
                protected String computeValue() {
                    switch (newItem.getType()) {
                        case CASH:
                            return "Cash";
                        case CREDIT:
                            return "Credit";
                        case CHECK:
                            return "Check";
                        case GIFTCARD:
                            return "Giftcard";
                        default:
                            return "No type";
                    }
                }
            });
            setLabel(lblPaymentAmount, newItem.amountProperty().asString());
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblPaymentType);
        unsetLabel(lblPaymentAmount);
        getView().setOnMouseClicked(null);
    }


    /******************************************************************
     *                                                                *
     * Fit width of label to width of ListView
     *                                                                *
     ******************************************************************/

    public final void fixWidth(ListView<?> lv) {
        ListViewUtils.bindFitToListView(lblPaymentType, lv);
    }
}
