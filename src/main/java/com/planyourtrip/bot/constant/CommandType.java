package com.planyourtrip.bot.constant;

import lombok.Getter;

@Getter
public enum CommandType {
    NEW_TRIP("new_trip", "Create a new trip"),
    MY_TRIPS("my_trips", "List all your trips"),
    DELETE_TRIP("delete_trip", "Delete current trip"),
    DUPLICATE_TRIP("duplicate_trip", "Create a copy of an existing trip"),
    EDIT_TRIP("edit_trip", "Edit current trip"),

    ADD_HOTEL("add_hotel", "Add a hotel or accommodation"),
    EDIT_HOTEL("edit_hotel", "Edit hotel details"),
    MY_HOTELS("my_hotels", "List of hotel bookings"),
    DELETE_HOTEL("delete_hotel", "Delete a hotel "),

    ADD_TICKET("add_ticket", "Add a new ticket"),
    MY_TICKETS("my_tickets", "List a tickets"),
    DELETE_TICKET("delete_ticket", "Delete a ticket"),
    EDIT_TICKET("edit_ticket", "Edit ticket details"),

    ADD_NOTE("add_note", "Add a note or task"),
    MY_NOTES("my_notes", "List of notes"),
    DELETE_NOTE("delete_note", "Delete a note"),
    EDIT_NOTE("edit_note", "Edit a note"),

    ADD_MEMBER("add_member", "Invite a participant to the trip (only for admin of a trip)"),
    MEMBERS("members", "List all trip members"),
    DELETE_MEMBER("delete_member", "Delete a participant (only for admin of a trip)"),
    MY_MEMBERS("my_members", "List all your members for all trips"),
    LEAVE_TRIP("leave_trip", "Leave the current trip"),

    HELP("help", "Show help and available commands"),
    SUMMERY("summery", "Show summery of the current trip"),
    MENU("menu", "Open main menu with navigation options"),
    START("start", "Start using the bot"),
    LANGUAGE("language", "Change your preferred language"),
    DEFAULT("default", "Service command for unavailable command"),
    CANCEL("cancel", "Service command for canceling");

    private final String name;
    private final String description;

    CommandType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
