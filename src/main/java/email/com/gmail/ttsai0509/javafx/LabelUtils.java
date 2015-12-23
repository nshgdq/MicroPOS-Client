package email.com.gmail.ttsai0509.javafx;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public final class LabelUtils {

    private LabelUtils() {}

    public static void fitToText(Label label) {
        label.setMinWidth(Region.USE_PREF_SIZE);
    }

}
