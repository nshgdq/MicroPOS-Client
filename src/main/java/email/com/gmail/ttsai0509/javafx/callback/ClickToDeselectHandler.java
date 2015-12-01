package email.com.gmail.ttsai0509.javafx.callback;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.MouseEvent;
import lombok.NonNull;

public class ClickToDeselectHandler implements EventHandler<MouseEvent> {

    private final ListView context;
    private final ListCell cell;

    public ClickToDeselectHandler(@NonNull ListView context, @NonNull ListCell cell) {
        this.context = context;
        this.cell = cell;
    }

    @Override
    public void handle(MouseEvent event) {
        context.requestFocus();
        SelectionModel selectionModel = context.getSelectionModel();
        if (!cell.isEmpty()) {
            int index = cell.getIndex();
            if (selectionModel.isSelected(index)) {
                selectionModel.clearSelection(index);
            } else {
                selectionModel.select(index);
            }
            event.consume();
        }
    }
}
