package io.shinmen.airnewsaggregator.utility;

public class Messages {

    private Messages() {
    }

    public static final String USER_REGISTRATION_MESSAGE = "User registered successfully. A verification email has been sent to the provided address.";
    public static final String USER_VERIFICATION_MESSAGE = "User email verified successfully. Account is now active.";
    public static final String USER_RE_VERIFICATION_EMAIL_MESSAGE = "A new verification email has been sent to the user's registered email address.";

    public static final String CACHE_CLEARED_MESSAGE = "Application cache has been successfully cleared.";

    public static final String ARTICLE_READ_MESSAGE = "Article has been marked as read.";
    public static final String ARTICLE_FAVORITE_MESSAGE = "Article has been added to favorites.";
    public static final String ARTICLE_UNREAD_MESSAGE = "Article has been marked as unread.";
    public static final String ARTICLE_UNFAVORITE_MESSAGE = "Article has been removed from favorites.";

    public static final String PAGE_MIN_MESSAGE = "Invalid page number. Page must be a positive integer greater than or equal to "
            + Constants.PAGE_SIZE_MIN + ".";
    public static final String PAGE_SIZE_MIN_MESSAGE = "Invalid page size. Page size must be at least "
            + Constants.PAGE_SIZE_MIN + ".";
    public static final String PAGE_SIZE_MAX_MESSAGE = "Invalid page size. Page size must not exceed "
            + Constants.PAGE_SIZE_MAX + ".";

    public static final String NEWS_API_EXCEPTION_MESSAGE_JSON_ERROR = "Failed to parse JSON response from News API. Please check API version compatibility.";
    public static final String NEWS_API_EXCEPTION_MESSAGE_UNKNOWN_ERROR = "An unknown error occurred while fetching data from News API. Please try again later.";
    public static final String NEWS_API_EXCEPTION_MESSAGE_NO_RESPONSE_ERROR = "No response received from News API. Please check your network connection and try again.";

    public static final String MESSAGE_INVALID_USERNAME_PASSWORD = "Authentication failed: Invalid username or password.";
    public static final String MESSAGE_ACCESS_DENIED = "Access denied: Insufficient permissions for this resource.";
    public static final String MESSAGE_AUTHENTICATION_FAILED = "Authentication failed: Unable to verify user credentials.";
    public static final String MESSAGE_VALIDATION_ERROR = "Request validation failed. Please check the input parameters and try again.";
    public static final String MESSAGE_UNEXPECTED_ERROR = "An unexpected error occurred. Please contact support if the issue persists.";
}
