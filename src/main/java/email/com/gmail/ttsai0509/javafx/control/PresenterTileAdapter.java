package email.com.gmail.ttsai0509.javafx.control;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.javafx.presenter.Presenter;

public class PresenterTileAdapter<T> extends TileCell<T> {

    private ItemPresenter<T> presenter;

    public PresenterTileAdapter(ItemPresenter<T> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateItem(T item) {
        super.updateItem(item);

        if (item == null) {
            presenter.setItem(null);
            setGraphic(null);
        } else {
            presenter.setItem(item);
            setGraphic(presenter.getView());
        }
    }

    public static <T> PresenterTileAdapter<T> load(String resource) {
        return new PresenterTileAdapter<>(Presenter.load(resource));
    }
}
