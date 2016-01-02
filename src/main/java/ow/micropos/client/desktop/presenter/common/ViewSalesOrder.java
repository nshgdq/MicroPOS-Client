package ow.micropos.client.desktop.presenter.common;


import email.com.gmail.ttsai0509.javafx.LabelUtils;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;

public class ViewSalesOrder extends ItemPresenter<SalesOrder> {

    @FXML public GridPane gpTop;
    @FXML public Label lblSoId;
    @FXML public Label lblSoTotal;
    @FXML public Label lblSoTarget;
    @FXML public Label lblSoDetails;
    @FXML public Label lblSoStatus;
    @FXML public Label lblSoTime;
    @FXML public ListView<ProductEntry> lvSoSummary;

    public final void onTopClick(EventHandler<? super MouseEvent> handler) {
        gpTop.setOnMouseClicked(handler);
    }

    public final void onBottomClick(EventHandler<? super MouseEvent> handler) {
        lvSoSummary.setOnMouseClicked(handler);
    }

    public final void onAnyClick(EventHandler<? super MouseEvent> handler) {
        onTopClick(handler);
        onBottomClick(handler);
    }

    public final void onSubItemClick(OnProductEntryClick handler) {
        subHandler = handler;
    }

    public final void onClickDefaults() {
        onTopClick(event -> Platform.runLater(() -> {
            if (getItem() != null)
                App.main.setNextRefresh(App.paymentEditorPresenter, getItem());
        }));
        onBottomClick(event -> Platform.runLater(() -> {
            if (getItem() != null)
                App.main.setNextRefresh(App.orderEditorPresenter, getItem());
        }));
    }

    @Override
    public void initialize() {

        GridPane.setHalignment(lblSoTotal, HPos.RIGHT);
        GridPane.setHalignment(lblSoStatus, HPos.RIGHT);
        GridPane.setHalignment(lblSoTime, HPos.RIGHT);

        LabelUtils.fitToText(lblSoTotal);
        LabelUtils.fitToText(lblSoStatus);
        LabelUtils.fitToText(lblSoTime);

        lvSoSummary.setCellFactory(param -> {
            ViewProductEntry presenter = Presenter.load("/view/common/view_product_entry.fxml");
            presenter.fixWidth(lvSoSummary);
            presenter.onClick(event -> Platform.runLater(() -> onSubItemClick(presenter)));
            return new PresenterCellAdapter<>(presenter);
        });

    }

    @Override
    protected void updateItem(SalesOrder currentItem, SalesOrder newItem) {
        if (newItem == null) {
            unsetLabel(lblSoId);
            unsetLabel(lblSoTotal);
            unsetLabel(lblSoTarget);
            unsetLabel(lblSoDetails);
            unsetLabel(lblSoStatus);
            unsetLabel(lblSoTime);
            unsetListView(lvSoSummary);
        } else {
            setLabel(lblSoId, Bindings.concat("#", newItem.idProperty().asString()));
            setLabel(lblSoTotal, Bindings.concat("$", newItem.grandTotalProperty().asString()));
            setLabel(lblSoStatus, newItem.statusTextProperty());
            setLabel(lblSoTime, newItem.prettyTimeProperty());
            setListView(lvSoSummary, newItem.getProductEntries());

            if (newItem.hasType(SalesOrderType.TAKEOUT) && newItem.hasCustomer()) {
                setLabel(lblSoTarget, newItem.getCustomer().fullNameProperty());
                setLabel(lblSoDetails, newItem.getCustomer().phoneNumberProperty());
            } else if (newItem.hasType(SalesOrderType.DINEIN) && newItem.hasSeat()) {
                setLabel(lblSoTarget, Bindings.concat("Seat ", newItem.getSeat().tagProperty()));
                unsetLabel(lblSoDetails);
            }
        }
    }


    @Override
    public void dispose() {
        unsetLabel(lblSoId);
        unsetLabel(lblSoTotal);
        unsetLabel(lblSoTarget);
        unsetLabel(lblSoDetails);
        unsetLabel(lblSoStatus);
        unsetLabel(lblSoTime);
        unsetListView(lvSoSummary);
        onAnyClick(null);
        getView().setOnMouseClicked(null);
    }

    /******************************************************************
     *                                                                *
     * List View Item Click Handler
     *                                                                *
     ******************************************************************/

    public static interface OnProductEntryClick {
        public void onClick(ViewSalesOrder presenter, ViewProductEntry subPresenter);
    }

    private OnProductEntryClick subHandler;

    private void onSubItemClick(ViewProductEntry presenter) {
        if (subHandler != null)
            subHandler.onClick(this, presenter);
    }

}
