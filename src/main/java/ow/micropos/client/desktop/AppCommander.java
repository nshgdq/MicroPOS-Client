package ow.micropos.client.desktop;

import email.com.gmail.ttsai0509.escpos.ESCPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.model.menu.Modifier;
import ow.micropos.client.desktop.model.orders.ProductEntry;
import ow.micropos.client.desktop.model.orders.SalesOrder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppCommander {

    private static final Logger log = LoggerFactory.getLogger(AppCommander.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");

    private final int width;
    private final ByteArrayOutputStream out;
    private final ESCPos.Commander commander;

    public AppCommander(int width) {
        this.width = width;
        this.out = new ByteArrayOutputStream();
        this.commander = new ESCPos.Commander(out);
    }

    public AppCommander print(int val) throws IOException {
        commander.print(val);
        return this;
    }

    public AppCommander print(String text) throws IOException {
        commander.print(text);
        return this;
    }

    public AppCommander print(byte... cmd) throws IOException {
        commander.print(cmd);
        return this;
    }

    public AppCommander begin() throws IOException {
        out.reset();
        return print(ESCPos.INITIALIZE);
    }

    public AppCommander restaurant() throws IOException {
        return print(ESCPos.ALIGN_CENTER)
                .print(ESCPos.FONT_DH_EMPH)
                .print("ORIENTAL WOK")
                .print(ESCPos.FEED_N)
                .print(2);
    }

    public AppCommander address() throws IOException {
        return print(ESCPos.ALIGN_CENTER)
                .print(ESCPos.FONT_REG)
                .print("6 NORTH BOLTON AVE.")
                .print(ESCPos.FEED)
                .print("ALEXANDRIA, LA 71301")
                .print(ESCPos.FEED)
                .print("(318) 448-8247")
                .print(ESCPos.FEED_N)
                .print(2);
    }

    public AppCommander salesOrderInfo(SalesOrder so) throws IOException {

        String datetime = dateFormat.format(so.getDate());
        String employee = so.getEmployee().getFirstName();
        String orderNumber = so.getId().toString();

        return print(ESCPos.ALIGN_CENTER)
                .print(ESCPos.FONT_DWDH)
                .print("Order " + orderNumber)
                .print(ESCPos.FEED_N)
                .print(2)
                .print(ESCPos.FONT_REG)
                .print(ESCPos.ALIGN_LEFT)
                .print("Date     : " + datetime)
                .print(ESCPos.FEED)
                .print("Employee : " + employee)
                .print(ESCPos.FEED_N)
                .print(2);
    }

    public AppCommander salesOrderContent(SalesOrder so) throws IOException {

        print(ESCPos.ALIGN_LEFT);

        for (ProductEntry pe : so.getProductEntries()) {

            String item = pe.getMenuItem().getName();

            String tag = pe.getMenuItem().getTag();

            String quantity;
            if (isIntegerValue(pe.getQuantity()))
                quantity = pe.getQuantity().setScale(0, BigDecimal.ROUND_UNNECESSARY).toString();
            else
                quantity = pe.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString();

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
                    log.warn("Skipping unexpected product entry " + pe.toString());
                    return this;
            }

            String price = pe.totalProperty().get().toString();

            String desc = quantity + " " + tag + " " + item;
            int descWidth = width - price.length() - status.length() - 1;
            desc = String.format("%-" + descWidth + "." + descWidth + "s ", desc);

            print(ESCPos.FONT_EMPH)
                    .print(status)
                    .print(ESCPos.FONT_REG)
                    .print(desc)
                    .print(price)
                    .print(ESCPos.FEED);

            for (Modifier mod : pe.getModifiers())
                total("        " + mod.getName(), mod.getPrice() + "        ");
        }

        return print(ESCPos.FEED_N).print(2);
    }

    public AppCommander salesOrderTotals(SalesOrder so) throws IOException {

        String subtotal = so.productTotalProperty().get().toString();
        String discounts = so.chargeTotalProperty().get().toString();
        String gratuity = so.gratuityTotalProperty().get().toString();
        String taxtotal = so.taxTotalProperty().get().toString();
        String grandtotal = so.grandTotalProperty().get().toString();

        print(ESCPos.ALIGN_LEFT)
                .print(ESCPos.FONT_REG);

        total("Subtotal", subtotal);

        if (!so.getChargeEntries().isEmpty())
            total("Discounts", discounts);

        if (so.hasGratuity())
            total("Gratuity (" + so.gratuityTextProperty().get() + ")", gratuity);

        total("Sales Tax (" + so.taxTextProperty().get() + ")", taxtotal);

        print(ESCPos.FONT_EMPH)
                .total("Grand Total", grandtotal);

        return print(ESCPos.FEED_N).print(2);
    }

    public AppCommander thanks() throws IOException {
        return print(ESCPos.ALIGN_CENTER)
                .print(ESCPos.FONT_REG)
                .print("We appreciate your business!")
                .print(ESCPos.FEED_N)
                .print(2);
    }

    public AppCommander footer() throws IOException {
        return print(ESCPos.ALIGN_CENTER)
                .print(ESCPos.FONT_REG)
                .print(Long.toString(new Date().getTime()))
                .print(ESCPos.FEED_N)
                .print(2);
    }

    public byte[] end() throws IOException {

        print(ESCPos.FEED_N)
                .print(4)
                .print(ESCPos.PART_CUT);

        byte[] result = out.toByteArray();
        out.reset();

        return result;
    }

    private AppCommander total(String desc, String total) throws IOException {

        int dWidth = width - total.length() - 1;

        return print(String.format("%-" + dWidth + "." + dWidth + "s", desc))
                .print(" ")
                .print(total)
                .print(ESCPos.FEED);
    }

    private boolean isIntegerValue(BigDecimal bd) {
        return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
    }

}
