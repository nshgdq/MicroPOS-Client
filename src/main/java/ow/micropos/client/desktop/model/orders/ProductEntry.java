package ow.micropos.client.desktop.model.orders;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.collections4.CollectionUtils;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.menu.Modifier;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProductEntry {

    public ProductEntry() {}

    public ProductEntry(ProductEntry productEntry) {
        setStatus(productEntry.getStatus());
        setQuantity(productEntry.getQuantity());
        setMenuItem(productEntry.getMenuItem());
        setModifiers(productEntry.getModifiers());
        setDate(productEntry.getDate());
    }

    public ProductEntry(MenuItem menuItem, BigDecimal quantity) {
        setMenuItem(menuItem);
        setQuantity(quantity);
        setStatus(ProductEntryStatus.REQUEST_SENT);
        setDate(new Date());
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private ObjectProperty<Date> date = new SimpleObjectProperty<>();

    private ObjectProperty<ProductEntryStatus> status = new SimpleObjectProperty<>();

    private ObjectProperty<BigDecimal> quantity = new SimpleObjectProperty<>();

    private ObjectProperty<MenuItem> menuItem = new SimpleObjectProperty<>();

    private ListProperty<Modifier> modifiers = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ObjectProperty<SalesOrder> salesOrder = new SimpleObjectProperty<>();

    public Long getId() {
        return id.get();
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public Date getDate() {
        return date.get();
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public ProductEntryStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<ProductEntryStatus> statusProperty() {
        return status;
    }

    public void setStatus(ProductEntryStatus status) {
        this.status.set(status);
    }

    public boolean hasStatus(ProductEntryStatus status) {
        return this.status.get() == status;
    }

    public BigDecimal getQuantity() {
        return quantity.get();
    }

    public ObjectProperty<BigDecimal> quantityProperty() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity.set(quantity);
    }

    public MenuItem getMenuItem() {
        return menuItem.get();
    }

    public ObjectProperty<MenuItem> menuItemProperty() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem.set(menuItem);
    }

    public ObservableList<Modifier> getModifiers() {
        return modifiers.get();
    }

    public ListProperty<Modifier> modifiersProperty() {
        return modifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers.setAll(modifiers);
    }

    public SalesOrder getSalesOrder() {
        return salesOrder.get();
    }

    public ObjectProperty<SalesOrder> salesOrderProperty() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder.set(salesOrder);
    }

    public boolean canMerge(ProductEntry productEntry) {
        if (productEntry == null)
            return false;
        if (!Objects.equals(getMenuItem().getId(), productEntry.getMenuItem().getId()))
            return false;
        if (!Objects.equals(getModifiers().size(), productEntry.getModifiers().size()))
            return false;
        if (!Objects.equals(getStatus(), productEntry.getStatus()))
            return false;

        List<Long> thisIds = getModifiers()
                .stream()
                .map(Modifier::getId)
                .sorted()
                .collect(Collectors.toList());

        List<Long> otherIds = productEntry.getModifiers()
                .stream()
                .map(Modifier::getId)
                .sorted()
                .collect(Collectors.toList());

        return CollectionUtils.isEqualCollection(thisIds, otherIds);

    }

    /******************************************************************
     *                                                                *
     * Modifiers String                                               *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper modifiersText;

    public ReadOnlyStringProperty modifiersTextProperty() {
        if (modifiersText == null) {
            modifiersText = new ReadOnlyStringWrapper();
            modifiersText.bind(new StringBinding() {
                {
                    bind(modifiers);
                }

                @Override
                protected String computeValue() {
                    StringBuilder sb = new StringBuilder("");
                    for (Modifier modifier : modifiers)
                        sb.append(modifier.getName()).append(" - ").append(modifier.getPrice()).append('\n');
                    return sb.toString();
                }
            });
        }
        return modifiersText.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Status Text                                                    *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper statusText;

    public ReadOnlyStringProperty statusTextProperty() {
        if (statusText == null) {
            statusText = new ReadOnlyStringWrapper();
            statusText.bind(new StringBinding() {
                {
                    bind(status);
                }

                @Override
                protected String computeValue() {
                    switch (status.get()) {
                        case REQUEST_EDIT:
                            return "Editing";
                        case REQUEST_HOLD:
                            return "Holding";
                        case REQUEST_VOID:
                            return "Voiding";
                        case REQUEST_SENT:
                            return "Sending";
                        case HOLD:
                            return "Held";
                        case SENT:
                            return "Sent";
                        case VOID:
                            return "Void";
                        default:
                            return "Error";
                    }
                }
            });
        }
        return statusText.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Menu Item Name                                                 *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper menuItemName;

    public ReadOnlyStringProperty menuItemNameProperty() {
        if (menuItemName == null) {
            menuItemName = new ReadOnlyStringWrapper();
            menuItemName.bind(menuItem.get().nameProperty());
        }
        return menuItemName.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Modifiers Total                                                *
     *                                                                *
     ******************************************************************/

    private ReadOnlyObjectWrapper<BigDecimal> modifierTotal;

    public ReadOnlyObjectProperty<BigDecimal> modifierTotalProperty() {
        if (modifierTotal == null) {
            modifierTotal = new ReadOnlyObjectWrapper<>();
            modifierTotal.bind(new ObjectBinding<BigDecimal>() {
                {
                    bind(modifiersProperty());
                }

                @Override
                protected BigDecimal computeValue() {
                    return modifiers.stream()
                            .map(Modifier::getPrice)
                            .reduce(BigDecimal::add)
                            .orElse(BigDecimal.ZERO);
                }
            });
        }
        return modifierTotal.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Total                                                          *
     *                                                                *
     ******************************************************************/

    private ReadOnlyObjectWrapper<BigDecimal> total;

    public ReadOnlyObjectProperty<BigDecimal> totalProperty() {
        if (total == null) {
            total = new ReadOnlyObjectWrapper<>();
            total.bind(new ObjectBinding<BigDecimal>() {
                {
                    bind(modifierTotalProperty(), menuItem, quantity);
                }

                @Override
                protected BigDecimal computeValue() {
                    if (modifierTotal.get() != null && menuItem.get() != null && menuItem.get().getPrice() != null)
                        return (modifierTotalProperty().get().add(menuItem.get().getPrice()))
                                .multiply(quantity.get())
                                .max(BigDecimal.ZERO)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
                    return BigDecimal.ZERO;
                }

                @Override
                public void dispose() {
                    unbind(modifierTotal, menuItem);
                }
            });
        }
        return total.getReadOnlyProperty();
    }

}
