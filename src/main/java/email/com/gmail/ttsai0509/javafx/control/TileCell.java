package email.com.gmail.ttsai0509.javafx.control;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class TileCell<T> {

    protected T item;
    protected Node graphic;
    protected EventHandler<MouseEvent> event;

    public void updateItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setGraphic(Node graphic) {
        // Unbind EventHandler on old graphic.
        if (graphic != null)
            graphic.setOnMouseClicked(null);

        this.graphic = graphic;

        // Bind EventHandler to new graphic.
        if (graphic != null)
            graphic.setOnMouseClicked(event);
    }

    public Node getGraphic() {
        return graphic;
    }

    public void setOnMouseClicked(EventHandler<MouseEvent> event) {
        this.event = event;

        if (graphic != null)
            graphic.setOnMouseClicked(event);
    }

}
