package ow.micropos.client.desktop.model.orders;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ow.micropos.client.desktop.model.enums.ChargeEntryStatus;
import ow.micropos.client.desktop.model.enums.ChargeType;
import ow.micropos.client.desktop.model.menu.Charge;

import java.math.BigDecimal;

public class ChargeEntry {

    public ChargeEntry() {}

    public ChargeEntry(Charge charge) {
        setCharge(charge);
        setStatus(ChargeEntryStatus.REQUEST_APPLY);
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private ObjectProperty<Charge> charge = new SimpleObjectProperty<>();

    private ObjectProperty<ChargeEntryStatus> status = new SimpleObjectProperty<>();

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

    public Charge getCharge() {
        return charge.get();
    }

    public ObjectProperty<Charge> chargeProperty() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge.set(charge);
    }

    public ChargeEntryStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<ChargeEntryStatus> statusProperty() {
        return status;
    }

    public void setStatus(ChargeEntryStatus status) {
        this.status.set(status);
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

    public boolean hasStatus(ChargeEntryStatus status) {
        return getStatus() == status;
    }

    public boolean hasType(ChargeType type) {
        return getCharge() != null && getCharge().hasType(type);
    }

    public BigDecimal applyAmountTo(BigDecimal price) {
        Charge charge = getCharge();

        if (charge == null)
            return BigDecimal.ZERO;
        else
            return charge.applyAmountTo(price);
    }

    public BigDecimal applyAmountIfActive(BigDecimal price) {
        if (hasStatus(ChargeEntryStatus.APPLIED) ||
                hasStatus(ChargeEntryStatus.REQUEST_APPLY))
            return applyAmountTo(price);
        else
            return BigDecimal.ZERO;
    }

}