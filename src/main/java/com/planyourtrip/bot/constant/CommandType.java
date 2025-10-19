package com.planyourtrip.bot.constant;

import lombok.Getter;

@Getter
public enum CommandType {
    NEW_TRIP("new_trip", BotAnswer.NEW_TRIP_BUTTON_DESCRIPTION),
    MY_TRIPS("my_trips", BotAnswer.MY_TRIPS_BUTTON_DESCRIPTION),
    DELETE_TRIP("delete_trip", BotAnswer.DELETE_TRIP_BUTTON_DESCRIPTION),
    DUPLICATE_TRIP("duplicate_trip", BotAnswer.DUPLICATE_TRIP_BUTTON_DESCRIPTION),
    EDIT_TRIP("edit_trip", BotAnswer.EDIT_TRIP_BUTTON_DESCRIPTION),

    ADD_ACCOMMODATION("add_accommodation", BotAnswer.ADD_ACCOMMODATION_BUTTON_DESCRIPTION),
    EDIT_ACCOMMODATION("edit_add_accommodation", BotAnswer.EDIT_ACCOMMODATION_BUTTON_DESCRIPTION),
    MY_ACCOMMODATIONS("my_add_accommodations", BotAnswer.MY_ACCOMMODATIONS_BUTTON_DESCRIPTION),
    DELETE_ACCOMMODATION("delete_add_accommodation", BotAnswer.DELETE_ACCOMMODATION_BUTTON_DESCRIPTION),

    ADD_TICKET("add_ticket", BotAnswer.ADD_TICKET_BUTTON_DESCRIPTION),
    MY_TICKETS("my_tickets", BotAnswer.MY_TICKETS_BUTTON_DESCRIPTION),
    DELETE_TICKET("delete_ticket", BotAnswer.DELETE_TICKET_BUTTON_DESCRIPTION),
    EDIT_TICKET("edit_ticket", BotAnswer.EDIT_TICKET_BUTTON_DESCRIPTION),

    ADD_NOTE("add_note", BotAnswer.ADD_NOTE_BUTTON_DESCRIPTION),
    MY_NOTES("my_notes", BotAnswer.MY_NOTES_BUTTON_DESCRIPTION),
    DELETE_NOTE("delete_note", BotAnswer.DELETE_NOTE_BUTTON_DESCRIPTION),
    EDIT_NOTE("edit_note", BotAnswer.EDIT_NOTE_BUTTON_DESCRIPTION),

    ADD_MEMBER("add_member", BotAnswer.ADD_MEMBER_BUTTON_DESCRIPTION),
    MEMBERS("members", BotAnswer.MEMBERS_BUTTON_DESCRIPTION),
    DELETE_MEMBER("delete_member", BotAnswer.DELETE_MEMBER_BUTTON_DESCRIPTION),
    MY_MEMBERS("my_members", BotAnswer.MY_MEMBERS_BUTTON_DESCRIPTION),
    LEAVE_TRIP("leave_trip", BotAnswer.LEAVE_TRIP_BUTTON_DESCRIPTION),

    HELP("help", BotAnswer.HELP_BUTTON_DESCRIPTION),
    SUMMARY("summary", BotAnswer.SUMMARY_BUTTON_DESCRIPTION),
    MENU("menu", BotAnswer.MENU_BUTTON_DESCRIPTION),
    START("start", BotAnswer.START_BUTTON_DESCRIPTION),
    LANGUAGE("language", BotAnswer.LANGUAGE_BUTTON_DESCRIPTION),
    DEFAULT("default", BotAnswer.DEFAULT_BUTTON_DESCRIPTION),
    CANCEL("cancel", BotAnswer.CANCEL_BUTTON_DESCRIPTION);

    private final String name;
    private final String description;

    CommandType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
