package com.hfad.sportsapp.enums;

public enum ServerResponse {

    // register
    EMAIL_ERROR("Confirmation email could not be send"),
    ALREADY_REGISTERED("User with this email already registered"),

    // token confirm and resend token
    TOKEN_NOT_FOUND("Token not found in database"),
    NOT_REGISTERED("No email resend, not registered before"),
    TOKEN_EXPIRED("Expired token"),

    // all requests
    METHOD_ARGS_NOT_VALID("Method arguments are not valid e.g. empty fields, wrong email"),

    // login
    CONFIRM_YOUR_ACCOUNT("Confirm your new account"),
    WRONG_LOGIN_OR_PASSWORD("Wrong login or password"),

    // image controller
    EMPTY_FILE("File is empty"),
    INVALID_IMAGE_PROPORTIONS("Invalid image proportions"),
    FILE_TOO_BIG("File is too big"),
    WRONG_FORMAT("Wrong file format"),
    MEDIA_SERVICE_NOT_AVAILABLE("Multimedia service is inaccessible now"),
    RESOURCE_NOT_FOUND("Resource was not found on server"),

    // whole app
    ACCESS_DENIED("Access denied to resource");

    private final String message;

    ServerResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
