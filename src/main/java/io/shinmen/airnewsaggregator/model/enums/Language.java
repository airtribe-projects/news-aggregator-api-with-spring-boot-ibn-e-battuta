package io.shinmen.airnewsaggregator.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Language {
    AR, DE, EN, ES, FR, HE, IT, NL, NO, PT, RU, SV, UD, ZH, UNKNOWN;

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static Language fromValue(String value) {
        try {
            return Language.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Illegal language value, {}", value, e);
            return UNKNOWN;
        }
    }
}
