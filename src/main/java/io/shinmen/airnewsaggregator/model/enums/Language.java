package io.shinmen.airnewsaggregator.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    AR, DE, EN, ES, FR, HE, IT, NL, NO, PT, RU, SV, UD, ZH;

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static Language fromValue(String value) {
        return Language.valueOf(value.toUpperCase());
    }
}
