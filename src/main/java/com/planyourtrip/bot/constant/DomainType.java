package com.planyourtrip.bot.constant;

public enum DomainType {
    TRIP("trip"),
    HOTEL("hotel"),
    TICKET("ticket"),
    NOTE("note"),
    USER("user");

    private final String description;

    DomainType(final String descriptionParam) {
        this.description = descriptionParam;
    }

}
