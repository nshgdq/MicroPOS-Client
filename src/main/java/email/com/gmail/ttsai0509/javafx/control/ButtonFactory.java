package email.com.gmail.ttsai0509.javafx.control;


import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ButtonFactory {

    private ButtonFactory() {
    }

    public static interface Procedure {
        void invoke();
    }

    @Builder
    public static class Config {
        private String text = null;
        private StringProperty textProperty = null;
        private EventHandler<ActionEvent> onButton = null;
        private EventHandler<MouseEvent> onWrapper = null;
        private Procedure onAny = null;
        private String wrapperId = null;

        public Button create() {
            return ButtonFactory.create(this);
        }

        public StackPane wrapCreate() {
            return ButtonFactory.wrapCreate(this);
        }
    }

    public static Button create(Config config) {
        Button button = new Button();

        if (config.text != null) button.setText(config.text);
        if (config.textProperty != null) button.textProperty().bind(config.textProperty);
        if (config.onButton != null) button.setOnAction(config.onButton);
        if (config.onAny != null) button.setOnAction(event -> config.onAny.invoke());

        return button;
    }

    public static StackPane wrapCreate(Config config) {
        StackPane sp = new StackPane(create(config));

        if (config.wrapperId != null) sp.setId(config.wrapperId);
        if (config.onWrapper != null) sp.setOnMouseClicked(config.onWrapper);
        if (config.onAny != null) sp.setOnMouseClicked(event -> config.onAny.invoke());

        return sp;
    }

    public static List<Node> wrapCreates(Config... configs) {
        return Arrays.asList(configs)
                .stream()
                .map(ButtonFactory::wrapCreate)
                .collect(Collectors.toList());
    }

}
