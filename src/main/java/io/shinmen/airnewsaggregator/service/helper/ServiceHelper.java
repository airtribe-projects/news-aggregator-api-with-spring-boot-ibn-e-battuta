package io.shinmen.airnewsaggregator.service.helper;

public class ServiceHelper {

    private ServiceHelper() {
    }

    public static String getEntityNotFoundMessage(String entity, String field, String value) {
        return entity + " with " + field + ": " + value + " was not found";
    }

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
}
