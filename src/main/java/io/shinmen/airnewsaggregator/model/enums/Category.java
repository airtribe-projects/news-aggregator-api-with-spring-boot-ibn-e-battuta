package io.shinmen.airnewsaggregator.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Category {
    BUSINESS, ENTERTAINMENT, GENERAL, HEALTH, SCIENCE, SPORTS, TECHNOLOGY;

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static Category fromValue(String value) {
        try {
            return Category.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Illegal category value, {}", value, e);
            return null; // Return null for invalid values
        }
    }
}
