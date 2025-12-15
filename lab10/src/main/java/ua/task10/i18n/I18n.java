package ua.task10.i18n;

import java.text.MessageFormat;
import java.util.*;

public final class I18n {
    private Locale locale;
    private ResourceBundle bundle;

    public I18n(Locale initial) {
        setLocale(initial);
    }

    public void setLocale(Locale locale) {
        this.locale = Objects.requireNonNull(locale);
        this.bundle = ResourceBundle.getBundle("location.messages", this.locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public String t(String key) {
        return bundle.getString(key);
    }

    public String tf(String key, Object... args) {
        return MessageFormat.format(t(key), args);
    }
}
