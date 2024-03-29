package ow.micropos.client.desktop;

import email.com.gmail.ttsai0509.javafx.StageScene;
import email.com.gmail.ttsai0509.javafx.beans.DateTimeClock;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import email.com.gmail.ttsai0509.print.dispatcher.PrinterDispatcher;
import ow.micropos.client.desktop.common.*;
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
import ow.micropos.client.desktop.misc.DataConverter;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.Permission;
import ow.micropos.client.desktop.presenter.ConfirmPresenter;
import ow.micropos.client.desktop.presenter.MainPresenter;
import ow.micropos.client.desktop.presenter.NotifyPresenter;
import ow.micropos.client.desktop.presenter.change.ChangeDuePresenter;
import ow.micropos.client.desktop.presenter.database.*;
import ow.micropos.client.desktop.presenter.dinein.DineInPresenter;
import ow.micropos.client.desktop.presenter.dinein.DineInPromptPresenter;
import ow.micropos.client.desktop.presenter.finder.FinderPresenter;
import ow.micropos.client.desktop.presenter.login.LoginPresenter;
import ow.micropos.client.desktop.presenter.manager.ManagerPresenter;
import ow.micropos.client.desktop.presenter.modify.ProductEditorPresenter;
import ow.micropos.client.desktop.presenter.move.MovePresenter;
import ow.micropos.client.desktop.presenter.order.OrderEditorPresenter;
import ow.micropos.client.desktop.presenter.report.ReportPresenter;
import ow.micropos.client.desktop.presenter.takeout.TakeOutPresenter;
import ow.micropos.client.desktop.presenter.target.TargetPresenter;
import ow.micropos.client.desktop.service.RestService;
import ow.micropos.client.desktop.service.RestServiceProxy;
import retrofit.RestAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
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
    public static PrinterDispatcher dispatcher;
    public static PrintJobBuilder jobBuilder;
    public static RestServiceProxy apiProxy;
    public static AtomicBoolean apiIsBusy;

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
    public static DineInPromptPresenter dineInPromptPresenter;
    public static TakeOutPresenter takeOutPresenter;
    public static FinderPresenter finderPresenter;
    public static OrderEditorPresenter orderEditorPresenter;
    public static ProductEditorPresenter productEditorPresenter;
    public static TargetPresenter targetPresenter;
    public static MovePresenter movePresenter;
    public static ChangeDuePresenter changeDuePresenter;

    // Database Presenters
    public static DbEmployeePresenter dbEmployeePresenter;
    public static DbPositionPresenter dbPositionPresenter;
    public static DbMenuItemsPresenter dbMenuItemsPresenter;
    public static DbCategoryPresenter dbCategoriesPresenter;
    public static DbSectionPresenter dbSectionPresenter;
    public static DbSeatPresenter dbSeatPresenter;
    public static DbModifierGroupPresenter dbModifierGroupPresenter;
    public static DbModifierPresenter dbModifierPresenter;
    public static DbChargePresenter dbChargePresenter;
    public static DbCustomerPresenter dbCustomerPresenter;
    public static DbSalesOrderPresenter dbSalesOrderPresenter;
    public static DbPropertyPresenter dbPropertyPresenter;
    public static DbTimeCardPresenter dbTimeCardPresenter;

    // Report Presenters
    public static ReportPresenter reportPresenter;


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
        if (employee.hasPermission(Permission.CLIENT_CLOSE)) {
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

        // http://stackoverflow.com/questions/31786980/javafx-windows-10-combobox-error
        System.setProperty("glass.accessible.force", "false");

        logo = new Image(properties.getStr("icon"));

        apiIsBusy = new AtomicBoolean(false);

        clock = new DateTimeClock();

        apiProxy = RestServiceProxy.from(new RestAdapter.Builder()
                .setLog(log::info)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(DataConverter.jackson())
                .setEndpoint(server)
                .setRequestInterceptor(request -> {
                    if (employee != null)
                        request.addHeader(properties.getStr("header"), employee.getPin());
                })
                .build()
                .create(RestService.class));

        keyboard = KeyBoardPopupBuilder.create()
                .addIRobot(new FXRobotHandler())
                .initScale(properties.getDbl("keyboard-scale"))
                .initLocale(Locale.ENGLISH)
                .build();

        pConfig = StageScene.Config.builder()
                .css("/css/app.css")
                .maximized(true)
                .onClose(event -> {})
                .modality(null)
                .style(StageStyle.UNDECORATED)
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

        if (properties.getBool("virtual-keyboard"))
            primary = new StageScene(pConfig, stage, keyboard);
        else
            primary = new StageScene(pConfig, stage);

        secondary = new StageScene(sConfig);

        main = Presenter.load("/view/main.fxml");
        notify = new NotifyPresenter();
        confirm = new ConfirmPresenter();

        loginPresenter = Presenter.load("/view/login/login.fxml");
        managerPresenter = Presenter.load("/view/manager/manager.fxml");
        dineInPresenter = Presenter.load("/view/dinein/dine_in.fxml");
        dineInPromptPresenter = Presenter.load("/view/dinein/dine_in_prompt.fxml");
        takeOutPresenter = Presenter.load("/view/takeout/take_out.fxml");
        finderPresenter = Presenter.load("/view/finder/finder.fxml");
        orderEditorPresenter = Presenter.load("/view/order/order_editor.fxml");
        productEditorPresenter = Presenter.load("/view/modify/product_editor.fxml");
        targetPresenter = Presenter.load("/view/target/target.fxml");
        movePresenter = Presenter.load("/view/move/move.fxml");
        changeDuePresenter = Presenter.load("/view/change/change_due.fxml");

        dbEmployeePresenter = new DbEmployeePresenter();
        dbPositionPresenter = new DbPositionPresenter();
        dbMenuItemsPresenter = new DbMenuItemsPresenter();
        dbCategoriesPresenter = new DbCategoryPresenter();
        dbModifierGroupPresenter = new DbModifierGroupPresenter();
        dbModifierPresenter = new DbModifierPresenter();
        dbSeatPresenter = new DbSeatPresenter();
        dbSectionPresenter = new DbSectionPresenter();
        dbChargePresenter = new DbChargePresenter();
        dbCustomerPresenter = new DbCustomerPresenter();
        dbSalesOrderPresenter = new DbSalesOrderPresenter();
        dbPropertyPresenter = new DbPropertyPresenter();
        dbTimeCardPresenter = new DbTimeCardPresenter();

        reportPresenter = Presenter.load("/view/report/report.fxml");

        homePresenter = getHomePresenter(properties.getStr("home"));

        //jobBuilder = new WokPrintJobBuilder(42);
        jobBuilder = new TemplatePrintJobBuilder(
                properties.getStr("r-name"),
                properties.getStr("r-address1"),
                properties.getStr("r-address2"),
                properties.getStr("r-phone"),
                properties.getStr("r-thanks"),
                42
        );
        dispatcher = PrinterDispatcherFactory.transientConnectionDispatcher(properties);

        clock.start();
        primary.setShow(main);
        main.next(loginPresenter);
    }

    private File configProperties(String pathname) {
        File userFile = new File(pathname + "/app.properties");

        // Load all print properties
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

}
