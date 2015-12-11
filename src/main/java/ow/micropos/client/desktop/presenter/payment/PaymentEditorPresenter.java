package ow.micropos.client.desktop.presenter.payment;

import email.com.gmail.ttsai0509.javafx.binding.CentStringBinding;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.*;
import ow.micropos.client.desktop.model.menu.Charge;
import ow.micropos.client.desktop.model.orders.ChargeEntry;
import ow.micropos.client.desktop.model.orders.PaymentEntry;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.presenter.order.ViewProductEntry;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.Callback;

import java.math.BigDecimal;
import java.util.List;

public class PaymentEditorPresenter extends ItemPresenter<SalesOrder> {

    @FXML Label time;
    @FXML ListView<ProductEntry> orderEntries;
    @FXML Label employee;
    @FXML Label target;
    @FXML Label info;
    @FXML Label status;
    @FXML Label productTotal;
    @FXML Label chargeTotal;
    @FXML Label gratuityTotal;
    @FXML Label taxTotal;
    @FXML Label grandTotal;
    //@FXML Label lblPaidAmount;
    //@FXML Label lblChangeAmount;

    @FXML ListView<Charge> lvCharges;

    @FXML TextField tfAmount;
    @FXML Button btn7;
    @FXML Button btn8;
    @FXML Button btn9;
    @FXML Button btn4;
    @FXML Button btn5;
    @FXML Button btn6;
    @FXML Button btn1;
    @FXML Button btn2;
    @FXML Button btn3;
    @FXML Button btn0;
    @FXML Button btn00;
    @FXML Button btnEvenTender;
    @FXML Button btnClear;
    @FXML Button btnCash;
    @FXML Button btnCredit;
    @FXML Button btnCheck;
    @FXML Button btnGiftCard;

    @FXML ListView<ChargeEntry> lvChargeEntries;
    @FXML ListView<PaymentEntry> lvPaymentEntries;

    private StringProperty rawAmount = new SimpleStringProperty("");
    private CentStringBinding amountFormatter = new CentStringBinding(rawAmount);

