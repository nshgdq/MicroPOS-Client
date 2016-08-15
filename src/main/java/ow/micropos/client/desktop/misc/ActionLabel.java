package ow.micropos.client.desktop.misc;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ActionLabel extends ItemPresenter<Action> {

    private static final String styleClass = "action-label";

    private final Label label;
    private final StackPane stackPane;

    public ActionLabel() {
        label = new Label();

        stackPane = new StackPane(label);
        stackPane.getStyleClass().setAll(styleClass);

        Presenter.injectView(this, stackPane);
    }

    @Override
    public void dispose() {
        stackPane.setOnMouseClicked(null);
    }

    @Override
    protected void updateItem(Action currentItem, Action newItem) {
        if (newItem.getCssId() != null)
            stackPane.setId(newItem.getCssId());

        stackPane.setOnMouseClicked(newItem.getAction());
        label.setText(newItem.getTitle());
    }
}
