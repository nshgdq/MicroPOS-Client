package ow.micropos.client.desktop.presenter.order;

import email.com.gmail.ttsai0509.javafx.binding.CentStringBinding;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.*;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import ow.micropos.client.desktop.model.menu.Charge;
import ow.micropos.client.desktop.model.orders.ChargeEntry;
import ow.micropos.client.desktop.model.orders.PaymentEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.List;

public class PaymentPresenter extends ItemPresenter<SalesOrder> {

    @FXML public ListView<Charge> lvCharges;

    @FXML public Label tfAmountDue;
    @FXML public Label tfAmountEntry;
    @FXML public StackPane btn7;
    @FXML public StackPane btn8;
    @FXML public StackPane btn9;
    @FXML public StackPane btn4;
    @FXML public StackPane btn5;
    @FXML public StackPane btn6;
    @FXML public StackPane btn1;
    @FXML public StackPane btn2;
    @FXML public StackPane btn3;
    @FXML public StackPane btn0;
    @FXML public StackPane btn00;
    @FXML public StackPane btnEvenTender;
    @FXML public StackPane btnClear;
    @FXML public StackPane btnCash;
    @FXML public StackPane btnCredit;
    @FXML public StackPane btnCheck;
    @FXML public StackPane btnGiftCard;

    @FXML public StackPane gratuityOption;
    @FXML public StackPane printOption;
    @FXML public StackPane cancelOption;
    @FXML public StackPane sendOption;

    @FXML public StackPane voidOption;
    @FXML public ListView<ChargeEntry> lvChargeEntries;
    @FXML public ListView<PaymentEntry> lvPaymentEntries;
    @FXML public Label voidText;

    private StringProperty rawAmount = new SimpleStringProperty("");
    private CentStringBinding amountFormatter = new CentStringBinding(rawAmount);

    @FXML
    public void initialize() {

        StackPane.setAlignment(tfAmountDue, Pos.CENTER_LEFT);
        StackPane.setAlignment(tfAmountEntry, Pos.CENTER_RIGHT);

        cancelOption.setOnMouseClicked(
                event -> App.confirm.showAndWait("Are you sure? Any changes will not be saved.", App.main::backRefresh)
        );

        sendOption.setOnMouseClicked(event -> {
            if (getItem().getProductEntries().isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("Nothing to send."));

            } else if (getItem().changeProperty().get().compareTo(BigDecimal.ZERO) < 0) {
                Platform.runLater(() -> App.notify.showAndWait("Payment Due : " + getItem().dueProperty().get()));

            } else if (getItem().getProductEntries().stream().anyMatch(pe -> pe.hasStatus(ProductEntryStatus.HOLD) || pe.hasStatus(ProductEntryStatus.REQUEST_HOLD))) {
                Platform.runLater(() -> App.notify.showAndWait("Can't close orders with held items."));

            } else {
                SalesOrderStatus prevStatus = getItem().getStatus();
                getItem().setStatus(SalesOrderStatus.REQUEST_CLOSE);
                App.apiProxy.postSalesOrder(getItem(), new RunLaterCallback<Long>() {
                    @Override
                    public void laterSuccess(Long aLong) {
                        getItem().setId(aLong);
                        App.main.setSwapRefresh(App.changeDuePresenter, getItem());

                        // Print takeout receipt on pay
                        if (App.properties.getBool("print-pay-takeout") && getItem().hasType(SalesOrderType.TAKEOUT))
                            App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), true));

                        // Print dinein receipt on pay
                        else if (App.properties.getBool("print-pay-dinein") && getItem().hasType(SalesOrderType.DINEIN))
                            App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), true));

