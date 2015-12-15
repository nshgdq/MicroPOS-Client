package ow.micropos.client.desktop.model.orders;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ow.micropos.client.desktop.model.enums.PaymentEntryStatus;
import ow.micropos.client.desktop.model.enums.PaymentEntryType;

import java.math.BigDecimal;

public class PaymentEntry {

    public PaymentEntry() {}

    public PaymentEntry(BigDecimal amount, PaymentEntryType type) {
        setStatus(PaymentEntryStatus.REQUEST_PAID);
        setAmount(amount);
        setType(type);
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();

    private ObjectProperty<PaymentEntryStatus> status = new SimpleObjectProperty<>();

    private ObjectProperty<PaymentEntryType> type = new SimpleObjectProperty<>();

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

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public PaymentEntryStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<PaymentEntryStatus> statusProperty() {
        return status;
    }

    public void setStatus(PaymentEntryStatus status) {
        this.status.set(status);
    }

    public PaymentEntryType getType() {
        return type.get();
    }

    public ObjectProperty<PaymentEntryType> typeProperty() {
        return type;
    }

    public void setType(PaymentEntryType type) {
        this.type.set(type);
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

    public boolean hasStatus(PaymentEntryStatus status) {
        return getStatus() == status;
    }

    public boolean hasType(PaymentEntryType type) {
        return getType() == type;
    }

}
