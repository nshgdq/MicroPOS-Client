package email.com.gmail.ttsai0509.javafx.presenter;


public abstract class ItemPresenter<T> extends Presenter {

    private T item;

    public final T getItem() {
        return item;
    }

    public final void setItem(T newItem) {
        updateItem(item, newItem);
        this.item = newItem;
    }

    protected abstract void updateItem(T currentItem, T newItem);

    public final void setRefresh(T newItem) {
        setItem(newItem);
        refresh();
    }

}
