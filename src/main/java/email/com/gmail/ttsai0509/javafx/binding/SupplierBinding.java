package email.com.gmail.ttsai0509.javafx.binding;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;

import java.util.function.Supplier;

public class SupplierBinding<T> extends ObjectBinding<T> {

    private final Supplier<T> supplier;
    private final Observable[] dependencies;

    public SupplierBinding(Supplier<T> supplier, Observable... dependencies) {
        bind(dependencies);
        this.supplier = supplier;
        this.dependencies = dependencies;
    }

    @Override
    protected T computeValue() {
        return supplier.get();
    }

    @Override
    public void dispose() {
        unbind(dependencies);
    }
}
