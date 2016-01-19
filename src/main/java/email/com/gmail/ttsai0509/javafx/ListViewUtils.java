package email.com.gmail.ttsai0509.javafx;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

public class ListViewUtils {

    private ListViewUtils() {
        throw new RuntimeException(getClass().getSimpleName() + " is a utility class.");
    }

    public static class Range {
        public final int left;
        public final int right;

        public Range(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    // http://stackoverflow.com/questions/30457708/visible-items-of-listview
    public static Range getListViewRange(ListView<?> lv) {
        ListViewSkin<?> skin = (ListViewSkin<?>) lv.getSkin();
        VirtualFlow<?> flow = (VirtualFlow<?>) skin.getChildren().get(0);
        return new Range(flow.getFirstVisibleCell().getIndex(), flow.getLastVisibleCell().getIndex());
    }

    public static void listViewScrollBy(ListView<?> lv, int i) {
        ListViewSkin<?> skin = (ListViewSkin<?>) lv.getSkin();
        VirtualFlow<?> flow = (VirtualFlow<?>) skin.getChildren().get(0);
        int curr = flow.getFirstVisibleCell().getIndex();
        int next = Math.max(0, Math.min(flow.getCellCount() - 1, curr + i));
        flow.scrollTo(next);
    }

    public static void fitToListView(Region n, ListView<?> lv) {
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
