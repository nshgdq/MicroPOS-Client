package ow.micropos.client.desktop.presenter.common;


import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;

public class ViewSalesOrder extends ItemPresenter<SalesOrder> {

    @FXML GridPane gpTop;
    @FXML GridPane gpBottom;
    @FXML ListView<ProductEntry> lvOrderSummary;
    @FXML Label lblDate;
    @FXML Label lblEmployee;
    @FXML Label lblId;
    @FXML Label lblTarget;
    @FXML Label lblTargetDetails;
    @FXML Label lblStatus;
    @FXML Label lblTotal;
    @FXML StackPane spTotalWrapper;

    public final void onTopClick(EventHandler<? super MouseEvent> handler) {
        gpTop.setOnMouseClicked(handler);
    }

    public final void onBottomClick(EventHandler<? super MouseEvent> handler) {
        gpBottom.setOnMouseClicked(handler);
    }

    public final void onSummaryClick(EventHandler<? super MouseEvent> handler) {
        lvOrderSummary.setOnMouseClicked(handler);
    }

    public final void onTotalClick(EventHandler<? super MouseEvent> handler) {
        spTotalWrapper.setOnMouseClicked(handler);
    }

    public final void onAnyClick(EventHandler<? super MouseEvent> handler) {
        onTopClick(handler);
        onSummaryClick(handler);
        onBottomClick(handler);
    }

    public final void onClickDefaults() {
        lvOrderSummary.setOnMouseClicked(event -> Platform.runLater(() ->
                        App.main.setNextRefresh(App.orderEditorPresenter, getItem())
        ));

        gpTop.setOnMouseClicked(event -> Platform.runLater(() ->
                        App.main.setNextRefresh(App.orderEditorPresenter, getItem())
        ));

        gpBottom.setOnMouseClicked(event -> Platform.runLater(() ->
                        App.main.setNextRefresh(App.paymentEditorPresenter, getItem())
        ));
    }

    public final void onSubItemClick(OnProductEntryClick handler) {
        subHandler = handler;
    }

    public static interface OnProductEntryClick {
        public void onClick(ViewSalesOrder presenter, ViewProductEntry subPresenter);
    }

    private OnProductEntryClick subHandler;

    private void onSubItemClick(ViewProductEntry presenter) {
        if (subHandler != null)
            subHandler.onClick(this, presenter);
    }

    @Override
    public void initialize() {

        lvOrderSummary.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/common/view_product_entry.fxml");
            presenter.fixWidth(lvOrderSummary);
            presenter.onClick(event -> Platform.runLater(() -> onSubItemClick(presenter)));
            return new PresenterCellAdapter<>(presenter);
        });

    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {
        if (newItem == null) {
            unsetLabel(lblId);
            unsetLabel(lblTarget);
            unsetLabel(lblTargetDetails);
            unsetLabel(lblStatus);
            unsetLabel(lblTotal);
            unsetListView(lvOrderSummary);
            unsetLabel(lblDate);
        } else {
            setLabel(lblId, new StringBinding() {
                {bind(newItem.idProperty());}

                @Override
                protected String computeValue() {
                    Long id = newItem.idProperty().get();
                    return (id == null) ? "-" : Long.toString(id);
                }

                @Override
                public void dispose() {
                    unbind(newItem.idProperty());
                }
            });
            setLabel(lblStatus, Bindings.concat("Status : ", newItem.statusTextProperty()));
            setLabel(lblTotal, Bindings.concat("$", newItem.grandTotalProperty().asString()));
            setLabel(lblEmployee, Bindings.concat("Employee : ", newItem.employeeNameProperty()));
            setLabel(lblDate, newItem.prettyTimeProperty());
            setListView(lvOrderSummary, newItem.getProductEntries());

            if (newItem.hasType(SalesOrderType.TAKEOUT) && newItem.hasCustomer()) {
                setLabel(lblTarget, newItem.getCustomer().fullNameProperty());
                setLabel(lblTargetDetails, newItem.getCustomer().phoneNumberProperty());
            } else if (newItem.hasType(SalesOrderType.DINEIN) && newItem.hasSeat()) {
                setLabel(lblTarget, Bindings.concat("Seat ", newItem.getSeat().tagProperty()));
                unsetLabel(lblTargetDetails);
            }
        }
    }


    @Override
    public void dispose() {
        unsetLabel(lblId);
        unsetLabel(lblTarget);
        unsetLabel(lblStatus);
        unsetLabel(lblTotal);
        unsetLabel(lblTargetDetails);
        unsetListView(lvOrderSummary);
        getView().setOnMouseClicked(null);
        gpTop.setOnMouseClicked(null);
        gpBottom.setOnMouseClicked(null);
        lvOrderSummary.setOnMouseClicked(null);
    }

}
