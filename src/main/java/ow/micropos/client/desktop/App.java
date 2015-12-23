package ow.micropos.client.desktop;

import email.com.gmail.ttsai0509.escpos.com.ComUtils;
import email.com.gmail.ttsai0509.javafx.StageScene;
import email.com.gmail.ttsai0509.javafx.beans.DateTimeClock;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import email.com.gmail.ttsai0509.print.dispatcher.PrinterDispatcher;
import email.com.gmail.ttsai0509.print.dispatcher.PrinterDispatcherAsync;
import email.com.gmail.ttsai0509.print.printer.RawPrinter;
import email.com.gmail.ttsai0509.utils.LoggerOutputStream;
import email.com.gmail.ttsai0509.utils.NullOutputStream;
import email.com.gmail.ttsai0509.utils.PrinterConfig;
import email.com.gmail.ttsai0509.utils.TypedProperties;
import gnu.io.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.IOUtils;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.keyboard.control.VkProperties;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.common.DataConverter;
import ow.micropos.client.desktop.custom.PrintJobBuilder;
import ow.micropos.client.desktop.custom.wok.WokPrintJobBuilder;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.presenter.ConfirmPresenter;
import ow.micropos.client.desktop.presenter.MainPresenter;
import ow.micropos.client.desktop.presenter.NotifyPresenter;
import ow.micropos.client.desktop.presenter.database.*;
import ow.micropos.client.desktop.presenter.dinein.DineInPresenter;
import ow.micropos.client.desktop.presenter.finder.FinderPresenter;
import ow.micropos.client.desktop.presenter.login.LoginPresenter;
import ow.micropos.client.desktop.presenter.manager.ManagerPresenter;
import ow.micropos.client.desktop.presenter.modify.ProductEditorPresenter;
import ow.micropos.client.desktop.presenter.move.MovePresenter;
import ow.micropos.client.desktop.presenter.order.OrderEditorPresenter;
import ow.micropos.client.desktop.presenter.payment.PaymentEditorPresenter;
import ow.micropos.client.desktop.presenter.takeout.TakeOutPresenter;
import ow.micropos.client.desktop.presenter.target.TargetPresenter;
import ow.micropos.client.desktop.service.RestService;
import retrofit.RestAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class App extends Application implements VkProperties {

    private final Logger log = LoggerFactory.getLogger(App.class);

    // Properties
    public static TypedProperties properties;

    // Image
    private Image logo;

    // Employee Context
    public static Employee employee;

    // Application Services
    public static KeyBoardPopup keyboard;
    public static DateTimeClock clock;
    public static RestService api;
    public static AtomicBoolean apiIsBusy;
    public static PrinterDispatcher dispatcher;
    public static PrintJobBuilder jobBuilder;

    // Display Components
    public static StageScene primary, secondary;
    private StageScene.Config pConfig, sConfig;

    // StageScene Presenters
    public static MainPresenter main;
    public static NotifyPresenter notify;
    public static ConfirmPresenter confirm;

    // Content Presenter
    public static Presenter homePresenter;
    public static LoginPresenter loginPresenter;
    public static ManagerPresenter managerPresenter;
    public static DineInPresenter dineInPresenter;
    public static TakeOutPresenter takeOutPresenter;
    public static FinderPresenter finderPresenter;
    public static OrderEditorPresenter orderEditorPresenter;
    public static ProductEditorPresenter productEditorPresenter;
    public static PaymentEditorPresenter paymentEditorPresenter;
    public static TargetPresenter targetPresenter;
    public static MovePresenter movePresenter;

    // Database Presenters
    public static DbMenuItemsPresenter dbMenuItemsPresenter;
    public static DbCategoryPresenter dbCategoriesPresenter;
    public static DbSectionPresenter dbSectionPresenter;
    public static DbSeatPresenter dbSeatPresenter;
    public static DbModifierGroupPresenter dbModifierGroupPresenter;
    public static DbModifierPresenter dbModifierPresenter;
    public static DbChargePresenter dbChargePresenter;
    public static DbCustomerPresenter dbCustomerPresenter;
    public static DbSalesOrderPresenter dbSalesOrderPresenter;

    /******************************************************************
     *                                                                *
     * FatJar Run Command                                             *
     *      java -jar app.jar --url=http://xxx.xxx.xxx.xxx:xxxxx      *
     *                                                                *
     ******************************************************************/

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println(" java -jar app.jar --url=http://xxx.xxx.xxx.xxx:xxxxx");

        } else {
            App.launch(args);
        }

        System.exit(0);
    }

    public static void exit() {
        if (employee.hasPermission(Permission.CLOSE_CLIENT)) {
            dispatcher.requestClose(true);
            Platform.exit();
            System.exit(0);
        } else {
            App.notify.showAndWait("Permission Required [CLOSE_CLIENT]");
        }
    }

    /******************************************************************
     *                                                                *
     * Try to keep all configuration here.                            *
     *                                                                *
     ******************************************************************/

    @Override
    public void init() throws Exception {

        String server = getParameters().getNamed().get("url");
        File config = configProperties(System.getProperty("user.dir"));

        System.out.println("\n\nMicroPOS Client v1.0\n\n");

        logProperty("Java Version", System.getProperty("java.version"));
        logProperty("JavaFX Version", com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        logProperty("Working Directory", System.getProperty("user.dir"));
        logProperty("Config File", config.getCanonicalPath());
        logProperty("Server Url", server);

        for (String property : properties.getProperties())
            logProperty(property, properties.getStr(property));

        logo = new Image(properties.getStr("icon"));

        apiIsBusy = new AtomicBoolean(false);

        clock = new DateTimeClock();

        api = new RestAdapter.Builder()
                .setLog(log::info)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(DataConverter.jackson())
                .setEndpoint(server)
                .setRequestInterceptor(request -> {
                    if (employee != null)
                        request.addHeader(properties.getStr("header"), employee.getPin());
                })
                .build()
                .create(RestService.class);

        keyboard = KeyBoardPopupBuilder.create()
                .addIRobot(new FXRobotHandler())
                .initScale(properties.getDbl("keyboard-scale"))
                .initLocale(Locale.ENGLISH)
                .build();

        pConfig = StageScene.Config.builder()
                .css("/css/app.css")
                .maximized(false)
                .width(1000.0)
                .height(750.0)
                .onClose(event -> {})
                .modality(null)
                .style(StageStyle.DECORATED)
                .build();

        sConfig = StageScene.Config.builder()
                .rootId("root-pane")
                .css("/css/app.css")
                .maximized(false)
                .onClose(event -> secondary.hide())
                .modality(Modality.APPLICATION_MODAL)
                .style(StageStyle.UNDECORATED)
                .build();

    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.getIcons().add(logo);

        primary = new StageScene(pConfig, stage, keyboard);
        secondary = new StageScene(sConfig);

        main = Presenter.load("/view/main.fxml");
        notify = new NotifyPresenter();
        confirm = new ConfirmPresenter();

        loginPresenter = Presenter.load("/view/login/login.fxml");
        managerPresenter = Presenter.load("/view/manager/manager.fxml");
        dineInPresenter = Presenter.load("/view/dinein/dine_in.fxml");
        takeOutPresenter = Presenter.load("/view/takeout/take_out.fxml");
        finderPresenter = Presenter.load("/view/finder/finder.fxml");
        orderEditorPresenter = Presenter.load("/view/order/order_editor.fxml");
        productEditorPresenter = Presenter.load("/view/modify/product_editor.fxml");
        paymentEditorPresenter = Presenter.load("/view/pay/payment_editor.fxml");
        targetPresenter = Presenter.load("/view/target/target.fxml");
        movePresenter = Presenter.load("/view/move/move.fxml");

        dbMenuItemsPresenter = new DbMenuItemsPresenter();
        dbCategoriesPresenter = new DbCategoryPresenter();
        dbModifierGroupPresenter = new DbModifierGroupPresenter();
        dbModifierPresenter = new DbModifierPresenter();
        dbSeatPresenter = new DbSeatPresenter();
        dbSectionPresenter = new DbSectionPresenter();
        dbChargePresenter = new DbChargePresenter();
        dbCustomerPresenter = new DbCustomerPresenter();
        dbSalesOrderPresenter = new DbSalesOrderPresenter();

        homePresenter = getHomePresenter(properties.getStr("home"));

        jobBuilder = new WokPrintJobBuilder(42);
        dispatcher = initPrinterDispatcher(properties);

        clock.start();
        primary.setShow(main);
        main.next(loginPresenter);
    }

    private File configProperties(String pathname) {
        File userFile = new File(pathname + "/app.properties");

        // Load all custom properties
        if (userFile.isFile())
            properties = TypedProperties.fromFile(userFile);
        else
            properties = new TypedProperties();

        // Add all absent properties
        TypedProperties defaultProperties = TypedProperties.fromResource("default.properties");
        defaultProperties.getProperties()
                .stream()
                .filter(property -> !properties.has(property))
                .forEach(property -> properties.add(property, defaultProperties.getStr(property)));

        // Export all loaded properties
        properties.save(userFile);

        // Copy default properties as sample
        try {
            IOUtils.copy(
                    App.class.getResourceAsStream("/default.properties"),
                    new FileOutputStream(pathname + "/default.properties")
            );
        } catch (IOException e) {
            log.info("Unable to copy default.properties");
        }

        return userFile;
    }

    private Presenter getHomePresenter(String val) {
        switch (val) {
            case "dine-in":
                return dineInPresenter;
            case "take-out":
                return takeOutPresenter;
            case "manager":
                return managerPresenter;
            case "finder":
            default:
                return finderPresenter;
        }
    }

    private void logProperty(String key, String val) {
        if (key.length() > 30)
            key = key.substring(0, 27) + "...";
        System.out.println(String.format("%-30s%s", key, val));
    }

    private PrinterDispatcher initPrinterDispatcher(TypedProperties properties) {

        PrinterDispatcher dispatcher = new PrinterDispatcherAsync();

        // Get Printer Configurations
        int count = properties.getInt("printers");
        PrinterConfig[] printerConfigs = new PrinterConfig[count];
        for (int i = 0; i < count; i++)
            printerConfigs[i] = PrinterConfig.fromProperties(properties, "printer" + i);

        new Thread(() -> {

            Map<String, OutputStream> deviceMap = new HashMap<>();

            // Load default devices
            deviceMap.put("CLI", System.out);
            deviceMap.put("LOG", new LoggerOutputStream(PrinterDispatcher.class, LoggerOutputStream.Level.INFO));
            deviceMap.put("NULL", new NullOutputStream());

            // Load config devices
            for (PrinterConfig printerConfig : printerConfigs) {

                // Open SerialPort if it's not opened yet, and we are able to.
                if (!deviceMap.containsKey(printerConfig.device) && printerConfig.serialConfig != null) {
                    try {
                        SerialPort sp = ComUtils.connectSerialPort(printerConfig.device, 2000, printerConfig
                                .serialConfig);
                        deviceMap.put(printerConfig.device, sp.getOutputStream());

                    } catch (Error e) {
                        log.error("Check serial drivers. " + printerConfig.name + " using LOG.");

                    } catch (Exception e) {
                        log.warn("Check serial port " + printerConfig.device + ". " + printerConfig.name + " using " +
                                "LOG");

                    }
                }

                OutputStream device = deviceMap.get(printerConfig.device);

                // Device issues default to the Logger
                if (device == null)
                    dispatcher.registerPrinter(printerConfig.name, new RawPrinter(deviceMap.get("LOG")));
                else
                    dispatcher.registerPrinter(printerConfig.name, new RawPrinter(device));

            }

            // Dispatcher runs on same thread as opened OutputStreams
            dispatcher.run();

        }).start();

        return dispatcher;

    }

}
