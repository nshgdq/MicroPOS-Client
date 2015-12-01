package email.com.gmail.ttsai0509.javafx.binding;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;

public class CentStringBinding extends StringBinding {

    private final StringProperty cents;

    public CentStringBinding(StringProperty cents) {
        super();
        bind(cents);
        this.cents = cents;
    }

    @Override
    public void dispose() {
        unbind(cents);
    }

    @Override
    protected String computeValue() {
        String raw = cents.get();
        int length = raw.length();
        if (length == 0)
            return "0.00";
        else if (length == 1)
            return "0.0" + raw;
        else if (length == 2)
            return "0." + raw;
        else
            return raw.substring(0, length - 2) + "." + raw.substring(length - 2, length);
    }
}
