package ow.micropos.client.desktop.presenter.modify;

import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.model.enums.ModifierType;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.utils.Action;
import ow.micropos.client.desktop.utils.ActionType;
import ow.micropos.client.desktop.utils.AlertCallback;
import retrofit.client.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

public class ProductEditorPresenter extends ItemPresenter<ProductEntry> {

    @FXML Label lblStatus;
    @FXML Label lblMenuItem;
    @FXML Label lblUnitPrice;

    @FXML Label lblBasePrice;
    @FXML Label lblTotalPrice;

    @FXML RadioButton rbAll;
    @FXML RadioButton rbAdd;
    @FXML RadioButton rbExc;
    @FXML RadioButton rbSub;
    @FXML RadioButton rbIns;
    @FXML RadioButton rbVar;

    @FXML ListView<ModifierGroup> modifierGroups;
    @FXML GridView<Modifier> modifiers;
    @FXML ListView<Modifier> entryModifiers;

    private final ToggleGroup filterToggle = new ToggleGroup();
    private ObservableList<Modifier> source = FXCollections.emptyObservableList();
    private Predicate<Modifier> predicate = m -> true;

    @FXML
    public void initialize() {

        GridPane.setHalignment(lblMenuItem, HPos.CENTER);
        GridPane.setHalignment(lblUnitPrice, HPos.LEFT);
        GridPane.setHalignment(lblStatus, HPos.RIGHT);

        GridPane.setHalignment(lblBasePrice, HPos.RIGHT);
        GridPane.setHalignment(lblTotalPrice, HPos.RIGHT);

        rbAll.setToggleGroup(filterToggle);
        rbAdd.setToggleGroup(filterToggle);
        rbExc.setToggleGroup(filterToggle);
        rbSub.setToggleGroup(filterToggle);
        rbIns.setToggleGroup(filterToggle);
        rbVar.setToggleGroup(filterToggle);
        rbAll.setSelected(true);

        filterToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == rbAdd)
                    predicate = m -> m.getType() == ModifierType.ADDITION;
                else if (newValue == rbExc)
                    predicate = m -> m.getType() == ModifierType.EXCLUSION;
                else if (newValue == rbSub)
                    predicate = m -> m.getType() == ModifierType.SUBSTITUTION;
                else if (newValue == rbIns)
                    predicate = m -> m.getType() == ModifierType.INSTRUCTION;
                else if (newValue == rbVar)
                    predicate = m -> m.getType() == ModifierType.VARIATION;
                else
                    predicate = m -> true;

