package io.shinmen.airnewsaggregator.utility;

public class Constants {

    private Constants() {
    }

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String CACHE_MANAGER_PREFIX = "cache-manager";

    public static final String PAGE_DEFAULT = "1";
    public static final String PAGE_SIZE_DEFAULT = "20";

    public static final int PAGE_MIN = 1;
    public static final int PAGE_SIZE_MIN = 1;
    public static final int PAGE_SIZE_MAX = 100;

    public static final String READ = "read";
    public static final String FAVORITE = "favorite";

    // api/auth paths
    public static final String API_AUTH = "/api/auth";
    public static final String API_AUTH_REGISTER = "/register";
    public static final String API_AUTH_LOGIN = "/login";
    public static final String API_AUTH_REFRESH_TOKEN = "/refresh-token";
    public static final String API_AUTH_VERIFY = "/verify";
    public static final String API_AUTH_RE_VERIFY = "/re-verify";

    // api/cache paths
    public static final String API_CACHE = "/api/cache";
    public static final String API_CACHE_NAME_PATH = "/{cacheName}";

    // api/news paths
    public static final String API_NEWS = "/api/news";
    public static final String API_NEWS_TOP_HEADLINES = "/top-headlines";
    public static final String API_NEWS_SEARCH_KEYWORD = "/search/{keyword}";
    public static final String API_NEWS_EVERYTHING = "/everything";
    public static final String API_NEWS_READ = "/read";
    public static final String API_NEWS_FAVORITE = "/favorite";
    public static final String API_NEWS_UNREAD_ARTICLE_ID = "/unread/{articleId}";
    public static final String API_NEWS_UNFAVORITE_ARTICLE_ID = "/unfavorite/{articleId}";

    // api/preferences paths
    public static final String API_PREFERENCES = "/api/preferences";

    // caches
    public static final String CACHE_SOURCES = "sources";
    public static final String CACHE_TOP_HEADLINES = "topHeadlines";
    public static final String CACHE_SEARCH = "search";
    public static final String CACHE_EVERYTHING = "everything";

    // news api endpoints
    public static final String NEWS_API_SOURCES = "top-headlines/sources";
    public static final String NEWS_API_TOP_HEADLINES = "top-headlines";
    public static final String NEWS_API_EVERYTHING = "everything";

    // news api query parameters
    public static final String NEWS_API_QUERY_PAGE_SIZE = "pageSize";
    public static final String NEWS_API_QUERY_PAGE = "page";
    public static final String NEWS_API_QUERY_LANGUAGE = "language";
    public static final String NEWS_API_QUERY_KEYWORD = "q";
    public static final String NEWS_API_QUERY_FROM = "from";
    public static final String NEWS_API_QUERY_TO = "to";
    public static final String NEWS_API_QUERY_SORT_BY = "sortBy";
    public static final String NEWS_API_QUERY_SOURCES = "sources";
    public static final String NEWS_API_QUERY_CATEGORY = "category";
    public static final String NEWS_API_QUERY_COUNTRY = "country";
    public static final String NEWS_API_QUERY_DOMAINS = "domains";
    public static final String NEWS_API_QUERY_API_KEY = "apiKey";

    public static final String NEWS_API_EXCEPTION_CODE_JSON_ERROR = "NEWS-API-JSON-PROCESSING";
    public static final String NEWS_API_EXCEPTION_CODE_UNKNOWN_ERROR = "NEWS-API-UNKNOWN";
    public static final String NEWS_API_EXCEPTION_CODE_NO_RESPONSE_ERROR = "NEWS-API-NO-RESPONSE";

    public static final String NEWS_API_STATUS_OK = "ok";
    public static final String NEWS_API_STATUS_ERROR = "error";
}
