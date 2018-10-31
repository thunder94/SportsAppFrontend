package com.hfad.sportsapp.enums;

public enum SuccessType {

    POST_SUCCESS(""),
    AVATAR_SUCCESS("Profile photo has been changed successfully");

    private final String message;

    SuccessType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
