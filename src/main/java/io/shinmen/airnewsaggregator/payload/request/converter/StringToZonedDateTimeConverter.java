package io.shinmen.airnewsaggregator.payload.request.converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(@NonNull String source) {
        LocalDate localDate = LocalDate.parse(source);
        return localDate.atStartOfDay(ZoneId.systemDefault());
    }
}
