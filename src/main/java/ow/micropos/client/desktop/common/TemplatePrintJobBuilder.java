package ow.micropos.client.desktop.common;

import email.com.gmail.ttsai0509.escpos.EscPosBuilder;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import email.com.gmail.ttsai0509.print.printer.PrintJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.App;
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

public class TemplatePrintJobBuilder implements PrintJobBuilder {

    private static final Logger log = LoggerFactory.getLogger(TemplatePrintJobBuilder.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
    private static final SimpleDateFormat dayOnlyFormat = new SimpleDateFormat("MM/dd/yy");
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");

    private final String name;
    private final String address1;
    private final String address2;
    private final String phone;
    private final String thanks;
    private final int width;
    private final EscPosBuilder builder;

    public TemplatePrintJobBuilder(
            String name,
            String address1,
            String address2,
            String phone,
            String thanks,
            int width
    ) {
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.phone = phone;
        this.thanks = thanks;
        this.width = width;
        this.builder = new EscPosBuilder();
    }

    @Override
    public PrintJob check(SalesOrder so, boolean kick) {
        return begin()
                .restaurant()
                .address()
                .salesOrderInfo(so)
                .salesOrderContent(so)
                .salesOrderTotals(so)
                .paymentEntryTotals(so)
                .thanks()
                .footer(so.getDate())
                .optional(kick, () -> this.raw(KICK_CODE))
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

    /**
     * ***************************************************************
     * *
     * Common
     * *
     * ****************************************************************
     */

    private TemplatePrintJobBuilder begin() {
        builder.reset().initialize();
        return this;
    }

    private TemplatePrintJobBuilder restaurant() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.DWDH_EMPHASIZED)
                .text(this.name)
                .feed(2);
        return this;
    }

    private TemplatePrintJobBuilder address() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text(this.address1)
                .feed(1)
                .text(this.address2)
                .feed(1)
                .text(this.phone)
                .feed(2);
        return this;
    }

    private TemplatePrintJobBuilder thanks() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text(this.thanks)
                .feed(2);
        return this;
    }

    private TemplatePrintJobBuilder footer(Date date) {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text(Long.toString(date.getTime()))
                .feed(2);
        return this;
    }

    private TemplatePrintJobBuilder printSalesReport(SalesReport report) {
        return text(dateFormat.format(new Date()))
                .feed(2)
                .align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.EMPHASIZED)
                .optional(report.start != null, () -> text("From   : " + dateFormat.format(report.start)).feed())
                .optional(report.end != null, () -> text("To     : " + dateFormat.format(report.end)).feed())
                .optional(report.status != null, () -> text("Status : " + report.status).feed())
                .optional(report.type != null, () -> text("Type   : " + report.type).feed())
                .font(EscPosBuilder.Font.REGULAR)
                .feed()
                .total("Total", report.total.toString())
                .feed()
                .total("Payment Total", report.paymentTotal.toString())
                .total("   Cash      ", report.cashTotal.toString())
                .total("   Credit    ", report.creditTotal.toString())
                .total("   Check     ", report.checkTotal.toString())
                .total("   Giftcard  ", report.giftcardTotal.toString())
                .feed()
                .total("Tax Total", report.taxTotal.toString())
                .total("Charge Total", report.chargeTotal.toString())
                .feed()
                .total("Orders", report.orderCount)
                .total("   Dine In", report.dineInCount)
                .total("   Take Out", report.takeOutCount)
                .feed()
                .total("Taxed Sales Total", report.taxedSalesTotal.toString())
                .total("Untaxed Sales Total", report.untaxedSalesTotal.toString())
                .feed()
                .total("Item Sales Total", report.salesTotal.toString())
                .forEach(report.categorySalesTotals.entrySet(), item -> {

                })
                .font(EscPosBuilder.Font.REGULAR)
                .feed()
                .feed();
    }

    private TemplatePrintJobBuilder printMonthlySalesReport(MonthlySalesReport report) {
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

    /**
     * ***************************************************************
     * *
     * Sales Orders
     * *
     * ****************************************************************
     */

    private TemplatePrintJobBuilder salesOrderInfo(SalesOrder so) {
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

    private TemplatePrintJobBuilder salesOrderContent(SalesOrder so) {

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

    private TemplatePrintJobBuilder salesOrderTotals(SalesOrder so) {

        String untaxed = so.untaxedProductTotalProperty().get().toString();
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
                .optional(so.hasUntaxedEntry(), () -> total("Giftcard", untaxed))
                .font(EscPosBuilder.Font.EMPHASIZED)
                .total("Grand Total", grandtotal)
                .feed(1);
    }

    private TemplatePrintJobBuilder paymentEntryTotals(SalesOrder so) {

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

    /**
     * ***************************************************************
     * *
     * Convenience
     * *
     * ****************************************************************
     */

    private static byte[] KICK_CODE = new byte[]{(byte) 27, (byte) 112, (byte) 0, (byte) 25, (byte) 250};

    private interface Action {
        void invoke();
    }

    private interface Action1<T> {
        void invoke(T item);
    }

    private TemplatePrintJobBuilder optional(boolean condition, Action action) {
        if (condition)
            action.invoke();
        return this;
    }

    private TemplatePrintJobBuilder total(String desc, int total) {
        return total(desc, Integer.toString(total));
    }

    private TemplatePrintJobBuilder fill(char character) {

        StringBuilder sb = new StringBuilder(width);

        for (int i = 0; i < width; i++)
            sb.append(character);

        builder.text(sb.toString())
                .feed(1);

        return this;
    }

    private TemplatePrintJobBuilder total(String desc, String total) {

        int dWidth = width - total.length() - 1;

        builder.text(String.format("%-" + dWidth + "." + dWidth + "s", desc))
                .text(" ")
                .text(total)
                .feed(1);

        return this;
    }

    private TemplatePrintJobBuilder col3(String desc, String dat1, String dat2) {

        int cWidth = width / 3;

        builder.text(String.format("%-" + cWidth + "." + cWidth + "s", desc))
                .text(String.format("%" + cWidth + "s", dat1))
                .text(String.format("%" + cWidth + "s", dat2))
                .feed(1);

        return this;
    }

    private TemplatePrintJobBuilder text(String text) {
        builder.text(text);
        return this;
    }

    private TemplatePrintJobBuilder raw(byte[] raw) {
        builder.raw(raw);
        return this;
    }

    private TemplatePrintJobBuilder feed() {
        builder.feed(1);
        return this;
    }

    private TemplatePrintJobBuilder feed(int lines) {
        builder.feed(lines);
        return this;
    }

    private TemplatePrintJobBuilder font(EscPosBuilder.Font font) {
        builder.font(font);
        return this;
    }

    private TemplatePrintJobBuilder align(EscPosBuilder.Align align) {
        builder.align(align);
        return this;
    }

    private <T> TemplatePrintJobBuilder forEach(Collection<T> items, Action1<T> action) {
        items.forEach(action::invoke);
        return this;
    }

}
