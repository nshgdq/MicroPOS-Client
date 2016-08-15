package ow.micropos.client.desktop.misc;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import lombok.Data;

@Data
public class Action {

    private String title;
    private String cssId;
    private EventHandler<MouseEvent> action;

    public Action(String title, EventHandler<MouseEvent> action) {
        this.title = title;
        this.action = action;
    }

    public Action(String title, String cssId, EventHandler<MouseEvent> action) {
        this(title, action);
        this.cssId = cssId;
    }

    public Action(String title, ActionType type, EventHandler<MouseEvent> action) {
        this(title, action);
        this.cssId = type.getCssId();
    }

}
