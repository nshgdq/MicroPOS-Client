package email.com.gmail.ttsai0509.javafx.control;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TiledListView<T> extends TilePane {

    private static final String styleClass = "tiled-list-view";

    private ObservableList<T> items = FXCollections.observableArrayList();

    public TiledListView() {
        super();
        this.getStyleClass().add(styleClass);
    }

    public ObservableList<T> getItems() {
        return this.items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.removeListener(changeListener);
        this.items = items;
        this.items.addListener(changeListener);
        Platform.runLater(() -> changeListener.onChanged(null));
    }

    /******************************************************************
     *                                                                *
     * Cell Factory                                                   *
     *                                                                *
     ******************************************************************/

    private Callback<TiledListView<T>, TileCell<T>> cellFactory;

    public void setCellFactory(Callback<TiledListView<T>, TileCell<T>> factory) {
        this.cellFactory = factory;
    }

    /******************************************************************
     *                                                                *
     * On Click Handler                                               *
     *                                                                *
     ******************************************************************/

    private ClickHandler<T> clickHandler;

    public interface ClickHandler<T> {
        void handle(MouseEvent event, TileCell<T> cell);
    }

    public ClickHandler<T> getClickHandler() {
        return clickHandler;
    }

    public void setClickHandler(ClickHandler<T> clickHandler) {
        this.clickHandler = clickHandler;

        // Update Cell Cache View Handlers
        for (TileCell<T> cell : cellCache)
            cell.setOnMouseClicked(event -> {
                if (clickHandler != null)
                    getClickHandler().handle(event, cell);
            });
    }


    /******************************************************************
     *                                                                *
     * Cell Cache                                                     *
     *                                                                *
     ******************************************************************/

    private final List<TileCell<T>> cellCache = new ArrayList<>();

    private void createCacheViews(int n) {
        for (int i = 0; i < n; i++) {
            TileCell<T> cell = cellFactory.call(this);
            cell.setOnMouseClicked(event -> {
                ClickHandler<T> clickHandler = getClickHandler();
                if (clickHandler != null)
                    getClickHandler().handle(event, cell);
            });
            cellCache.add(cell);
        }
    }

    /******************************************************************
     *                                                                *
     *                                                                *
     *                                                                *
     ******************************************************************/

    private ListChangeListener<T> changeListener = c -> {
        int newSize = items.size();
        int currSize = cellCache.size();

        // Ensure we have enough cached views
        createCacheViews(newSize - currSize);

        // Update all views with their new content
        // This is a naive, greedy strategy. Sufficient for now.
        for (int i = 0; i < newSize; i++)
            cellCache.get(i).updateItem(items.get(i));

        for (int i = newSize; i < currSize; i++)
            cellCache.get(i).updateItem(null);

        // Show only the newly updated views
        getChildren().clear();
        for (int i = 0; i < newSize; i++)
            getChildren().add(cellCache.get(i).getGraphic());
    };

}
