package io.shinmen.airnewsaggregator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends AirNewsException {
    public RoleNotFoundException(final String role) {
        super("Role: " + role + " was not found");
    }
}
