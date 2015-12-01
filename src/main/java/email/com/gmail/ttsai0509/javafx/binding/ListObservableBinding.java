package email.com.gmail.ttsai0509.javafx.binding;

import javafx.beans.Observable;
import javafx.collections.ObservableList;

import java.util.List;

/*=======================================================================*
 =                                                                       =
 = Binding for ObservableList of Observables                             =
 =                                                                       =
 *=======================================================================*/
@Deprecated
public class ListObservableBinding<E extends Observable> extends ListContentBinding<E> {

    public ListObservableBinding(ObservableList<E> list) {
        super(list);
    }

    @Override
    protected void addItemBindings(E item, List<Observable> bindings) {
        bindings.add(item);
    }

}
