package email.com.gmail.ttsai0509.javafx.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public abstract class DeepListBinding<T, E> extends ObjectBinding<T> {

    private final ObservableList<E> list;
    private final List<Observable> staticBindings;
    private final List<Observable> itemBindings;

    public DeepListBinding(ObservableList<E> list) {
        super();
        this.list = list;
        this.staticBindings = new ArrayList<>();
        this.itemBindings = new ArrayList<>();

        initStaticBindings();
        refreshItemBindings();

        list.addListener((ListChangeListener<? super E>) c -> refreshItemBindings());
        list.addListener((InvalidationListener) c -> refreshItemBindings());
    }

    @Override
    public void dispose() {
        unbind(list);
        itemBindings.forEach(this::unbind);
        itemBindings.clear();
    }

    protected void addStaticBindings(List<Observable> bindings) {}

    protected abstract void addItemBindings(E item, List<Observable> bindings);

    private void initStaticBindings() {
        staticBindings.add(list);
        addStaticBindings(staticBindings);
        staticBindings.forEach(this::bind);
    }

    private void refreshItemBindings() {
        itemBindings.forEach(this::unbind);
        itemBindings.clear();

        list.forEach(item -> addItemBindings(item, itemBindings));
        itemBindings.forEach(this::bind);

        invalidate();
    }
}
