package com.example.scheduleservice.model;

public enum UserType {
    ADMIN,MANAGER,CLIENT;

    @Override
    public String toString() {
        return name().toLowerCase(); // Use lowercase representation for the enum values
    }

    public static UserType fromString(String value) {
        for (UserType userType : values()) {
            if (userType.name().equalsIgnoreCase(value)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Unknown UserType: " + value);
    }
}
