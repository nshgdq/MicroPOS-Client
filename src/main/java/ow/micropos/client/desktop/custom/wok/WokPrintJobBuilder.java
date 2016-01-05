package ow.micropos.client.desktop.custom.wok;

import email.com.gmail.ttsai0509.escpos.EscPosBuilder;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import email.com.gmail.ttsai0509.print.printer.PrintJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.custom.PrintJobBuilder;
import ow.micropos.client.desktop.model.enums.PaymentEntryStatus;
import ow.micropos.client.desktop.model.enums.ProductEntryStatus;
import ow.micropos.client.desktop.model.orders.PaymentEntry;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.ActiveSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;
import ow.micropos.client.desktop.model.report.Summary;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class WokPrintJobBuilder implements PrintJobBuilder {

    private static final Logger log = LoggerFactory.getLogger(WokPrintJobBuilder.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");

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
    public PrintJob report(ActiveSalesReport report) {
        return begin()
                .restaurant()
                .address()
                .activeReport(report)
                .finish();
    }

    @Override
    public PrintJob report(DaySalesReport report) {
        return begin()
                .restaurant()
                .address()
                .dayReport(report)
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

    private WokPrintJobBuilder activeReport(ActiveSalesReport report) {
        return text(dateFormat.format(new Date()))
                .feed(1)
                .summary("CLOSED", report.closedSummary)
                .feed(1)
                .summary("OPEN", report.openSummary)
                .feed(1)
                .summary("VOID", report.voidSummary);
    }

    private WokPrintJobBuilder dayReport(DaySalesReport report) {
        return text(dateFormat.format(report.start))
                .text(" - ")
                .text(dateFormat.format(report.end))
                .feed(1)
                .summary("CLOSED", report.closedSummary)
                .feed(1)
                .summary("OPEN", report.openSummary)
                .feed(1)
                .summary("VOID", report.voidSummary);
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

        String datetime = dateFormat.format(so.getDate());
        String employee = so.getEmployee().getFirstName();
        String orderNumber = so.getId().toString();

        return align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.DWDH)
                .text("Order " + orderNumber)
                .feed(2)
                .align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.REGULAR)
                .text("Date     : " + datetime)
                .feed(1)
                .text("Employee : " + employee)
                .feed(2);
    }

    private WokPrintJobBuilder salesOrderContent(SalesOrder so) {

        // Skip void items (Requests should have already been sent)
        List<ProductEntry> active = so.getProductEntries().filtered(
                pe -> pe.hasStatus(ProductEntryStatus.SENT) || pe.hasStatus(ProductEntryStatus.HOLD)
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
                .feed(2);
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
                .feed(2);
    }

    private WokPrintJobBuilder paymentEntryTotals(SalesOrder so) {

        List<PaymentEntry> active = so.getPaymentEntries().filtered(pe -> pe.hasStatus(PaymentEntryStatus.PAID));

        return optional(!active.isEmpty(),
                () -> align(EscPosBuilder.Align.LEFT)
                        .font(EscPosBuilder.Font.REGULAR)
                        .forEach(active, pe -> total(pe.getType().name(), pe.getAmount().toString()))
                        .font(EscPosBuilder.Font.EMPHASIZED)
                        .total("Change Due", so.changeProperty().get().toString())
                        .font(EscPosBuilder.Font.REGULAR)
                        .feed(2));
    }

    private WokPrintJobBuilder summary(String title, Summary summary) {
        return align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.EMPHASIZED)
                .text(title + " (" + summary.dineInCount + " Dine In, " + summary.takeOutCount + " Take Out)")
                .font(EscPosBuilder.Font.REGULAR)
                .feed(1)
                .total("Product Total (" + summary.productCount + ")", summary.productTotal.toString())
                .total("Charge Total (" + summary.chargeCount + ")", summary.chargeTotal.toString())
                .total("Sub Total", summary.subTotal.toString())
                .total("Tax Total", summary.taxTotal.toString())
                .font(EscPosBuilder.Font.EMPHASIZED)
                .total("Sales Total", summary.grandTotal.subtract(summary.gratuityTotal).toString())
                .font(EscPosBuilder.Font.REGULAR);
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
