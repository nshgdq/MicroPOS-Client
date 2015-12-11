package ow.micropos.client.desktop.presenter.payment;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ow.micropos.client.desktop.model.charge.Charge;
import ow.micropos.client.desktop.model.enums.ChargeType;
import ow.micropos.client.desktop.model.orders.ChargeEntry;

public class ViewChargeEntry extends ItemPresenter<ChargeEntry> {

    @FXML Label lblChargeName;
    @FXML Label lblChargeValue;
    @FXML Label lblChargeStatus;

    @Override
    protected void updateItem(ChargeEntry currentItem, ChargeEntry newItem) {
        if (newItem == null) {
            unsetLabel(lblChargeName);
            unsetLabel(lblChargeValue);
            unsetLabel(lblChargeStatus);
        } else {
            Charge charge = newItem.getCharge();
            if (charge != null) {
                setLabel(lblChargeName, charge.nameProperty());
                setLabel(lblChargeValue, new StringBinding() {
                    {
                        bind(charge.amountProperty(), charge.typeProperty());
                    }

                    @Override
                    protected String computeValue() {
                        if (charge.getAmount() != null && charge.hasType(ChargeType.FIXED_AMOUNT))
                            return BigDecimalUtils.asDollars(charge.getAmount()).toString();
                        else if (charge.getAmount() != null && charge.hasType(ChargeType.PERCENTAGE))
                            return BigDecimalUtils.asPercent(charge.getAmount()).toString() + "%";
                        else
                            return "-";
                    }
                });
                setLabel(lblChargeStatus, new StringBinding() {
                    {bind(newItem.statusProperty());}

                    @Override
                    protected String computeValue() {
                        switch (newItem.getStatus()) {
                            case REQUEST_APPLY:
                                return "Applying";
                            case REQUEST_VOID:
                                return "Voiding";
                            case APPLIED:
                                return "Applied";
                            case VOID:
                                return "Void";
                            default:
                                return "Error";
                        }
                    }
                });
            }
        }
    }

    @Override
    public void dispose() {
        unsetLabel(lblChargeName);
        getView().setOnMouseClicked(null);
    }
}
