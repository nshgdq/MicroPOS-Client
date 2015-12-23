package ow.micropos.client.desktop.custom;

import email.com.gmail.ttsai0509.print.printer.PrintJob;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.CurrentSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;

public interface PrintJobBuilder {

    PrintJob check(SalesOrder so);

    PrintJob report(CurrentSalesReport report);

    PrintJob report(DaySalesReport report);

}