                modifiers.setItems(source.filtered(predicate));
            }
        });

        modifierGroups.setCellFactory(param -> PresenterCellAdapter.load("/view/modify/view_modifier_group.fxml"));
        modifierGroups.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                source = FXCollections.emptyObservableList();
            else
                source = newValue.modifiersProperty();

            modifiers.setItems(source.filtered(predicate));
        });

        modifiers.setRows(App.properties.getInt("modify-modifiers-rows"));
        modifiers.setCols(App.properties.getInt("modify-modifiers-cols"));
        modifiers.setCellFactory(param -> {
            ViewModifier presenter = Presenter.load("/view/modify/view_modifier.fxml");
            presenter.onClick(event -> Platform.runLater(() -> {
                entryModifiers.getItems().add(presenter.getItem());

                ProductEntry pe = getItem();
                if (pe.hasStatus(ProductEntryStatus.SENT))
                    pe.setStatus(ProductEntryStatus.REQUEST_EDIT);
            }));
            return presenter;
        });

        entryModifiers.setCellFactory(param -> PresenterCellAdapter.load("/view/modify/view_modifier_entry.fxml"));
        entryModifiers.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null)
                Platform.runLater(() -> {
                    entryModifiers.getSelectionModel().clearSelection();
                    entryModifiers.getItems().remove(newValue);

                    ProductEntry pe = getItem();
                    if (pe.hasStatus(ProductEntryStatus.SENT))
                        pe.setStatus(ProductEntryStatus.REQUEST_EDIT);
                });
        });

    }

    @Override
    protected void updateItem(ProductEntry currentItem, ProductEntry newItem) {
        if (newItem == null) {
            unsetListView(entryModifiers);
            unsetLabel(lblStatus);
            unsetLabel(lblUnitPrice);
            unsetLabel(lblBasePrice);
            unsetLabel(lblTotalPrice);
            unsetLabel(lblMenuItem);
        } else {
            setLabel(lblStatus, new StringBinding() {
                {bind(newItem.statusProperty());}

                @Override
                protected String computeValue() {
                    switch (newItem.getStatus()) {
                        case SENT:
                            return "Sent";
                        case HOLD:
                            return "Held";
                        case VOID:
                            return "Void";
                        case REQUEST_SENT:
                            return "Sending";
                        case REQUEST_EDIT:
                            return "Editing";
                        case REQUEST_HOLD:
                            return "Holding";
                        case REQUEST_VOID:
                            return "Voiding";
                        default:
                            return "Error";
                    }
                }
            });
            setLabel(lblBasePrice, EasyBind.combine(
                            newItem.getMenuItem().priceProperty(),
                            newItem.modifierTotalProperty(),
                            (bd1, bd2) -> BigDecimalUtils.asDollars(bd1.add(bd2).max(BigDecimal.ZERO)).toString())
            );
            setLabel(lblMenuItem, Bindings.concat(
                    newItem.quantityProperty(),
                    " - ",
                    newItem.getMenuItem().tagProperty(),
                    " ",
                    newItem.getMenuItem().nameProperty()
            ));
            setLabel(lblUnitPrice, Bindings.concat(
                    "Item Price : ",
                    newItem.getMenuItem().priceProperty().asString()
            ));
            setLabel(lblTotalPrice, newItem.totalProperty().asString());
            setListView(entryModifiers, newItem.modifiersProperty());
        }
    }

    @Override
    public void refresh() {
        if (App.apiIsBusy.compareAndSet(false, true)) {
            App.api.getModifierGroups(new AlertCallback<List<ModifierGroup>>() {
                @Override
                public void onSuccess(List<ModifierGroup> mgList, Response response) {
                    setListView(modifierGroups, FXCollections.observableList(mgList));
                }
            });
        }
    }

    @Override
    public void dispose() {
        unsetListView(entryModifiers);
        unsetListView(modifierGroups);
        unsetLabel(lblStatus);
        unsetLabel(lblUnitPrice);
        unsetLabel(lblBasePrice);
        unsetLabel(lblTotalPrice);
        unsetLabel(lblMenuItem);
        setItem(null);
    }

    /*****************************************************************************
     *                                                                           *
     * Menu Buttons
     *                                                                           *
     *****************************************************************************/

    private ObservableList<ProductEntry> context;

    public final void setContextList(ObservableList<ProductEntry> context) {
        this.context = context;
    }

    @Override
    public ObservableList<Action> menu() {
        if (getItem().hasStatus(ProductEntryStatus.REQUEST_HOLD) ||
                getItem().hasStatus(ProductEntryStatus.HOLD))
            return menuHold;
        else
            return menuSend;
    }

    private final ObservableList<Action> menuHold = FXCollections.observableArrayList(

            new Action("Done", ActionType.FINISH, event -> Platform.runLater(App.main::back)),
            new Action("Void", ActionType.FINISH, event -> Platform.runLater(() -> {
                if (getItem().hasStatus(ProductEntryStatus.REQUEST_SENT)) {
                    context.remove(getItem());
                    App.main.back();

                } else if (!App.employee.hasPermission(Permission.VOID_SALES_ORDER)) {
                    App.notify.showAndWait("Requires Permissions : [VOID_SALES_ORDER]");

                } else {
                    getItem().setStatus(ProductEntryStatus.REQUEST_VOID);
                    App.main.back();
                }
            })),
            new Action("Send", ActionType.FINISH, event -> Platform.runLater(() -> {
                if (getItem().hasStatus(ProductEntryStatus.REQUEST_HOLD) ||
                        getItem().hasStatus(ProductEntryStatus.HOLD)) {
                    getItem().setStatus(ProductEntryStatus.REQUEST_SENT);
                    App.main.back();

                } else {
                    App.notify.showAndWait("Item can not be held anymore.");
                }
            }))

    );


    private final ObservableList<Action> menuSend = FXCollections.observableArrayList(

            new Action("Done", ActionType.FINISH, event -> Platform.runLater(App.main::back)),
            new Action("Void", ActionType.FINISH, event -> Platform.runLater(() -> {
                if (getItem().hasStatus(ProductEntryStatus.REQUEST_SENT)) {
                    context.remove(getItem());
                    App.main.back();

                } else if (!App.employee.hasPermission(Permission.VOID_SALES_ORDER)) {
                    App.notify.showAndWait("Requires Permissions : [VOID_SALES_ORDER]");

                } else {
                    getItem().setStatus(ProductEntryStatus.REQUEST_VOID);
                    App.main.back();
                }
            })),
            new Action("Hold", ActionType.FINISH, event -> Platform.runLater(() -> {
                if (getItem().hasStatus(ProductEntryStatus.REQUEST_SENT)) {
                    getItem().setStatus(ProductEntryStatus.REQUEST_HOLD);
                    App.main.back();

                } else if (getItem().hasStatus(ProductEntryStatus.REQUEST_HOLD) ||
                        getItem().hasStatus(ProductEntryStatus.HOLD)) {
                    App.main.back();

                } else {
                    App.notify.showAndWait("Item can not be held anymore.");
                }
            }))

    );


}
