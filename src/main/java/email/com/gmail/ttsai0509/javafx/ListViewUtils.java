package email.com.gmail.ttsai0509.javafx;

import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

public class ListViewUtils {

    private ListViewUtils() {
        throw new RuntimeException(getClass().getSimpleName() + " is a utility class.");
    }

    public static void bindFitToListView(Region n, ListView<?> lv, double padding) {
        n.maxWidthProperty().bind(lv.widthProperty().add(padding));
        n.minWidthProperty().bind(lv.widthProperty().add(padding));
        n.prefWidthProperty().bind(lv.widthProperty().add(padding));
    }

    public static void setFitToListView(Region n, ListView<?> lv, double padding) {
        n.maxWidth(lv.getWidth() + padding);
        n.minWidth(lv.getWidth() + padding);
        n.prefWidth(lv.getWidth() + padding);
    }

}
