package ow.micropos.client.desktop.model.orders;

import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.time.FastDateFormat;
import org.fxmisc.easybind.EasyBind;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.*;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.target.Customer;
import ow.micropos.client.desktop.model.target.Seat;

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
        created.setStatus(SalesOrderStatus.OPEN);
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
        created.setGratuityPercent(BigDecimal.ZERO);
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

    private ObjectProperty<Date> cookTime = new SimpleObjectProperty<>();

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

    public Date getCookTime() {
        return cookTime.get();
    }

    public ObjectProperty<Date> cookTimeProperty() {
        return cookTime;
    }

    public void setCookTime(Date cookTime) {
        this.cookTime.set(cookTime);
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

    public boolean hasStatuses(SalesOrderStatus... statuses) {
        for (SalesOrderStatus status : statuses)
            if (hasStatus(status))
                return true;
        return false;
    }

    public ProductEntry addMenuItem(MenuItem menuItem) {
        ProductEntry newPE = new ProductEntry(menuItem, BigDecimal.ONE);

        ProductEntry addedTo = null;

        boolean added = false;
        for (ProductEntry oldPE : productEntriesProperty()) {
            if (oldPE.canMerge(newPE)) {
                oldPE.setQuantity(oldPE.getQuantity().add(newPE.getQuantity()));
                addedTo = oldPE;
                added = true;
                break;
            }
        }

        if (!added) {
            addedTo = newPE;
            productEntriesProperty().add(newPE);
        }

        return addedTo;
    }

    public boolean canHaveGratuity() {
        return hasType(SalesOrderType.DINEIN);
    }

    public boolean hasGratuity() {
        return getGratuityPercent() != null && getGratuityPercent().compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean hasAppliedCharge() {
        for (ChargeEntry ce : chargeEntries)
            if (ce.hasStatus(ChargeEntryStatus.APPLIED) || ce.hasStatus(ChargeEntryStatus.REQUEST_APPLY))
                return true;
        return false;
    }

    public boolean hasUntaxedEntry() {
        for (ProductEntry pe : productEntries)
            if (!pe.getMenuItem().getTaxed())
                return true;
        return false;
    }

    public boolean canPrint() {
        if (hasStatus(SalesOrderStatus.REQUEST_CLOSE)
                || hasStatus(SalesOrderStatus.REQUEST_OPEN)
                || hasStatus(SalesOrderStatus.REQUEST_VOID))
            return false;

        for (PaymentEntry pe : paymentEntries)
            if (pe.hasStatus(PaymentEntryStatus.REQUEST_PAID)
                    || pe.hasStatus(PaymentEntryStatus.REQUEST_VOID))
                return false;

        for (ProductEntry pe : productEntries)
            if (pe.hasStatus(ProductEntryStatus.REQUEST_EDIT)
                    || pe.hasStatus(ProductEntryStatus.REQUEST_SENT)
                    || pe.hasStatus(ProductEntryStatus.REQUEST_VOID))
                return false;

        for (ChargeEntry ce : chargeEntries)
            if (ce.hasStatus(ChargeEntryStatus.REQUEST_APPLY)
                    || ce.hasStatus(ChargeEntryStatus.REQUEST_VOID))
                return false;

        return true;
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

    private ReadOnlyObjectWrapper<BigDecimal> untaxedProductTotal;
    private ReadOnlyObjectWrapper<BigDecimal> productTotal;
    private ReadOnlyObjectWrapper<BigDecimal> chargeTotal;
    private ReadOnlyObjectWrapper<BigDecimal> subTotal;
    private ReadOnlyObjectWrapper<BigDecimal> taxTotal;
    private ReadOnlyObjectWrapper<BigDecimal> gratuityTotal;
    private ReadOnlyObjectWrapper<BigDecimal> grandTotal;
    private ReadOnlyObjectWrapper<BigDecimal> paymentTotal;
    private ReadOnlyObjectWrapper<BigDecimal> change;
    private ReadOnlyObjectWrapper<BigDecimal> due;

    public ReadOnlyObjectProperty<BigDecimal> untaxedProductTotalProperty() {
        if (untaxedProductTotal == null) {
            untaxedProductTotal = new ReadOnlyObjectWrapper<>();
            untaxedProductTotal.bind(EasyBind.combine(
                    EasyBind.map(
                            productEntriesProperty().filtered(pe -> !pe.getMenuItem().taxedProperty().get()),
                            ProductEntry::totalProperty
                    ),
                    bigDecimalStream -> BigDecimalUtils.asDollars(
                            bigDecimalStream
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .max(BigDecimal.ZERO)
                    )
            ));

        }
        return untaxedProductTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> productTotalProperty() {
        if (productTotal == null) {
            productTotal = new ReadOnlyObjectWrapper<>();
            productTotal.bind(EasyBind.combine(
                    EasyBind.map(
                            productEntriesProperty().filtered(pe -> pe.getMenuItem().taxedProperty().get()),
                            ProductEntry::totalProperty
                    ),
                    bigDecimalStream -> BigDecimalUtils.asDollars(
                            bigDecimalStream
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .max(BigDecimal.ZERO)
                    )
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
                    chargeEntries.forEach(ce -> bind(ce.statusProperty()));
                    chargeEntries.addListener((obs, oldVal, newVal) -> {
                        oldVal.forEach(ce -> unbind(ce.statusProperty()));
                        newVal.forEach(ce -> bind(ce.statusProperty()));
                    });
                }

                @Override
                protected BigDecimal computeValue() {
                    BigDecimal total = BigDecimal.ZERO;
                    for (ChargeEntry chargeEntry : chargeEntriesProperty())
                        total = total.add(chargeEntry.applyAmountIfActive(productTotalProperty().get()));
                    return BigDecimalUtils.asDollars(total);
                }
            });
        }
        return chargeTotal.getReadOnlyProperty();
    }

    // Taxable Sub Total
    public ReadOnlyObjectProperty<BigDecimal> subTotalProperty() {
        if (subTotal == null) {
            subTotal = new ReadOnlyObjectWrapper<>();
            subTotal.bind(EasyBind.combine(
                    productTotalProperty(),
                    chargeTotalProperty(),
                    (a, b) -> BigDecimalUtils.asDollars(a.add(b).max(BigDecimal.ZERO))
            ));
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
                        return BigDecimalUtils.ZERO_DOLLARS;

                    return BigDecimalUtils.asDollars(
                            subTotalProperty().get()
                                    .multiply(taxPercent.get())
                                    .max(BigDecimal.ZERO)
                    );
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
                        return BigDecimalUtils.ZERO_DOLLARS;

                    return BigDecimalUtils.asDollars(
                            subTotalProperty().get()
                                    .multiply(gratuityPercent.get())
                                    .max(BigDecimal.ZERO)
                    );
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
                    untaxedProductTotalProperty(),
                    (a, b, c, d) -> BigDecimalUtils.asDollars(a.add(b).add(c).add(d).max(BigDecimal.ZERO))
            ));
        }
        return grandTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> paymentTotalProperty() {
        if (paymentTotal == null) {
            paymentTotal = new ReadOnlyObjectWrapper<>();
            paymentTotal.bind(new ObjectBinding<BigDecimal>() {
                {
                    bind(paymentEntriesProperty());
                    paymentEntries.forEach(pe -> bind(pe.statusProperty()));
                    paymentEntries.addListener((obs, oldVal, newVal) -> {
                        oldVal.forEach(pe -> unbind(pe.statusProperty()));
                        newVal.forEach(pe -> bind(pe.statusProperty()));
                    });
                }

                @Override
                protected BigDecimal computeValue() {
                    return paymentEntriesProperty()
                            .stream()
                            .filter(pe -> pe.hasStatus(PaymentEntryStatus.PAID) || pe.hasStatus(PaymentEntryStatus.REQUEST_PAID))
                            .map(PaymentEntry::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .max(BigDecimal.ZERO)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            });
        }
        return paymentTotal.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> changeProperty() {
        if (change == null) {
            change = new ReadOnlyObjectWrapper<>();
            change.bind(EasyBind.combine(
                    paymentTotalProperty(),
                    grandTotalProperty(),
                    (a, b) -> BigDecimalUtils.asDollars(a.subtract(b))
            ));
        }
        return change.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<BigDecimal> dueProperty() {
        if (due == null) {
            due = new ReadOnlyObjectWrapper<>();
            due.bind(EasyBind.combine(
                    grandTotalProperty(),
                    paymentTotalProperty(),
                    (a, b) -> BigDecimalUtils.asDollars(a.subtract(b).max(BigDecimal.ZERO))
            ));
        }
        return due.getReadOnlyProperty();
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
     * Cook Time
     *                                                                *
     ******************************************************************/
    private ReadOnlyStringWrapper prettyCookTime;

    public ReadOnlyStringProperty prettyCookTimeProperty() {
        if (prettyCookTime == null) {
            prettyCookTime = new ReadOnlyStringWrapper();
            prettyCookTime.bind(new StringBinding() {

                {
                    bind(cookTimeProperty());
                }

                @Override
                protected String computeValue() {
                    if (cookTimeProperty().get() == null)
                        return "";
                    else
                        return "@" + tf.format(cookTimeProperty().get()) + " | ";
                }
            });
        }
        return prettyCookTime.getReadOnlyProperty();
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
                            return "Creating";
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

    public ReadOnlyStringProperty gratuityTextProperty() {
        if (gratuityText == null) {
            gratuityText = new ReadOnlyStringWrapper();
            gratuityText.bind(new StringBinding() {
                {
                    bind(gratuityPercent);
                }

                @Override
                protected String computeValue() {
                    return BigDecimalUtils.asPercent(getGratuityPercent()).toString() + "%";
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
                    return BigDecimalUtils.asPercent(getTaxPercent()).toString() + "%";
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


    /******************************************************************
     *                                                                *
     * Payment Summary                                                *
     *                                                                *
     ******************************************************************/

    private ReadOnlyStringWrapper paymentSummary;

    public ReadOnlyStringProperty paymentSummaryProperty() {
        if (paymentSummary == null) {
            paymentSummary = new ReadOnlyStringWrapper();
            paymentSummary.bind(new StringBinding() {
                {
                    bind(paymentEntries);
                }

                @Override
                protected String computeValue() {
                    int cash = 0;
                    int credit = 0;
                    int check = 0;
                    int gc = 0;

                    for (PaymentEntry paymentEntry : getPaymentEntries()) {
                        switch (paymentEntry.getType()) {
                            case CASH:
                                cash++;
                                break;
                            case CREDIT:
                                credit++;
                                break;
                            case CHECK:
                                check++;
                                break;
                            case GIFTCARD:
                                gc++;
                                break;
                            default:
                                break;
                        }
                    }
                    return String.format("Cash(%d), Credit(%d), Check(%d), Gc(%d)", cash, credit, check, gc);
                }
            });
        }
        return paymentSummary.getReadOnlyProperty();
    }

}