    @FXML
    public void initialize() {

        //GridPane.setHalignment(lblPaidAmount, HPos.RIGHT);
        //GridPane.setHalignment(lblChangeAmount, HPos.RIGHT);

        GridPane.setHalignment(info, HPos.CENTER);
        GridPane.setHalignment(employee, HPos.LEFT);
        GridPane.setHalignment(status, HPos.RIGHT);
        GridPane.setHalignment(target, HPos.LEFT);
        GridPane.setHalignment(time, HPos.RIGHT);
        GridPane.setHalignment(productTotal, HPos.RIGHT);
        GridPane.setHalignment(chargeTotal, HPos.RIGHT);
        GridPane.setHalignment(gratuityTotal, HPos.RIGHT);
        GridPane.setHalignment(taxTotal, HPos.RIGHT);
        GridPane.setHalignment(grandTotal, HPos.RIGHT);

        orderEntries.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/order/view_product_entry.fxml");
            presenter.fixWidth(orderEntries);
            return new PresenterCellAdapter<>(presenter);
        });

        lvChargeEntries.setCellFactory(param -> {
            ViewChargeEntry presenter = Presenter.load("/view/pay/view_charge_entry.fxml");
            presenter.onClick(event -> Platform.runLater(() -> {
                ChargeEntry ce = presenter.getItem();

                if (ce.hasStatus(ChargeEntryStatus.REQUEST_APPLY)) {
                    getItem().chargeEntriesProperty().remove(ce);

                } else if (ce.hasStatus(ChargeEntryStatus.APPLIED)
                        && App.employee.hasPermission(Permission.VOID_CHARGE_ENTRY)) {
                    ce.setStatus(ChargeEntryStatus.REQUEST_VOID);

                } else if (ce.hasStatus(ChargeEntryStatus.REQUEST_VOID)) {
                    ce.setStatus(ChargeEntryStatus.APPLIED);
                }
            }));
            return new PresenterCellAdapter<>(presenter);
        });

        lvPaymentEntries.setCellFactory(param -> {
            ViewPaymentEntry presenter = Presenter.load("/view/pay/view_payment_entry.fxml");
            presenter.onClick(event -> Platform.runLater(() -> {
                PaymentEntry pe = presenter.getItem();

                if (pe.hasStatus(PaymentEntryStatus.REQUEST_PAID)) {
                    getItem().paymentEntriesProperty().remove(pe);

                } else if (pe.hasStatus(PaymentEntryStatus.PAID)
                        && App.employee.hasPermission(Permission.VOID_PAYMENT_ENTRY)) {
                    pe.setStatus(PaymentEntryStatus.REQUEST_VOID);

                } else if (pe.hasStatus(PaymentEntryStatus.REQUEST_VOID)) {
                    pe.setStatus(PaymentEntryStatus.PAID);
                }
            }));
            return new PresenterCellAdapter<>(presenter);
        });

        lvCharges.setCellFactory(param -> {
            ViewCharge presenter = Presenter.load("/view/pay/view_charge.fxml");
            presenter.onClick(event -> Platform.runLater(() -> {
                ChargeEntry entry = new ChargeEntry(presenter.getItem());
                lvChargeEntries.getItems().add(entry);
            }));
            return new PresenterCellAdapter<>(presenter);
        });

    }

    @Override
    public void refresh() {
        rawAmount.set("");

        if (App.apiIsBusy.compareAndSet(false, true)) {
            App.api.getCharges((AlertCallback<List<Charge>>) (charges, response) ->
                            lvCharges.setItems(FXCollections.observableList(charges))
            );
        }
    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {
        if (newItem == null) {
            disableKeypad();
            unsetListView(lvPaymentEntries);
            //unsetLabel(lblChangeAmount);
            //unsetLabel(lblPaidAmount);

        } else {
            setListView(lvPaymentEntries, newItem.getPaymentEntries());
            //setLabel(lblChangeAmount, newItem.changeProperty().asString());
            //setLabel(lblPaidAmount, newItem.paymentTotalProperty().asString());

            if (newItem.changeProperty().get().compareTo(BigDecimal.ZERO) >= 0)
                disableKeypad();
            else
                enableKeypad();
        }

        if (newItem == null) {
            unsetLabel(status);
            unsetLabel(target);
            unsetLabel(employee);
            unsetLabel(info);
            unsetLabel(productTotal);
            unsetLabel(chargeTotal);
            unsetLabel(gratuityTotal);
            unsetLabel(taxTotal);
            unsetLabel(grandTotal);
            unsetListView(orderEntries);
            unsetListView(lvChargeEntries);
        } else {
            setLabel(info, new StringBinding() {
                {bind(newItem.idProperty());}

                @Override
                protected String computeValue() {
                    Long id = newItem.idProperty().get();
                    return (id == null) ? "---" : "Order # " + Long.toString(id);
                }

                @Override
                public void dispose() {
                    unbind(newItem.idProperty());
                }
            });
            setLabel(status, newItem.statusTextProperty());
            setLabel(employee, newItem.employeeNameProperty());
            setLabel(target, newItem.targetNameProperty());
            setLabel(time, newItem.prettyTimeProperty());
            setLabel(productTotal, newItem.productTotalProperty().asString());
            setLabel(chargeTotal, newItem.chargeTotalProperty().asString());
            setLabel(grandTotal, newItem.grandTotalProperty().asString());
            setLabel(gratuityTotal, newItem.gratuityTotalProperty().asString());
            setLabel(taxTotal, newItem.taxTotalProperty().asString());
            setListView(orderEntries, newItem.productEntriesProperty());
            setListView(lvChargeEntries, newItem.chargeEntriesProperty());
            setListView(lvPaymentEntries, newItem.paymentEntriesProperty());
        }
    }

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    @Override
    public void dispose() {
        disableKeypad();
        unsetListView(lvPaymentEntries);
        //unsetLabel(lblChangeAmount);
        //unsetLabel(lblPaidAmount);

        unsetLabel(status);
        unsetLabel(target);
        unsetLabel(employee);
        unsetLabel(info);
        unsetLabel(productTotal);
        unsetLabel(chargeTotal);
        unsetLabel(gratuityTotal);
        unsetLabel(taxTotal);
        unsetLabel(grandTotal);
        unsetListView(orderEntries);
    }

    private void addPaymentEntry(PaymentEntryType type) {

        String strAmount = amountFormatter.get().isEmpty() ? "0.00" : amountFormatter.get();
        BigDecimal amount = BigDecimalUtils.asDollars(new BigDecimal(strAmount));

        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            PaymentEntry payment = new PaymentEntry(amount, type);
            getItem().paymentEntriesProperty().add(payment);
            rawAmount.set("");
        } else {
            App.warn.showAndWait("You must enter a payment amount.");
        }

        if (getItem().changeProperty().get().compareTo(BigDecimal.ZERO) >= 0) {
            getItem().setStatus(SalesOrderStatus.REQUEST_CLOSE);
            App.api.postSalesOrder(getItem(), onPost);
        }
    }

    private void enableKeypad() {
        setTextProperty(tfAmount.textProperty(), amountFormatter);
        btnClear.setOnAction(event -> rawAmount.set(""));
        btn00.setOnAction(event -> rawAmount.set(rawAmount.get() + "00"));
        btn0.setOnAction(event -> rawAmount.set(rawAmount.get() + "0"));
        btn1.setOnAction(event -> rawAmount.set(rawAmount.get() + "1"));
        btn2.setOnAction(event -> rawAmount.set(rawAmount.get() + "2"));
        btn3.setOnAction(event -> rawAmount.set(rawAmount.get() + "3"));
        btn4.setOnAction(event -> rawAmount.set(rawAmount.get() + "4"));
        btn5.setOnAction(event -> rawAmount.set(rawAmount.get() + "5"));
        btn6.setOnAction(event -> rawAmount.set(rawAmount.get() + "6"));
        btn7.setOnAction(event -> rawAmount.set(rawAmount.get() + "7"));
        btn8.setOnAction(event -> rawAmount.set(rawAmount.get() + "8"));
        btn9.setOnAction(event -> rawAmount.set(rawAmount.get() + "9"));
        btnCash.setOnAction(event -> addPaymentEntry(PaymentEntryType.CASH));
        btnCredit.setOnAction(event -> addPaymentEntry(PaymentEntryType.CREDIT));
        btnCheck.setOnAction(event -> addPaymentEntry(PaymentEntryType.CHECK));
        btnGiftCard.setOnAction(event -> addPaymentEntry(PaymentEntryType.GIFTCARD));
        btnEvenTender.setOnAction(event -> rawAmount.set(getItem()
                        .changeProperty()
                        .get()
                        .negate()
                        .toString()
                        .replace(".", "")
        ));
    }

    private void disableKeypad() {
        unsetTextProperty(tfAmount.textProperty(), "Already Paid");
        btnEvenTender.setOnAction(null);
        btnClear.setOnAction(null);
        btn00.setOnAction(null);
        btn0.setOnAction(null);
        btn1.setOnAction(null);
        btn2.setOnAction(null);
        btn3.setOnAction(null);
        btn4.setOnAction(null);
        btn5.setOnAction(null);
        btn6.setOnAction(null);
        btn7.setOnAction(null);
        btn8.setOnAction(null);
        btn9.setOnAction(null);
        btnCash.setOnAction(null);
        btnCredit.setOnAction(null);
        btnCheck.setOnAction(null);
        btnGiftCard.setOnAction(null);
    }

    private Callback<Long> onPost = (AlertCallback<Long>) (aLong, response) -> Platform.runLater(() -> {
        App.main.backRefresh();
        App.warn.showAndWait("Change Due : " + getItem().changeProperty().get().toString());
    });

    /*****************************************************************************
     *                                                                           *
     * Menu Buttons
     *                                                                           *
     *****************************************************************************/

    private final ObservableList<Action> menu = FXCollections.observableArrayList(

            new Action("Send", ActionType.FINISH, event -> {
                if (getItem().getProductEntries().isEmpty()) {
                    Platform.runLater(() -> App.warn.showAndWait("Nothing to send."));
                } else if (App.apiIsBusy.compareAndSet(false, true)) {
                    App.api.postSalesOrder(getItem(), (AlertCallback<Long>) (aLong, response) -> {
                        App.main.backRefresh();
                        App.warn.showAndWait("Sales Order " + aLong);
                    });
                }
            }),

            new Action("Print", ActionType.FINISH, event -> Platform.runLater(() -> {
                if (getItem().canPrint()) {
                    App.main.backRefresh();
                    App.printer.printCheck(getItem());
                } else {
                    App.warn.showAndWait("Order must be sent before printing.");
                }
            })),

            new Action("Cancel", ActionType.FINISH, event -> Platform.runLater(App.main::backRefresh)),

            new Action("Order", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.orderEditorPresenter, getItem())
            )),

            new Action("Pay", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.paymentEditorPresenter, getItem())
            ))

    );

}
