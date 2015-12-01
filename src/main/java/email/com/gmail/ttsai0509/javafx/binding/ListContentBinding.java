package email.com.gmail.ttsai0509.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ListBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class ListContentBinding<E> extends ListBinding<E> {

    private final ObservableList<E> list;
    private List<Observable> bindings;

    public ListContentBinding(ObservableList<E> list) {
        super();
        this.list = list;
        this.bindings = new ArrayList<>();

        bind(list);
        refreshBindings();

        list.addListener((ListChangeListener<? super E>) c -> refreshBindings());
        list.addListener((InvalidationListener) c -> refreshBindings());
    }

    @Override
    protected ObservableList<E> computeValue() {
        return list;
    }

    @Override
    public void dispose() {
        unbind(list);
        bindings.forEach(this::unbind);
        bindings.clear();
    }

    protected abstract void addItemBindings(E item, List<Observable> bindings);

    private void refreshBindings() {
        bindings.forEach(this::unbind);
        bindings.clear();

        list.forEach(item -> addItemBindings(item, bindings));
        bindings.forEach(this::bind);

        invalidate();
    }

}
