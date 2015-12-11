package email.com.gmail.ttsai0509.javafx;

import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

public class ListViewUtils {

    private ListViewUtils() {
        throw new RuntimeException(getClass().getSimpleName() + " is a utility class.");
    }

    public static void bindFitToListView(Region n, ListView<?> lv) {
        n.maxWidthProperty().bind(lv.widthProperty());
        n.minWidthProperty().bind(lv.widthProperty());
        n.prefWidthProperty().bind(lv.widthProperty());
    }

    public static void setFitToListView(Region n, ListView<?> lv) {
        n.maxWidth(lv.getWidth());
        n.minWidth(lv.getWidth());
        n.prefWidth(lv.getWidth());
    }

}
