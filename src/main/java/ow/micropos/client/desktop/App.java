package ow.micropos.client.desktop;

import email.com.gmail.ttsai0509.javafx.StageScene;
import email.com.gmail.ttsai0509.javafx.beans.DateTimeClock;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.keyboard.control.VkProperties;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ow.micropos.client.desktop.model.people.Employee;
import ow.micropos.client.desktop.presenter.ErrorPresenter;
import ow.micropos.client.desktop.presenter.MainPresenter;
import ow.micropos.client.desktop.presenter.PopupPresenter;
import ow.micropos.client.desktop.presenter.WarnPresenter;
import ow.micropos.client.desktop.presenter.dinein.DineInPresenter;
import ow.micropos.client.desktop.presenter.finder.FinderPresenter;
import ow.micropos.client.desktop.presenter.login.LoginPresenter;
import ow.micropos.client.desktop.presenter.manager.ManagerPresenter;
import ow.micropos.client.desktop.presenter.modify.ProductEditorPresenter;
import ow.micropos.client.desktop.presenter.move.MovePresenter;
import ow.micropos.client.desktop.presenter.order.OrderEditorPresenter;
import ow.micropos.client.desktop.presenter.payment.PaymentEditorPresenter;
import ow.micropos.client.desktop.presenter.seat.SeatPresenter;
import ow.micropos.client.desktop.presenter.takeout.TakeOutPresenter;
import ow.micropos.client.desktop.utils.DataConverter;
import retrofit.RestAdapter;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class App extends Application implements VkProperties {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Properties
    public static AppProperties properties;

    // Image
    private Image logo;

    // Employee Context
    public static Employee employee;

    // Application Services
    public static AtomicBoolean apiIsBusy;
    public static AppApi api;
    public static KeyBoardPopup keyboard;
    public static DateTimeClock clock;

    // Display Components
    public static StageScene primary, secondary;
    private StageScene.Config pConfig, sConfig;

    // StageScene Presenters
    public static MainPresenter main;
    public static PopupPresenter popup;
    public static WarnPresenter warn;
    public static ErrorPresenter error;

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
    public static SeatPresenter seatPresenter;
    public static MovePresenter movePresenter;

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
        Platform.exit();
        System.exit(0);
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

        logo = new Image(properties.getStr("logo"));

        api = new RestAdapter.Builder()
                .setLog(logger::info)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(DataConverter.jackson())
                .setEndpoint(server)
                .setRequestInterceptor(request -> {
                    if (employee != null)
                        request.addHeader(properties.getStr("header"), String.valueOf(employee.getPin()));
                })
                .build()
                .create(AppApi.class);

        keyboard = KeyBoardPopupBuilder.create()
                .addIRobot(new FXRobotHandler())
                .initScale(properties.getDbl("keyboard-scale"))
                .initLocale(Locale.ENGLISH)
                .build();

        pConfig = StageScene.Config.builder()
                .css("/css/app.css")
                .maximized(true)
                .onClose(event -> exit())
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

        apiIsBusy = new AtomicBoolean(false);

        clock = new DateTimeClock();

        primary = new StageScene(pConfig, stage, keyboard);
        secondary = new StageScene(sConfig);

        main = Presenter.load("/view/main.fxml");
        warn = new WarnPresenter();
        error = new ErrorPresenter();

        loginPresenter = Presenter.load("/view/login/login.fxml");
        managerPresenter = Presenter.load("/view/manager/manager.fxml");
        dineInPresenter = Presenter.load("/view/dinein/dine_in.fxml");
        takeOutPresenter = Presenter.load("/view/takeout/take_out.fxml");
        finderPresenter = Presenter.load("/view/finder/finder.fxml");
        orderEditorPresenter = Presenter.load("/view/order/order_editor.fxml");
        productEditorPresenter = Presenter.load("/view/modify/product_editor.fxml");
        paymentEditorPresenter = Presenter.load("/view/pay/payment_editor.fxml");
        seatPresenter = Presenter.load("/view/seat/seat.fxml");
        movePresenter = Presenter.load("/view/move/move.fxml");

        homePresenter = getHomePresenter(properties.getStr("home"));

        clock.start();
        primary.setShow(main);
        main.next(loginPresenter);
    }

    private File configProperties(String pathname) {
        File file = new File(pathname + "/app.properties");

        // Load all custom properties
        if (file.isFile())
            properties = AppProperties.fromFile(file);
        else
            properties = new AppProperties();

        // Add all absent properties
        AppProperties defaultProperties = AppProperties.fromResource("default.properties");
        defaultProperties.getProperties()
                .stream()
                .filter(property -> !properties.has(property))
                .forEach(property -> properties.add(property, defaultProperties.getStr(property)));

        // Export all loaded properties
        properties.save(file);
        return file;
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

}
