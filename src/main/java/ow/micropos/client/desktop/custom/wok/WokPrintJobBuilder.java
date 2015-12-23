package ow.micropos.client.desktop.custom.wok;

import email.com.gmail.ttsai0509.escpos.EscPosBuilder;
import email.com.gmail.ttsai0509.math.BigDecimalUtils;
import email.com.gmail.ttsai0509.print.printer.PrintJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.custom.PrintJobBuilder;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.CurrentSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        begin()
                .restaurant()
                .address()
                .salesOrderInfo(so)
                .salesOrderContent(so)
                .salesOrderTotals(so)
                .thanks()
                .footer();

        return new PrintJob(builder.getBytes(), true);
    }

    @Override
    public PrintJob report(CurrentSalesReport report) {
        begin()
                .restaurant()
                .address()
                .currentReport(report)
                .footer();

        return new PrintJob(builder.getBytes(), true);
    }

    @Override
    public PrintJob report(DaySalesReport report) {
        begin()
                .restaurant()
                .address()
                .dayReport(report)
                .footer();

        return new PrintJob(builder.getBytes(), true);
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

    private WokPrintJobBuilder footer() {
        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.REGULAR)
                .text(Long.toString(new Date().getTime()))
                .feed(2);
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

    private WokPrintJobBuilder currentReport(CurrentSalesReport report) {
        return text(dateFormat.format(new Date()))
                .feed(2)
                .total("Product Total", report.getProductTotal().toString())
                .total("Charge Total", report.getChargeTotal().toString())
                .total("Charge Count", report.getChargeCount())
                .total("Sub Total", report.getSubTotal().toString())
                .total("Tax Total", report.getTaxTotal().toString())
                .total("Gratuity Total", report.getGratuityTotal().toString())
                .total("Grand Total", report.getGrandTotal().toString())
                .total("Payment Total", report.getPaymentTotal().toString())
                .total("Payment Count", report.getPaymentCount())
                .total("Change Total", report.getChangeTotal().toString())
                .total("Net Sales", report.getNetSales().toString())
                .feed(2)
                .total("Order Count", report.orderCount)
                .total("   Open", report.openCount)
                .total("   Closed", report.closedCount)
                .total("   Void", report.voidCount)
                .total("   Dine In", report.dineInCount)
                .total("   Take Out", report.takeOutCount)
                .feed(2)
                .total("Product Count", report.getProductCount())
                .total("   Void", report.productVoidCount);
    }

    private WokPrintJobBuilder dayReport(DaySalesReport report) {
        return text(dateFormat.format(report.start))
                .text(" - ")
                .text(dateFormat.format(report.end))
                .feed(2)
                .total("Product Total", report.getProductTotal().toString())
                .total("Charge Total", report.getChargeTotal().toString())
                .total("Charge Count", report.getChargeCount())
                .total("Sub Total", report.getSubTotal().toString())
                .total("Tax Total", report.getTaxTotal().toString())
                .total("Gratuity Total", report.getGratuityTotal().toString())
                .total("Grand Total", report.getGrandTotal().toString())
                .total("Payment Total", report.getPaymentTotal().toString())
                .total("Payment Count", report.getPaymentCount())
                .total("Change Total", report.getChangeTotal().toString())
                .total("Net Sales", report.getNetSales().toString())
                .feed(2)
                .total("Order Count", report.orderCount)
                .total("   Open", report.openCount)
                .total("   Closed", report.closedCount)
                .total("   Void", report.voidCount)
                .total("   Dine In", report.dineInCount)
                .total("   Take Out", report.takeOutCount)
                .feed(2)
                .total("Product Count", report.getProductCount())
                .total("   Void", report.productVoidCount);
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

        builder.align(EscPosBuilder.Align.CENTER)
                .font(EscPosBuilder.Font.DWDH)
                .text("Order " + orderNumber)
                .feed(2)
                .align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.REGULAR)
                .text("Date     : " + datetime)
                .feed(1)
                .text("Employee : " + employee)
                .feed(2);
        return this;
    }

    private WokPrintJobBuilder salesOrderContent(SalesOrder so) {

        builder.align(EscPosBuilder.Align.LEFT);

        for (ProductEntry pe : so.getProductEntries()) {

            String item = pe.getMenuItem().getName();

            String tag = pe.getMenuItem().getTag();

            String quantity = BigDecimalUtils.asQuantity(pe.getQuantity()).toString();

            String status;
            switch (pe.getStatus()) {
                case SENT:
                    status = "     ";
                    break;
                case VOID:
                    status = "VOID ";
                    break;
                case HOLD:
                    status = "HOLD ";
                    break;
                default:
                    // TODO : Should probably throw Exception here instead
                    log.warn("Skipping unexpected product entry " + pe.toString());
                    return this;
            }

            String price = pe.totalProperty().get().toString();

            String desc = quantity + " " + tag + " " + item;
            int descWidth = width - price.length() - status.length() - 1;
            desc = String.format("%-" + descWidth + "." + descWidth + "s ", desc);

            builder.font(EscPosBuilder.Font.EMPHASIZED)
                    .text(status)
                    .font(EscPosBuilder.Font.REGULAR)
                    .text(desc)
                    .text(price)
                    .feed(1);

            for (Modifier mod : pe.getModifiers())
                total("        " + mod.getTag() + " " + mod.getName(), mod.getPrice() + "        ");
        }

        builder.feed(2);

        return this;
    }

    private WokPrintJobBuilder salesOrderTotals(SalesOrder so) {

        String subtotal = so.productTotalProperty().get().toString();
        String discounts = so.chargeTotalProperty().get().toString();
        String gratuity = so.gratuityTotalProperty().get().toString();
        String taxtotal = so.taxTotalProperty().get().toString();
        String grandtotal = so.grandTotalProperty().get().toString();

        builder.align(EscPosBuilder.Align.LEFT)
                .font(EscPosBuilder.Font.REGULAR);

        total("Subtotal", subtotal);

        if (so.hasAppliedCharge())
            total("Discounts", discounts);

        if (so.hasGratuity())
            total("Gratuity (" + so.gratuityTextProperty().get() + ")", gratuity);

        total("Sales Tax (" + so.taxTextProperty().get() + ")", taxtotal);

        builder.font(EscPosBuilder.Font.EMPHASIZED);

        total("Grand Total", grandtotal);

        builder.feed(2);

        return this;
    }

    /******************************************************************
     *                                                                *
     * Convenience
     *                                                                *
     ******************************************************************/

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

}