                        // TODO : Remove hardcoded scenario - print receipt for take out orders that pay immediately
                        else if (getItem().hasType(SalesOrderType.TAKEOUT)
                                && getItem().hasStatus(SalesOrderStatus.REQUEST_CLOSE)
                                && prevStatus == SalesOrderStatus.REQUEST_OPEN)
                            App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), true));

                    }

                    @Override
                    public void laterFailure(ErrorInfo error) {
                        super.laterFailure(error);
                        getItem().setStatus(prevStatus);
                    }
                });
            }
        });

        gratuityOption.setOnMouseClicked(event -> {
            if (getItem().canHaveGratuity() && !getItem().hasGratuity()) {
                getItem().setGratuityPercent(App.properties.getBd("gratuity-percent"));
            } else {
                getItem().setGratuityPercent(BigDecimal.ZERO);
            }
        });

        printOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (getItem().canPrint()) {
                App.main.backRefresh();
                App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem(), false));
            } else {
                App.notify.showAndWait("Changes must be sent before printing.");
            }
        }));

        voidOption.setOnMouseClicked(event -> {
            if (getItem().hasStatus(SalesOrderStatus.VOID) || getItem().hasStatus(SalesOrderStatus.CLOSED)) {
                App.confirm.showAndWait("Reopen Sales Order?", () -> {
                    SalesOrderStatus prevStatus = getItem().getStatus();
                    getItem().setStatus(SalesOrderStatus.REQUEST_OPEN);

                    App.apiProxy.postSalesOrder(getItem(), new RunLaterCallback<Long>() {
                        @Override
                        public void laterSuccess(Long aLong) {
                            App.notify.showAndWait("Sales Order " + aLong + " Reopened.");
                            App.main.backRefresh();
                        }

                        @Override
                        public void laterFailure(ErrorInfo error) {
                            super.laterFailure(error);
                            getItem().setStatus(prevStatus);
                        }
                    });
                });
            } else {
                App.confirm.showAndWait("Void Sales Order?", () -> {
                    SalesOrderStatus prevStatus = getItem().getStatus();
                    getItem().setStatus(SalesOrderStatus.REQUEST_VOID);

                    App.apiProxy.postSalesOrder(getItem(), new RunLaterCallback<Long>() {
                        @Override
                        public void laterSuccess(Long aLong) {
                            Platform.runLater(() -> {
                                App.main.backRefresh();
                                App.notify.showAndWait("Sales Order " + aLong + " Voided.");
                            });
                        }

                        @Override
                        public void laterFailure(ErrorInfo error) {
                            super.laterFailure(error);
                            getItem().setStatus(prevStatus);
                        }
                    });
                });
            }
        });

        lvChargeEntries.setCellFactory(param -> {
            ViewChargeEntry presenter = Presenter.load("/view/order/view_charge_entry.fxml");
            presenter.fixWidth(lvChargeEntries);
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
            ViewPaymentEntry presenter = Presenter.load("/view/order/view_payment_entry.fxml");
            presenter.fixWidth(lvPaymentEntries);
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
            ViewCharge presenter = Presenter.load("/view/order/view_charge.fxml");
            presenter.fixWidth(lvCharges);
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
        App.apiProxy.getCharges(new RunLaterCallback<List<Charge>>() {
            @Override
            public void laterSuccess(List<Charge> charges) {
                charges.sort((o1, o2) -> o1.getWeight() - o2.getWeight());
                lvCharges.setItems(FXCollections.observableList(charges));
            }
        });
    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {

        if (newItem == null) {
            unsetListView(lvChargeEntries);
            unsetListView(lvPaymentEntries);
            unsetLabel(tfAmountDue);
            disableKeypad();

        } else {
            setListView(lvChargeEntries, newItem.chargeEntriesProperty());
            setListView(lvPaymentEntries, newItem.paymentEntriesProperty());
            setLabel(tfAmountDue, Bindings.concat("Payment Due : ", newItem.dueProperty().asString()));

            if (newItem.hasStatus(SalesOrderStatus.CLOSED) || newItem.hasStatus(SalesOrderStatus.VOID))
                voidText.setText("Reopen");
            else
                voidText.setText("Void");

            enableKeypad();
        }
    }

    @Override
    public void dispose() {
        disableKeypad();
        unsetListView(lvPaymentEntries);
        //unsetLabel(lblChangeAmount);
        //unsetLabel(lblPaidAmount);
    }

    private void addPaymentEntry(PaymentEntryType type) {

        String strAmount = amountFormatter.get().isEmpty() ? "0.00" : amountFormatter.get();
        BigDecimal amount = BigDecimalUtils.asDollars(new BigDecimal(strAmount));

        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            PaymentEntry payment = new PaymentEntry(amount, type);
            getItem().paymentEntriesProperty().add(payment);
            rawAmount.set("");
        } else {
            App.notify.showAndWait("You must enter a payment amount.");
        }
    }

    private void enableKeypad() {
        setTextProperty(tfAmountEntry.textProperty(), amountFormatter);
        btnClear.setOnMouseClicked(event -> rawAmount.set(""));
        btn00.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "00"));
        btn0.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "0"));
        btn1.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "1"));
        btn2.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "2"));
        btn3.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "3"));
        btn4.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "4"));
        btn5.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "5"));
        btn6.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "6"));
        btn7.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "7"));
        btn8.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "8"));
        btn9.setOnMouseClicked(event -> rawAmount.set(rawAmount.get() + "9"));
        btnCash.setOnMouseClicked(event -> addPaymentEntry(PaymentEntryType.CASH));
        btnCredit.setOnMouseClicked(event -> addPaymentEntry(PaymentEntryType.CREDIT));
        btnCheck.setOnMouseClicked(event -> addPaymentEntry(PaymentEntryType.CHECK));
        btnGiftCard.setOnMouseClicked(event -> addPaymentEntry(PaymentEntryType.GIFTCARD));
        btnEvenTender.setOnMouseClicked(event -> rawAmount.set(getItem()
                        .dueProperty()
                        .get()
                        .toString()
                        .replace(".", "")
        ));
    }

    private void disableKeypad() {
        unsetTextProperty(tfAmountEntry.textProperty(), "Disabled");
        btnEvenTender.setOnMouseClicked(null);
        btnClear.setOnMouseClicked(null);
        btn00.setOnMouseClicked(null);
        btn0.setOnMouseClicked(null);
        btn1.setOnMouseClicked(null);
        btn2.setOnMouseClicked(null);
        btn3.setOnMouseClicked(null);
        btn4.setOnMouseClicked(null);
        btn5.setOnMouseClicked(null);
        btn6.setOnMouseClicked(null);
        btn7.setOnMouseClicked(null);
        btn8.setOnMouseClicked(null);
        btn9.setOnMouseClicked(null);
        btnCash.setOnMouseClicked(null);
        btnCredit.setOnMouseClicked(null);
        btnCheck.setOnMouseClicked(null);
        btnGiftCard.setOnMouseClicked(null);
    }

}
