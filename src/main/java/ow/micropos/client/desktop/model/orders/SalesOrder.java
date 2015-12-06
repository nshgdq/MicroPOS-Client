package ow.micropos.client.desktop.model.orders;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.time.FastDateFormat;
import org.fxmisc.easybind.EasyBind;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.people.Customer;
import ow.micropos.client.desktop.model.people.Employee;
import ow.micropos.client.desktop.model.seating.Seat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SalesOrder {

    public SalesOrder() {}

    public static SalesOrder fromModel(Employee employee, SalesOrder salesOrder) {
        SalesOrder created = new SalesOrder();
        created.setEmployee(employee);
        created.setCustomer(salesOrder.getCustomer());
        created.setSeat(salesOrder.getSeat());
        created.setType(salesOrder.getType());
        created.setStatus(SalesOrderStatus.REQUEST_OPEN);
        created.setTaxPercent(salesOrder.getTaxPercent());
        created.setGratuityPercent(salesOrder.getGratuityPercent());
        created.setDate(new Date());
        return created;
    }

    public static SalesOrder forCustomer(Employee employee, Customer customer, BigDecimal taxPercent) {
        SalesOrder created = new SalesOrder();
        created.setEmployee(employee);
        created.setCustomer(customer);
        created.setStatus(SalesOrderStatus.REQUEST_OPEN);
        created.setType(SalesOrderType.TAKEOUT);
        created.setTaxPercent(taxPercent);
        created.setGratuityPercent(new BigDecimal("0.00"));
        created.setDate(new Date());
        return created;
    }

    public static SalesOrder forSeat(Employee employee, Seat seat, BigDecimal taxPercent, BigDecimal gratuityPercent) {
        SalesOrder created = new SalesOrder();
        created.setEmployee(employee);
        created.setSeat(seat);
        created.setStatus(SalesOrderStatus.REQUEST_OPEN);
        created.setType(SalesOrderType.DINEIN);
        created.setTaxPercent(taxPercent);
        created.setGratuityPercent(gratuityPercent);
        created.setDate(new Date());
        return created;
    }

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();

    private ObjectProperty<Employee> employee = new SimpleObjectProperty<>();

    private ObjectProperty<Customer> customer = new SimpleObjectProperty<>();

    private ObjectProperty<Seat> seat = new SimpleObjectProperty<>();

    private ObjectProperty<Date> date = new SimpleObjectProperty<>();

    private ObjectProperty<SalesOrderStatus> status = new SimpleObjectProperty<>();

    private ObjectProperty<SalesOrderType> type = new SimpleObjectProperty<>();

    private ObjectProperty<BigDecimal> taxPercent = new SimpleObjectProperty<>();

    private ObjectProperty<BigDecimal> gratuityPercent = new SimpleObjectProperty<>();

    private ListProperty<ChargeEntry> chargeEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<PaymentEntry> paymentEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<ProductEntry> productEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Long getId() {
        return id.get();
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public Employee getEmployee() {
        return employee.get();
    }

    public ObjectProperty<Employee> employeeProperty() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee.set(employee);
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public ObjectProperty<Customer> customerProperty() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer.set(customer);
    }

    public Seat getSeat() {
        return seat.get();
    }

    public ObjectProperty<Seat> seatProperty() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat.set(seat);
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

    public SalesOrderStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<SalesOrderStatus> statusProperty() {
        return status;
    }

    public void setStatus(SalesOrderStatus status) {
        this.status.set(status);
    }

    public SalesOrderType getType() {
        return type.get();
    }

    public ObjectProperty<SalesOrderType> typeProperty() {
        return type;
    }

    public void setType(SalesOrderType type) {
        this.type.set(type);
    }

    public BigDecimal getTaxPercent() {
        return taxPercent.get();
    }

    public ObjectProperty<BigDecimal> taxPercentProperty() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent.set(taxPercent);
    }

    public BigDecimal getGratuityPercent() {
        return gratuityPercent.get();
    }

    public ObjectProperty<BigDecimal> gratuityPercentProperty() {
        return gratuityPercent;
    }

    public void setGratuityPercent(BigDecimal gratuityPercent) {
        this.gratuityPercent.set(gratuityPercent);
    }

    public ObservableList<ChargeEntry> getChargeEntries() {
        return chargeEntries.get();
    }

    public ListProperty<ChargeEntry> chargeEntriesProperty() {
        return chargeEntries;
    }

    public void setChargeEntries(List<ChargeEntry> chargeEntries) {
        this.chargeEntries.setAll(chargeEntries);
    }

    public ObservableList<PaymentEntry> getPaymentEntries() {
        return paymentEntries.get();
    }

    public ListProperty<PaymentEntry> paymentEntriesProperty() {
        return paymentEntries;
    }

    public void setPaymentEntries(List<PaymentEntry> paymentEntries) {
        this.paymentEntries.setAll(paymentEntries);
    }

    public ObservableList<ProductEntry> getProductEntries() {
        return productEntries.get();
    }

    public ListProperty<ProductEntry> productEntriesProperty() {
        return productEntries;
    }

    public void setProductEntries(List<ProductEntry> productEntries) {
        this.productEntries.setAll(productEntries);
    }

    public boolean hasCustomer() {
        return getCustomer() != null;
    }

    public boolean hasSeat() {
        return getSeat() != null;
    }

    public boolean hasType(SalesOrderType type) {
        return getType() == type;
    }

    public boolean hasStatus(SalesOrderStatus status) {
        return getStatus() == status;
    }

    public void addMenuItem(MenuItem menuItem) {
        ProductEntry newPE = new ProductEntry(menuItem, BigDecimal.ONE);

        boolean added = false;
        for (ProductEntry oldPE : productEntriesProperty()) {
            if (oldPE.canMerge(newPE)) {
                oldPE.setQuantity(oldPE.getQuantity().add(newPE.getQuantity()));
                added = true;
                break;
            }
        }

        if (!added)
            productEntriesProperty().add(newPE);
    }

    public boolean canHaveGratuity() {
        return hasType(SalesOrderType.DINEIN);
    }

    public boolean hasGratuity() {
        return getGratuityPercent() != null && getGratuityPercent().compareTo(BigDecimal.ZERO) != 0;
    }

    /******************************************************************
     *                                                                *
     * Customer Name                                                  *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper targetName;

    public ReadOnlyStringProperty targetNameProperty() {
        if (targetName == null) {
            targetName = new ReadOnlyStringWrapper();
            targetName.bind(new StringBinding() {
                {
                    bind(customer, seat, type);
                }

                @Override
                protected String computeValue() {
                    if (hasCustomer() && hasType(SalesOrderType.TAKEOUT))
                        return customer.get().fullNameProperty().get();
                    else if (hasSeat() && hasType(SalesOrderType.DINEIN))
                        return "Seat " + seat.get().getTag();
                    else
                        return "Invalid Target";
                }
            });
        }
        return targetName.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Employee Text                                                  *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper employeeName;

    public ReadOnlyStringProperty employeeNameProperty() {
        if (employeeName == null) {
            employeeName = new ReadOnlyStringWrapper();
            employeeName.bind(employee.get().fullNameProperty());
        }
        return employeeName.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Totals                                                         *
     *                                                                *
     ******************************************************************/

    private ReadOnlyObjectWrapper<BigDecimal> productTotal;
    private ReadOnlyObjectWrapper<BigDecimal> chargeTotal;
    private ReadOnlyObjectWrapper<BigDecimal> subTotal;
    private ReadOnlyObjectWrapper<BigDecimal> taxTotal;
    private ReadOnlyObjectWrapper<BigDecimal> gratuityTotal;
    private ReadOnlyObjectWrapper<BigDecimal> grandTotal;
    private ReadOnlyObjectWrapper<BigDecimal> paymentTotal;
    private ReadOnlyObjectWrapper<BigDecimal> change;

    public ReadOnlyObjectProperty<BigDecimal> productTotalProperty() {
        if (productTotal == null) {
            productTotal = new ReadOnlyObjectWrapper<>();
            productTotal.bind(EasyBind.combine(
                    EasyBind.map(
                            productEntriesProperty(),
                            ProductEntry::totalProperty
                    ),
                    bigDecimalStream -> bigDecimalStream
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .max(BigDecimal.ZERO)
                            .setScale(2, BigDecimal.ROUND_HALF_UP)
            ));

        }
        return productTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> chargeTotalProperty() {
        if (chargeTotal == null) {
            chargeTotal = new ReadOnlyObjectWrapper<>();
            chargeTotal.bind(new ObjectBinding<BigDecimal>() {
                {
                    bind(productTotalProperty(), chargeEntriesProperty());
                }

                @Override
                protected BigDecimal computeValue() {
                    BigDecimal total = new BigDecimal(0);
                    for (ChargeEntry chargeEntry : chargeEntriesProperty())
                        total = total.add(chargeEntry.applyAmountIfActive(productTotalProperty().get()));
                    return total.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            });
        }
        return chargeTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> subTotalProperty() {
        if (subTotal == null) {
            subTotal = new ReadOnlyObjectWrapper<>();
            subTotal.bind(EasyBind.combine(
                            productTotalProperty(),
                            chargeTotalProperty(),
                            (a, b) -> a.add(b).max(BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP))
            );
        }
        return subTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> taxTotalProperty() {
        if (taxTotal == null) {
            taxTotal = new ReadOnlyObjectWrapper<>();
            taxTotal.bind(new ObjectBinding<BigDecimal>() {
                {
                    bind(subTotalProperty(), taxPercentProperty());
                }

                @Override
                protected BigDecimal computeValue() {
                    if (subTotalProperty().get() == null ||
                            taxPercentProperty().get() == null)
                        return BigDecimal.ZERO;
                    return subTotalProperty().get()
                            .multiply(taxPercent.get())
                            .max(BigDecimal.ZERO)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            });
        }
        return taxTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> gratuityTotalProperty() {
        if (gratuityTotal == null) {
            gratuityTotal = new ReadOnlyObjectWrapper<>();
            gratuityTotal.bind(new ObjectBinding<BigDecimal>() {
                {
                    bind(subTotalProperty(), gratuityPercentProperty());
                }

                @Override
                protected BigDecimal computeValue() {
                    if (subTotalProperty().get() == null ||
                            gratuityPercentProperty().get() == null)
                        return BigDecimal.ZERO;
                    return subTotalProperty().get()
                            .multiply(gratuityPercent.get())
                            .max(BigDecimal.ZERO)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            });
        }
        return gratuityTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> grandTotalProperty() {
        if (grandTotal == null) {
            grandTotal = new ReadOnlyObjectWrapper<>();
            grandTotal.bind(EasyBind.combine(
                    subTotalProperty(),
                    taxTotalProperty(),
                    gratuityTotalProperty(),
                    (a, b, c) -> a.add(b).add(c).max(BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP)
            ));
        }
        return grandTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> paymentTotalProperty() {
        if (paymentTotal == null) {
            paymentTotal = new ReadOnlyObjectWrapper<>();
            paymentTotal.bind(EasyBind.combine(
                    EasyBind.map(getPaymentEntries(), PaymentEntry::amountProperty),
                    bigDecimalStream -> bigDecimalStream
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .max(BigDecimal.ZERO)
                            .setScale(2, BigDecimal.ROUND_HALF_UP)
            ));
        }
        return paymentTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> changeProperty() {
        if (change == null) {
            change = new ReadOnlyObjectWrapper<>();
            change.bind(EasyBind.combine(
                    paymentTotalProperty(),
                    grandTotalProperty(),
                    (a, b) -> a.subtract(b).setScale(2, BigDecimal.ROUND_HALF_UP)
            ));
        }
        return change.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Date Time                                                      *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper prettyDate;
    private ReadOnlyStringWrapper prettyTime;

    private static final FastDateFormat tf = FastDateFormat.getInstance("hh:mm");
    private static final FastDateFormat df = FastDateFormat.getInstance("MM/dd/yy");

    public ReadOnlyStringProperty prettyTimeProperty() {
        if (prettyTime == null) {
            prettyTime = new ReadOnlyStringWrapper();
            prettyTime.bind(new StringBinding() {

                {
                    bind(dateProperty());
                }

                @Override
                protected String computeValue() {
                    return tf.format(dateProperty().get());
                }
            });
        }
        return prettyTime.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty prettyDateProperty() {
        if (prettyDate == null) {
            prettyDate = new ReadOnlyStringWrapper();
            prettyDate.bind(new StringBinding() {

                {
                    bind(dateProperty());
                }

                @Override
                protected String computeValue() {
                    return df.format(dateProperty().get());
                }
            });
        }
        return prettyDate.getReadOnlyProperty();
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
                        case REQUEST_OPEN:
                            return "Opening";
                        case REQUEST_CLOSE:
                            return "Paying";
                        case REQUEST_VOID:
                            return "Voiding";
                        case OPEN:
                            return "Open";
                        case CLOSED:
                            return "Closed";
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
     * Tax / Gratuity As Percent                                      *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper gratuityText;

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public ReadOnlyStringProperty gratuityTextProperty() {
        if (gratuityText == null) {
            gratuityText = new ReadOnlyStringWrapper();
            gratuityText.bind(new StringBinding() {
                {
                    bind(gratuityPercent);
                }

                @Override
                protected String computeValue() {
                    return getGratuityPercent()
                            .multiply(HUNDRED)
                            .setScale(0, BigDecimal.ROUND_UNNECESSARY)
                            .toString() + "%";
                }
            });
        }
        return gratuityText.getReadOnlyProperty();
    }

    private ReadOnlyStringWrapper taxText;

    public ReadOnlyStringProperty taxTextProperty() {
        if (taxText == null) {
            taxText = new ReadOnlyStringWrapper();
            taxText.bind(new StringBinding() {
                {
                    bind(taxPercent);
                }

                @Override
                protected String computeValue() {
                    return getTaxPercent()
                            .multiply(HUNDRED)
                            .setScale(0, BigDecimal.ROUND_UNNECESSARY)
                            .toString() + "%";
                }
            });
        }
        return taxText.getReadOnlyProperty();
    }

    /******************************************************************
     *                                                                *
     * Order Summary                                                  *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper orderSummary;

    public ReadOnlyStringProperty orderSummaryProperty() {
        if (orderSummary == null) {
            orderSummary = new ReadOnlyStringWrapper();
            orderSummary.bind(new StringBinding() {
                {
                    bind(productEntries);
                }

                @Override
                protected String computeValue() {
                    StringBuilder sb = new StringBuilder();
                    for (ProductEntry pe : productEntries)
                        sb.append(pe.getMenuItem().getName()).append(", ");
                    return sb.toString();
                }
            });
        }
        return orderSummary.getReadOnlyProperty();
    }

}
