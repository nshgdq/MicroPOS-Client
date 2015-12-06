package ow.micropos.client.desktop;


import email.com.gmail.ttsai0509.escpos.ESCPos;
import email.com.gmail.ttsai0509.escpos.PrintJob;
import email.com.gmail.ttsai0509.escpos.dispatcher.PrinterDispatcher;
import email.com.gmail.ttsai0509.escpos.dispatcher.PrinterDispatcherAsync;
import email.com.gmail.ttsai0509.escpos.printer.RawPrinter;
import email.com.gmail.ttsai0509.escpos.printer.TextPrinter;
import email.com.gmail.ttsai0509.utils.LoggerOutputStream;
import email.com.gmail.ttsai0509.utils.NullOutputStream;
import gnu.io.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.model.orders.SalesOrder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class AppPrinter {

    private static final Logger log = LoggerFactory.getLogger(AppPrinter.class);

    private final PrinterDispatcher dispatcher;
    private final AppCommander cmd;

    public AppPrinter(int width, String printersProperty) {

        cmd = new AppCommander(width);

        dispatcher = new PrinterDispatcherAsync();

        String[] printerConfigs = printersProperty.split(":");

        Map<String, OutputStream> deviceMap = new HashMap<>();
        deviceMap.put("CLI", System.out);
        deviceMap.put("LOG", new LoggerOutputStream(PrinterDispatcher.class, LoggerOutputStream.Level.INFO));
        deviceMap.put("NULL", new NullOutputStream());

        for (String printerConfig : printerConfigs) {

            String[] config = printerConfig.split(",");
            if (config.length < 2) {
                log.warn("Printer config missing parameters : " + printerConfig);
                continue;
            }

            String printerName = config[0];
            String printerDevice = config[1];

            if (!deviceMap.containsKey(printerDevice)) {
                try {
                    SerialPort sp = ESCPos.connectSerialPort(printerDevice);
                    deviceMap.put(printerDevice, sp.getOutputStream());

                } catch (Error e) {
                    log.error("Unable to load serial drivers. " + printerName + " using LOG instead.");
                    printerDevice = "LOG";

                } catch (Exception e) {
                    log.warn("Unable to open serial port " + printerDevice + ". " + printerName + " using LOG instead");
                    printerDevice = "LOG";

                }
            }

            if (printerDevice.equals("CLI"))
                dispatcher.registerPrinter(new TextPrinter(printerName, new PrintStream(deviceMap.get(printerDevice))));
            else
                dispatcher.registerPrinter(new RawPrinter(printerName, deviceMap.get(printerDevice)));
        }
    }

    public void start() {
        new Thread(dispatcher).start();
    }

    public void stop() {
        dispatcher.requestClose(true);
    }

    public void printCheck(SalesOrder so) {
        try {
            dispatch("CHECK",
                    cmd.begin()
                            .restaurant()
                            .address()
                            .salesOrderInfo(so)
                            .salesOrderContent(so)
                            .salesOrderTotals(so)
                            .thanks()
                            .footer()
            );
        } catch (IOException e) {
            dispatch(
                    "CHECK",
                    "Could not print order " + so.getId() + ". Contact Manager."
            );
        }
    }

    public void printReceipt(SalesOrder so) {
        try {
            dispatch("RECEIPT",
                    cmd.begin()
                            .restaurant()
                            .address()
                            .salesOrderInfo(so)
                            .salesOrderContent(so)
                            .salesOrderTotals(so)
                            .thanks()
                            .footer()
            );
        } catch (IOException e) {
            dispatch(
                    "RECEIPT",
                    "Could not print order " + so.getId() + ". Contact Manager."
            );
        }
    }

    private void dispatch(String target, AppCommander cmd) throws IOException {
        dispatcher.requestPrint(new PrintJob(target, cmd.end()));
    }

    private void dispatch(String target, String job) {
        dispatcher.requestPrint(new PrintJob(target, job.getBytes()));
    }

}
