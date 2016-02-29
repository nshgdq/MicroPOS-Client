package ow.micropos.client.desktop.custom.wok;

import email.com.gmail.ttsai0509.escpos.EscPosBuilder;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import email.com.gmail.ttsai0509.print.printer.PrintJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.App;
import ow.micropos.client.desktop.custom.PrintJobBuilder;
import ow.micropos.client.desktop.model.enums.PaymentEntryStatus;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.orders.PaymentEntry;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.MonthlySalesReport;
import ow.micropos.client.desktop.model.report.SalesReport;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class WokPrintJobBuilder implements PrintJobBuilder {

    private static final Logger log = LoggerFactory.getLogger(WokPrintJobBuilder.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
    private static final SimpleDateFormat dayOnlyFormat = new SimpleDateFormat("MM/dd/yy");
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");

    private final int width;
    private final EscPosBuilder builder;

    public WokPrintJobBuilder(int width) {
        this.width = width;
        this.builder = new EscPosBuilder();
    }

    @Override
    public PrintJob check(SalesOrder so) {
        return begin()
                .restaurant()
                .address()
                .salesOrderInfo(so)
                .salesOrderContent(so)
                .salesOrderTotals(so)
                .paymentEntryTotals(so)
                .thanks()
                .footer(so.getDate())
                .finish();
    }

    @Override
    public PrintJob salesReport(SalesReport report) {
        return begin()
                .restaurant()
                .address()
                .printSalesReport(report)
                .finish();
    }

    @Override
    public PrintJob monthlySalesReport(MonthlySalesReport report) {
        return begin()
                .restaurant()
                .address()
                .printMonthlySalesReport(report)
                .finish();
    }

    /******************************************************************
     *                                                                *
     * Common
     *                                                                *
     ******************************************************************/

    private WokPrintJobBuilder begin() {
        builder.reset().initialize();
        return this;
    }

    private WokPrintJobBuilder restaurant() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.DWDH_EMPHASIZED)
                .text("ORIENTAL WOK")
                .feed(2);
        return this;
    }

    private WokPrintJobBuilder address() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text("6 NORTH BOLTON AVE.")
                .feed(1)
                .text("ALEXANDRIA, LA 71301")
                .feed(1)
                .text("(318) 448-8247")
                .feed(2);
        return this;
    }

    private WokPrintJobBuilder thanks() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text("We appreciate your business!")
                .feed(2);
        return this;
    }

    private WokPrintJobBuilder footer(Date date) {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text(Long.toString(date.getTime()))
                .feed(2);
        return this;
    }

    private WokPrintJobBuilder printSalesReport(SalesReport report) {
        return text(dateFormat.format(new Date()))
                .feed(2)
                .align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.EMPHASIZED)
                .optional(report.start != null, () -> text("Start Date : " + dateFormat.format(report.start)).feed())
                .optional(report.end != null, () -> text("End Date : " + dateFormat.format(report.end)).feed())
                .optional(report.status != null, () -> text("Status Filter : " + report.status).feed())
                .optional(report.type != null, () -> text("Type Filter : " + report.type).feed())
                .font(EscPosBuilder.Font.REGULAR)
                .feed()
                .total("Product Total  (" + report.productCount + ")", report.productTotal.toString())
                .total("Charge Total   (" + report.chargeCount + ")", report.chargeTotal.toString())
                .total("Sub Total", report.subTotal.toString())
                .feed()
                .total("Tax Total", report.taxTotal.toString())
                .total("Gratuity Total (" + report.gratuityCount + ")", report.gratuityTotal.toString())
                .total("Grand Total    (" + report.orderCount + ")", report.grandTotal.toString())
                .font(EscPosBuilder.Font.EMPHASIZED)
                .feed()
                .total("Sales Total", report.grandTotal.subtract(report.gratuityTotal).toString())
                .font(EscPosBuilder.Font.REGULAR)
                .feed()
                .total("Payment Total  (" + report.paymentCount + ")", report.paymentTotal.toString())
                .total("   Cash        (" + report.cashCount + ")", report.cashTotal.toString())
                .total("   Credit      (" + report.creditCount + ")", report.creditTotal.toString())
                .total("   Check       (" + report.checkCount + ")", report.checkTotal.toString())
                .total("   Giftcard    (" + report.giftcardCount + ")", report.giftcardTotal.toString())
                .feed();
    }

    private WokPrintJobBuilder printMonthlySalesReport(MonthlySalesReport report) {
        Calendar c = Calendar.getInstance();
        c.setTime(report.monthOf);

        return text(dateFormat.format(new Date()))
                .feed(2)
                .align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.EMPHASIZED)
                .text("Monthly Report : " + monthFormat.format(report.monthOf)).feed()
                .feed(1)
                .col3("Date", "Tax", "Total")
                .font(EscPosBuilder.Font.REGULAR)
                .forEach(report.dailySales, sales -> {
                    int idx = report.dailySales.indexOf(sales);
                    col3(dayOnlyFormat.format(c.getTime()), report.dailyTax.get(idx).toString(), sales.toString());
                    c.add(Calendar.DATE, 1);
                })
                .feed()
                .font(EscPosBuilder.Font.EMPHASIZED)
                .col3("Total", report.netTax.toString(), report.netSales.toString())
                .feed();
    }

    private PrintJob finish() {
        byte[] result = builder
                .feed(5)
                .cut(EscPosBuilder.Cut.PART)
                .getBytes();

        builder.reset();

        return new PrintJob(result, true);
    }

    /******************************************************************
     *                                                                *
     * Sales Orders
     *                                                                *
     ******************************************************************/

    private WokPrintJobBuilder salesOrderInfo(SalesOrder so) {
        return align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.DWDH)
                .optional(so.hasType(SalesOrderType.DINEIN), () -> text("Dine In "))
                .optional(so.hasType(SalesOrderType.TAKEOUT), () -> text("Take Out "))
                .optional(App.properties.getBool("receipt-order-id"), () -> text("#" + so.getId().toString()))
                .feed(2)
                .align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.REGULAR)
                .text("Date     : " + dateFormat.format(so.getDate()))
                .feed(1)
                .text("Server   : " + so.getEmployee().getFirstName())
                .feed(1)
                .optional(so.hasType(SalesOrderType.DINEIN),
                        () -> text("Seat     : " + so.getSeat().getId()))
                .optional(so.hasType(SalesOrderType.TAKEOUT),
                        () -> text("Customer : " + so.getCustomer().fullNameProperty().get()).feed(1))
                .optional(so.hasType(SalesOrderType.TAKEOUT),
                        () -> text("Phone    : " + so.getCustomer().getPhoneNumber()))
                .feed(2);
    }

    private WokPrintJobBuilder salesOrderContent(SalesOrder so) {

        // Skip void items
        List<ProductEntry> active = so.getProductEntries().filtered(
                pe -> !pe.hasStatus(ProductEntryStatus.REQUEST_VOID)
        );

        return align(EscPosBuilder.Align.LEFT)
                .forEach(active, pe -> {

                    String item = pe.getMenuItem().getName();
                    String tag = pe.getMenuItem().getTag();
                    String quantity = BigDecimalUtils.asQuantity(pe.getQuantity()).toString();
                    String price = pe.totalProperty().get().toString();

                    font(EscPosBuilder.Font.REGULAR)
                            .total(quantity + " " + tag + " " + item, price)
                            .forEach(pe.getModifiers(),
                                    mod -> total("     " + mod.getTag() + " " + mod.getName(), mod.getPrice() + "     ")
                            );
                })
                .feed(1);
    }

    private WokPrintJobBuilder salesOrderTotals(SalesOrder so) {

        String subtotal = so.productTotalProperty().get().toString();
        String discounts = so.chargeTotalProperty().get().toString();
        String gratuity = so.gratuityTotalProperty().get().toString();
        String taxtotal = so.taxTotalProperty().get().toString();
        String grandtotal = so.grandTotalProperty().get().toString();

        return align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.REGULAR)
                .total("Subtotal", subtotal)
                .optional(so.hasAppliedCharge(), () -> total("Discounts", discounts))
                .optional(so.hasGratuity(), () -> total("Gratuity (" + so.gratuityTextProperty().get() + ")", gratuity))
                .total("Sales Tax (" + so.taxTextProperty().get() + ")", taxtotal)
                .font(EscPosBuilder.Font.EMPHASIZED)
                .total("Grand Total", grandtotal)
                .feed(1);
    }

    private WokPrintJobBuilder paymentEntryTotals(SalesOrder so) {

        List<PaymentEntry> active = so.getPaymentEntries().filtered(
                pe -> !pe.hasStatus(PaymentEntryStatus.VOID) && !pe.hasStatus(PaymentEntryStatus.REQUEST_VOID)
        );

        return optional(!active.isEmpty() && so.dueProperty().get().compareTo(BigDecimal.ZERO) == 0,
                () -> align(EscPosBuilder.Align.LEFT)
                        .font(EscPosBuilder.Font.REGULAR)
                        .forEach(active, pe -> total(pe.getType().name(), pe.getAmount().toString()))
                        .font(EscPosBuilder.Font.EMPHASIZED)
                        .total("Change Due", so.changeProperty().get().toString())
                        .font(EscPosBuilder.Font.REGULAR)
                        .feed(2));
    }

    /******************************************************************
     *                                                                *
     * Convenience
     *                                                                *
     ******************************************************************/

    private interface Action {
        void invoke();
    }

    private interface Action1<T> {
        void invoke(T item);
    }

    private WokPrintJobBuilder optional(boolean condition, Action action) {
        if (condition)
            action.invoke();
        return this;
    }

    private WokPrintJobBuilder total(String desc, int total) {
        return total(desc, Integer.toString(total));
    }

    private WokPrintJobBuilder total(String desc, String total) {

        int dWidth = width - total.length() - 1;

        builder.text(String.format("%-" + dWidth + "." + dWidth + "s", desc))
                .text(" ")
                .text(total)
                .feed(1);

        return this;
    }

    private WokPrintJobBuilder col3(String desc, String dat1, String dat2) {

        int cWidth = width / 3;

        builder.text(String.format("%-" + cWidth + "." + cWidth + "s", desc))
                .text(String.format("%" + cWidth + "s", dat1))
                .text(String.format("%" + cWidth + "s", dat2))
                .feed(1);

        return this;
    }

    private WokPrintJobBuilder text(String text) {
        builder.text(text);
        return this;
    }

    private WokPrintJobBuilder feed() {
        builder.feed(1);
        return this;
    }

    private WokPrintJobBuilder feed(int lines) {
        builder.feed(lines);
        return this;
    }

    private WokPrintJobBuilder font(EscPosBuilder.Font font) {
        builder.font(font);
        return this;
    }

    private WokPrintJobBuilder align(EscPosBuilder.Align align) {
        builder.align(align);
        return this;
    }

    private <T> WokPrintJobBuilder forEach(Collection<T> items, Action1<T> action) {
        items.forEach(action::invoke);
        return this;
    }

}
