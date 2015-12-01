package email.com.gmail.ttsai0509.javafx.beans;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;
import java.util.Date;

public class DateTimeClock {

    private static final FastDateFormat simpleDateFormat = FastDateFormat.getInstance("MM/dd/yy");
    private static final FastDateFormat ticFormat = FastDateFormat.getInstance("hh:mm");
    private static final FastDateFormat tocFormat = FastDateFormat.getInstance("hh mm");

    private boolean isTic;
    private final ReadOnlyStringWrapper time;
    private final ReadOnlyStringWrapper date;
    private final ObjectProperty<Date> now;
    private final Timeline timeline;

    public DateTimeClock() {

        now = new SimpleObjectProperty<>(Calendar.getInstance().getTime());
        time = new ReadOnlyStringWrapper(ticFormat.format(now.get()));
        date = new ReadOnlyStringWrapper(simpleDateFormat.format(now.get()));

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> {
                    now.get().setTime(System.currentTimeMillis());

                    date.set(simpleDateFormat.format(now.get()));

                    if (isTic)
                        time.set(ticFormat.format(now.get()));
                    else
                        time.set(tocFormat.format(now.get()));
                    isTic = !isTic;
                }),
                new KeyFrame(Duration.seconds(2))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public final void start() {
        timeline.play();
    }

    public final void stop() {
        timeline.stop();
    }

    public final ObjectProperty<Date> nowProperty() {
        return now;
    }

    public final ReadOnlyStringProperty timeProperty() {
        return time.getReadOnlyProperty();
    }

    public final ReadOnlyStringProperty dateProperty() {
        return date.getReadOnlyProperty();
    }

}
