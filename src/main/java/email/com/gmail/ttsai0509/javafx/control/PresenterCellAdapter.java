package email.com.gmail.ttsai0509.javafx.control;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;
import javafx.scene.control.ListCell;

public class PresenterCellAdapter<T> extends ListCell<T> {

    private ItemPresenter<T> presenter;

    public PresenterCellAdapter(ItemPresenter<T> presenter) {
        this.presenter = presenter;
    }

    public ItemPresenter<T> getPresenter() {
        return presenter;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            presenter.setItem(null);
            setGraphic(null);
        } else {
            presenter.setItem(item);
            setGraphic(presenter.getView());
        }
    }

    public static <T> PresenterCellAdapter<T> load(String resource) {
        return new PresenterCellAdapter<>(Presenter.load(resource));
    }
}
