package ow.micropos.client.desktop.common;

import email.com.gmail.ttsai0509.print.printer.PrintJob;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.MonthlySalesReport;
import ow.micropos.client.desktop.model.report.SalesReport;

public interface PrintJobBuilder {

    PrintJob check(SalesOrder so, boolean kick);

    PrintJob salesReport(SalesReport report);

    PrintJob monthlySalesReport(MonthlySalesReport report);

}
