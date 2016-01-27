package ow.micropos.client.desktop.presenter.payment;

import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.binding.CentStringBinding;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.model.enums.*;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import ow.micropos.client.desktop.model.menu.Charge;
import ow.micropos.client.desktop.model.orders.ChargeEntry;
import ow.micropos.client.desktop.model.orders.PaymentEntry;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.presenter.order.ViewProductEntry;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.List;

public class PaymentEditorPresenter extends ItemPresenter<SalesOrder> {

    @FXML public Label time;
    @FXML public ListView<ProductEntry> orderEntries;
    @FXML public Label employee;
    @FXML public Label target;
    @FXML public Label info;
    @FXML public Label status;
    @FXML public Label productTotal;
    @FXML public Label chargeTotal;
    @FXML public Label gratuityTotal;
    @FXML public Label taxTotal;
    @FXML public Label grandTotal;

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

    @FXML public StackPane downOption;
    @FXML public StackPane upOption;

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

        StackPane.setAlignment(tfAmountDue, Pos.CENTER_LEFT);
        StackPane.setAlignment(tfAmountEntry, Pos.CENTER_RIGHT);

        upOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(orderEntries, -2))
        );

        downOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(orderEntries, 2))
        );

        cancelOption.setOnMouseClicked(
                event -> App.confirm.showAndWait("Are you sure? Any changes will not be saved.", App.main::backRefresh)
        );

        sendOption.setOnMouseClicked(event -> {
            if (getItem().getProductEntries().isEmpty()) {
                Platform.runLater(() -> App.notify.showAndWait("Nothing to send."));

            } else if (getItem().changeProperty().get().compareTo(BigDecimal.ZERO) < 0) {
                Platform.runLater(() -> App.notify.showAndWait("Payment Due : " + getItem().dueProperty().get()));

            } else {
                getItem().setStatus(SalesOrderStatus.REQUEST_CLOSE);
                App.apiProxy.postSalesOrder(getItem(), new RunLaterCallback<Long>() {
                    @Override
                    public void laterSuccess(Long aLong) {
                        getItem().setId(aLong);
                        App.main.setSwapRefresh(App.changeDuePresenter, getItem());
                        //App.main.backRefresh();
                        //App.notify.showAndWait("Change Due: " + getItem().changeProperty().get().toString());
                        App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem()));
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
                App.dispatcher.requestPrint("receipt", App.jobBuilder.check(getItem()));
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

        orderEntries.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/order/view_product_entry.fxml");
            presenter.fixWidth(orderEntries);
            return new PresenterCellAdapter<>(presenter);
        });

        lvChargeEntries.setCellFactory(param -> {
            ViewChargeEntry presenter = Presenter.load("/view/pay/view_charge_entry.fxml");
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
            ViewPaymentEntry presenter = Presenter.load("/view/pay/view_payment_entry.fxml");
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
            ViewCharge presenter = Presenter.load("/view/pay/view_charge.fxml");
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
            unsetListView(lvPaymentEntries);
            unsetLabel(tfAmountDue);
            disableKeypad();

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

    /*****************************************************************************
     *                                                                           *
     * Menu Buttons
     *                                                                           *
     *****************************************************************************/

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private final ObservableList<Action> menu = FXCollections.observableArrayList(
            new Action("Order", ActionType.TAB_DEFAULT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.orderEditorPresenter, getItem())
            )),

            new Action("Pay", ActionType.TAB_SELECT, event -> Platform.runLater(() ->
                            App.main.setSwapRefresh(App.paymentEditorPresenter, getItem())
            ))

    );

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
                        .changeProperty()
                        .get()
                        .negate()
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
