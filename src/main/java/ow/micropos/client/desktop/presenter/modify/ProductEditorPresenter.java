package ow.micropos.client.desktop.presenter.modify;

import email.com.gmail.ttsai0509.javafx.ListViewUtils;
import email.com.gmail.ttsai0509.javafx.control.GridView;
import email.com.gmail.ttsai0509.javafx.control.PresenterCellAdapter;
import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.fxmisc.easybind.EasyBind;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.common.Action;
import ow.micropos.client.desktop.common.ActionType;
import ow.micropos.client.desktop.model.enums.ModifierType;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.service.ComparatorUtils;
import ow.micropos.client.desktop.service.RunLaterCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ProductEditorPresenter extends ItemPresenter<ProductEntry> {

    @FXML public Label lblStatus;
    @FXML public Label lblMenuItem;
    @FXML public Label lblUnitPrice;
    @FXML public Label lblBasePrice;
    @FXML public Label lblTotalPrice;

    @FXML public StackPane modGroupOption;
    @FXML public StackPane subOption;
    @FXML public StackPane addOption;
    @FXML public StackPane voidOption;
    @FXML public StackPane holdOption;
    @FXML public StackPane doneOption;
    @FXML public StackPane spItems;
    @FXML public Label holdOptionLabel;

    @FXML public StackPane upOption;
    @FXML public StackPane downOption;

    @FXML public StackPane backOption;
    @FXML public StackPane nextOption;

    @FXML public Label voidOptionLabel;
    @FXML public GridView<ModifierGroup> gpModifierGroups;
    @FXML public GridView<Modifier> gpModifiers;
    @FXML public ListView<Modifier> entryModifiers;

    BigDecimal originalQty;

    @FXML
    public void initialize() {

        GridPane.setHalignment(lblMenuItem, HPos.CENTER);
        GridPane.setHalignment(lblUnitPrice, HPos.LEFT);
        GridPane.setHalignment(lblStatus, HPos.RIGHT);

        GridPane.setHalignment(lblBasePrice, HPos.RIGHT);
        GridPane.setHalignment(lblTotalPrice, HPos.RIGHT);

        modGroupOption.setOnMouseClicked(event -> Platform.runLater(this::showModifierGroups));

        upOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(entryModifiers, -2))
        );

        downOption.setOnMouseClicked(
                event -> Platform.runLater(() -> ListViewUtils.listViewScrollBy(entryModifiers, 2))
        );

        backOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (spItems.getChildren().contains(gpModifierGroups)) {
                gpModifierGroups.prevPage();
            } else if (spItems.getChildren().contains(gpModifiers)) {
                gpModifiers.prevPage();
            }
        }));

        nextOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (spItems.getChildren().contains(gpModifierGroups)) {
                gpModifierGroups.nextPage();
            } else if (spItems.getChildren().contains(gpModifiers)) {
                gpModifiers.nextPage();
            }
        }));


        subOption.setOnMouseClicked(event -> {
            ProductEntry pe = getItem();
            if (pe.hasStatus(ProductEntryStatus.REQUEST_SENT)
                    || App.employee.hasPermission(Permission.VOID_PRODUCT_ENTRY)
                    || pe.getQuantity().compareTo(originalQty) > 0) {

                if (pe.getQuantity().compareTo(BigDecimal.ONE) > 0) {
                    pe.setQuantity(pe.getQuantity().subtract(BigDecimal.ONE));
                    if (pe.hasStatus(ProductEntryStatus.SENT))
                        pe.setStatus(ProductEntryStatus.REQUEST_EDIT);
                }

            } else {

                App.notify.showAndWait(Permission.VOID_PRODUCT_ENTRY);

            }
        });

        addOption.setOnMouseClicked(event -> {
            ProductEntry pe = getItem();
            pe.setQuantity(pe.getQuantity().add(BigDecimal.ONE));
            if (pe.hasStatus(ProductEntryStatus.SENT))
                pe.setStatus(ProductEntryStatus.REQUEST_EDIT);
        });

        voidOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (getItem().hasStatus(ProductEntryStatus.REQUEST_SENT) || getItem().hasStatus(ProductEntryStatus.REQUEST_HOLD)) {
                App.confirm.showAndWait("Are you sure you want to remove this item? This can not be undone.", () -> {
                    context.remove(getItem());
                    App.main.backRefresh();
                });

            } else if (!App.employee.hasPermission(Permission.VOID_PRODUCT_ENTRY)) {
                App.notify.showAndWait(Permission.VOID_PRODUCT_ENTRY);

            } else if (getItem().hasStatus(ProductEntryStatus.REQUEST_SENT)) {
                App.notify.showAndWait("Item has not been sent yet. Please REMOVE instead.");

            } else if (getItem().hasStatus(ProductEntryStatus.HOLD)) {
                App.confirm.showAndWait("Are you sure you want to void this item? This can not be undone.", () -> {
                    getItem().setStatus(ProductEntryStatus.REQUEST_HOLD_VOID);
                    App.main.backRefresh();
                });

            } else {
                App.confirm.showAndWait("Are you sure you want to void this item? This can not be undone.", () -> {
                    getItem().setStatus(ProductEntryStatus.REQUEST_VOID);
                    App.main.backRefresh();
                });
            }
        }));

        holdOption.setOnMouseClicked(event -> Platform.runLater(() -> {
            if (!App.employee.hasPermission(Permission.HOLD_PRODUCT_ENTRY)) {
                App.notify.showAndWait(Permission.HOLD_PRODUCT_ENTRY);

            } else if (getItem().hasStatus(ProductEntryStatus.REQUEST_SENT)) {
                getItem().setStatus(ProductEntryStatus.REQUEST_HOLD);
                App.main.backRefresh();

            } else if (getItem().hasStatus(ProductEntryStatus.REQUEST_HOLD) ||
                    getItem().hasStatus(ProductEntryStatus.HOLD)) {
                getItem().setStatus(ProductEntryStatus.REQUEST_SENT);
                App.main.backRefresh();

            } else {
                App.notify.showAndWait("Item can not be held anymore.");
            }
        }));

        doneOption.setOnMouseClicked(event -> Platform.runLater(App.main::backRefresh));

        gpModifiers.setPage(0);
        gpModifiers.setRows(App.properties.getInt("modify-mod-rows"));
        gpModifiers.setCols(App.properties.getInt("modify-mod-cols"));
        gpModifiers.setHorizontal(true);
        gpModifiers.setCellFactory(param -> {
            ViewModifier presenter = ItemPresenter.load("/view/modify/view_modifier.fxml");
            presenter.onClick(event -> Platform.runLater(() -> {
                getItem().getModifiers().add(presenter.getItem());
                int lastIdx = getItem().getModifiers().size() - 1;
                entryModifiers.scrollTo(lastIdx);

                ProductEntry pe = getItem();
                if (pe.hasStatus(ProductEntryStatus.SENT))
                    pe.setStatus(ProductEntryStatus.REQUEST_EDIT);
            }));
            return presenter;
        });

        gpModifierGroups.setPage(0);
        gpModifierGroups.setRows(App.properties.getInt("modify-mod-group-rows"));
        gpModifierGroups.setCols(App.properties.getInt("modify-mod-group-cols"));
        gpModifierGroups.setHorizontal(true);
        gpModifierGroups.setCellFactory(param -> {
            ViewModifierGroup presenter = ItemPresenter.load("/view/modify/view_modifier_group.fxml");
            presenter.onClick(event -> Platform.runLater(() -> showModifiers(presenter.getItem())));
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

        updateMenu(null);
        showModifierGroups();
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

            if (newItem.hasStatus(ProductEntryStatus.HOLD) || newItem.hasStatus(ProductEntryStatus.REQUEST_HOLD))
                holdOptionLabel.setText("Send");
            else
                holdOptionLabel.setText("Hold");

            if (newItem.hasStatus(ProductEntryStatus.REQUEST_SENT) || newItem.hasStatus(ProductEntryStatus.REQUEST_HOLD))
                voidOptionLabel.setText("Del");
            else
                voidOptionLabel.setText("Void");

            originalQty = newItem.getQuantity();

        }
    }

    @Override
    public void refresh() {
        gpModifierGroups.setItems(FXCollections.emptyObservableList());
        resetModifiers();
        App.apiProxy.getModifierGroups(new RunLaterCallback<List<ModifierGroup>>() {
            @Override
            public void laterSuccess(List<ModifierGroup> mgList) {
                mgList.sort((o1, o2) -> {
                    if (o1.getWeight() != o2.getWeight())
                        return o1.getWeight() - o2.getWeight();
                    else
                        return ComparatorUtils.tagComparator.compare(o1.getTag(), o2.getTag());
                });
                mgList.forEach(mg -> mg.getModifiers().sort((o1, o2) -> {
                    if (o1.getWeight() != o2.getWeight())
                        return o1.getWeight() - o2.getWeight();
                    else
                        return ComparatorUtils.tagComparator.compare(o1.getTag(), o2.getTag());
                }));
                gpModifierGroups.setItems(FXCollections.observableList(mgList));
                showModifierGroups();
            }
        });
    }

    @Override
    public void dispose() {
        unsetListView(entryModifiers);
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

    private final ObservableList<Action> menu = FXCollections.observableArrayList(new ArrayList<>());

    private final Action allTabDefault = new Action("All", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
        updateModifiers(m -> true);
        updateMenu(null);
    }));
    private final Action allTabSelect = new Action("All", ActionType.TAB_SELECT, event -> Platform.runLater(() -> {
        updateModifiers(m -> true);
        updateMenu(null);
    }));
    private final Action addTabDefault = new Action("+", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.ADDITION));
        updateMenu(ModifierType.ADDITION);
    }));
    private final Action addTabSelect = new Action("+", ActionType.TAB_SELECT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.ADDITION));
        updateMenu(ModifierType.ADDITION);
    }));
    private final Action excTabDefault = new Action("-", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.EXCLUSION));
        updateMenu(ModifierType.EXCLUSION);
    }));
    private final Action excTabSelect = new Action("-", ActionType.TAB_SELECT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.EXCLUSION));
        updateMenu(ModifierType.EXCLUSION);
    }));
    private final Action subTabDefault = new Action("Sub", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.SUBSTITUTION));
        updateMenu(ModifierType.SUBSTITUTION);
    }));
    private final Action subTabSelect = new Action("Sub", ActionType.TAB_SELECT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.SUBSTITUTION));
        updateMenu(ModifierType.SUBSTITUTION);
    }));
    private final Action insTabDefault = new Action("Ins", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.INSTRUCTION));
        updateMenu(ModifierType.INSTRUCTION);
    }));
    private final Action insTabSelect = new Action("Ins", ActionType.TAB_SELECT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.INSTRUCTION));
        updateMenu(ModifierType.INSTRUCTION);
    }));
    private final Action varTabDefault = new Action("Var", ActionType.TAB_DEFAULT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.VARIATION));
        updateMenu(ModifierType.VARIATION);
    }));
    private final Action varTabSelect = new Action("Var", ActionType.TAB_SELECT, event -> Platform.runLater(() -> {
        updateModifiers(m -> m.hasType(ModifierType.VARIATION));
        updateMenu(ModifierType.VARIATION);
    }));

    @Override
    public ObservableList<Action> menu() {
        return menu;
    }

    private void updateMenu(ModifierType type) {
        if (type == null)
            menu.setAll(allTabSelect, addTabDefault, excTabDefault, subTabDefault, insTabDefault, varTabDefault);
        else
            switch (type) {
                case ADDITION:
                    menu.setAll(allTabDefault, addTabSelect, excTabDefault, subTabDefault, insTabDefault, varTabDefault);
                    break;
                case SUBSTITUTION:
                    menu.setAll(allTabDefault, addTabDefault, excTabDefault, subTabSelect, insTabDefault, varTabDefault);
                    break;
                case EXCLUSION:
                    menu.setAll(allTabDefault, addTabDefault, excTabSelect, subTabDefault, insTabDefault, varTabDefault);
                    break;
                case INSTRUCTION:
                    menu.setAll(allTabDefault, addTabDefault, excTabDefault, subTabDefault, insTabSelect, varTabDefault);
                    break;
                case VARIATION:
                    menu.setAll(allTabDefault, addTabDefault, excTabDefault, subTabDefault, insTabDefault, varTabSelect);
                    break;
            }
    }

    private void showModifierGroups() {
        resetModifiers();
        spItems.getChildren().setAll(gpModifierGroups);
    }

    private void showModifiers(ModifierGroup group) {
        updateModifiers(group.getModifiers());
        spItems.getChildren().setAll(gpModifiers);
    }

    /******************************************************************
     *                                                                *
     * Modifiers
     *                                                                *
     ******************************************************************/

    private ObservableList<Modifier> source = FXCollections.emptyObservableList();
    private Predicate<Modifier> predicate = m -> true;

    private void updateModifiers(Predicate<Modifier> predicate) {
        this.predicate = predicate;
        gpModifiers.setItems(source.filtered(predicate));
    }

    private void updateModifiers(ObservableList<Modifier> source) {
        this.source = source;
        gpModifiers.setItems(source.filtered(predicate));
    }

    private void resetModifiers() {
        this.source = FXCollections.emptyObservableList();
        this.predicate = m -> true;
        updateMenu(null);
        gpModifiers.setItems(source.filtered(predicate));
    }

    /******************************************************************
     *                                                                *
     *
     *                                                                *
     ******************************************************************/

    private ObservableList<ProductEntry> context;

    public final void setContextList(ObservableList<ProductEntry> context) {
        this.context = context;
    }

}
