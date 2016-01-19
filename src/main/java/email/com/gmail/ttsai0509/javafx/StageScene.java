package email.com.gmail.ttsai0509.javafx;

import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.Builder;
import org.comtel2000.keyboard.control.KeyBoardPopup;

public class StageScene {

    @Builder
    public static class Config {
        private EventHandler<WindowEvent> onClose = event -> {
        };
        private Modality modality = Modality.NONE;
        private StageStyle style = StageStyle.DECORATED;
        private Boolean maximized = false;
        private Boolean fullscreen = false;
        private Boolean alwaysOnTop = false;
        private Boolean resizable = false;
        private Double width = null;
        private Double height = null;

        private String rootId = null;

        private String css;
    }

    /******************************************************************
     *                                                                *
     * This should encapsulate the Stage/Scene API.                   *
     *                                                                *
     ******************************************************************/

    private Pane mPane;
    private Stage mStage;
    private Scene mScene;
    private Presenter presenter;

    public StageScene(Config config) {
        this(config, new Stage());
    }

    public StageScene(Config config, Stage stage) {
        mStage = stage;
        if (config.style != null) stage.initStyle(config.style);
        if (config.modality != null) stage.initModality(config.modality);
        if (config.onClose != null) stage.setOnCloseRequest(config.onClose);
        if (config.maximized != null) stage.setMaximized(config.maximized);
        if (config.fullscreen != null) stage.setFullScreen(config.fullscreen);
        if (config.alwaysOnTop != null) stage.setAlwaysOnTop(config.alwaysOnTop);
        if (config.resizable != null) stage.setResizable(config.resizable);
        if (config.width != null) stage.setWidth(config.width);
        if (config.height != null) stage.setHeight(config.height);

        mPane = new StackPane();
        if (config.rootId != null) mPane.setId(config.rootId);

        mScene = new Scene(mPane);
        if (config.css != null) mScene.getStylesheets().add(config.css);

        stage.setScene(mScene);
    }

    public StageScene(Config config, Stage stage, KeyBoardPopup keyboard) {
        this(config, stage);
        attach(keyboard);
    }

    public void set(Presenter presenter) {
        this.presenter = presenter;
        mPane.getChildren().setAll(presenter.getView());
    }

    public Presenter get() {
        return presenter;
    }

    public void show() {
        if (!mStage.isShowing())
            mStage.show();

        if (onShow != null)
            onShow.invoke();
    }

    public void hide() {
        if (mStage.isShowing())
            mStage.hide();

        if (onHide != null)
            onHide.invoke();
    }

    public void setShow(Presenter presenter) {
        set(presenter);
        show();
    }

    /******************************************************************
     *                                                                *
     * Event Handling
     *                                                                *
     ******************************************************************/

    public static interface Procedure {
        void invoke();
    }

    private Procedure onShow, onHide;

    public void setOnShow(Procedure onShow) {
        this.onShow = onShow;
    }

    public void setOnHide(Procedure onHide) {
        this.onHide = onHide;
    }

    /******************************************************************
     *                                                                *
     * Keyboard Support                                               *
     *                                                                *
     ******************************************************************/

    public void attach(KeyBoardPopup keyboard) {
        keyboard.registerScene(mScene);
        keyboard.setAutoHide(true);
        keyboard.setAutoFix(true);
        keyboard.addFocusListener(mScene);

        // Hack to remove focus from TextField on AutoHide
        keyboard.setOnAutoHide(event -> Platform.runLater(mPane::requestFocus));
    }

}
