package uk.gov.dwp.jsa.notification.service.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class SimpleI8NDateFormat implements I8NDateFormat {

    private Locale locale;

    public SimpleI8NDateFormat(final Locale locale) {
        this.locale = locale;
    }
    @Override
    public String format(final LocalDate localDate) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd/MM/yyyy")
                .toFormatter(locale);
        return formatter.format(localDate);

    }
}
