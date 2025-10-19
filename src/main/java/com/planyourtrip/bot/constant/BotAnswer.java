package com.planyourtrip.bot.constant;

public class BotAnswer {
    public static final String NEW_TRIP_NAME_REQUEST = "Введите название поездки (например, \"Барселона %s\")";
    public static final String NEW_TRIP_START_DT_REQUEST =
            "Введите дату начала поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_END_DT_REQUEST = "Введите дату конца поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_FINAL_RESPONSE =
            "Поездка <b>%s</b> создана!✅\nТеперь вы можете добавить бронирования и заметки.";
    public static final String MY_TRIPS_EMPTY_RESPONSE = "У вас пока что нет ни одного путешествия.\n" +
            "Создайте свое первое путешествие ✈️";
    public static final String MY_TRIPS_RESPONSE = "Ваши путешествия:";
    public static final String DELETE_TRIP_CONFIRMATION_REQUEST =
            "Вы действительно хотите удалить путешествие <b>%s%s</b>?";
    public static final String DELETE_TRIP_FINAL_RESPONSE = "Путешествие удалено";
    public static final String CHOOSE_TRIP_REQUEST = "Выберите путешествие:";

    public static final String ADD_TICKET_TYPE_REQUEST = "Выберите тип билета";
    public static final String ADD_TICKET_DEPARTURE_REQUEST = "Введите место отправления";
    public static final String ADD_TICKET_DEPARTURE_DATE_REQUEST =
            "Введите дату отправления в формате дд.ММ.гггг\uD83D\uDCC5";
    public static final String ADD_TICKET_DEPARTURE_TIME_REQUEST =
            "Введите время отправления в формате ЧЧ:мм\uD83D\uDCC5";
    public static final String ADD_TICKET_ARRIVAL_REQUEST = "Введите место назначения";
    public static final String ADD_TICKET_ARRIVAL_DATE_REQUEST =
            "Введите дату прибытия в формате дд.ММ.гггг\uD83D\uDCC5";
    public static final String ADD_TICKET_ARRIVAL_TIME_REQUEST =
            "Введите время прибытия в формате ЧЧ:мм\uD83D\uDCC5";
    public static final String ADD_TICKET_FILE_REQUEST = "Добавьте билет";
    public static final String ADD_TICKET_FINAL_RESPONSE = "Билет добавлен!✅";
    public static final String MY_TICKETS_EMPTY_RESPONSE = "У Вас пока что нет билетов для этой поездки";
    public static final String MY_TICKETS_RESPONSE = """
            <b>Список билетов:</b>
            %s
            """;
    public static final String DELETE_TICKET_CONFIRMATION_REQUEST = "Вы действительно хотите удалить билет <b>%s</b>?";
    public static final String DELETE_TICKET_FINAL_RESPONSE = "Билет удален";
    public static final String CHOOSE_TICKET_REQUEST = "Выберите билет:";

    public static final String ADD_ACCOMMODATION_TYPE_REQUEST = "Выберите тип размещения";
    public static final String ADD_ACCOMMODATION_NAME_REQUEST = "Введите название";
    public static final String ADD_ACCOMMODATION_ADDRESS_REQUEST = "Введите адрес";
    public static final String ADD_ACCOMMODATION_CHECK_IN_REQUEST =
            "Введите дату заселения в формате дд.ММ.гггг\uD83D\uDCC5";
    public static final String ADD_ACCOMMODATION_CHECK_OUT_REQUEST =
            "Введите дату выезда в формате дд.ММ.гггг\uD83D\uDCC5";
    public static final String ADD_ACCOMMODATION_FILE_REQUEST = "Добавьте подтверждение бронирования";
    public static final String ADD_ACCOMMODATION_FINAL_RESPONSE = "Размещение добавлено!✅";
    public static final String MY_ACCOMMODATIONS_EMPTY_RESPONSE = "У Вас пока что нет размещений для этой поездки";
    public static final String MY_ACCOMMODATIONS_RESPONSE = """
            <b>Список размещений:</b>
            %s
            """;
    public static final String DELETE_ACCOMMODATION_CONFIRMATION_REQUEST =
            "Вы действительно хотите удалить размещение <b>%s (%s - %s)</b>?";
    public static final String DELETE_ACCOMMODATION_FINAL_RESPONSE = "Размещение удалено";
    public static final String CHOOSE_ACCOMMODATION_REQUEST = "Выберите размещение:";

    public static final String ADD_NOTE_TITLE_REQUEST = "Введите заголовок заметки";
    public static final String ADD_NOTE_CONTENT_REQUEST = "Введите заметку";
    public static final String ADD_NOTE_FINAL_RESPONSE = "Заметка добавлена!✅";
    public static final String MY_NOTES_EMPTY_RESPONSE = "У Вас пока что нет заметок для этой поездки";
    public static final String MY_NOTES_RESPONSE = """
            <b>Список заметок:</b>
            %s
            """;
    public static final String DELETE_NOTE_CONFIRMATION_REQUEST = "Вы действительно хотите удалить заметку: <b>%s</b>?";
    public static final String DELETE_NOTE_FINAL_RESPONSE = "Заметка удалена";
    public static final String CHOOSE_NOTE_REQUEST = "Выберите заметку:";

    public static final String CHOOSE_SUMMARY_FORMAT_REQUEST = "Выберите формат:";

    public static final String DELETE_CONFIRMATION_REQUEST = "Подтвердить удаление";
    public static final String EDIT_CHOOSE_FIELD_REQUEST = "Выберите, что хотели бы изменить:";
    public static final String NEW_VALUE_REQUEST = "Введите новое значение:";
    public static final String EXPIRED = " (Завершено)";
    public static final String CONFIRMED = "Да";
    public static final String CANCEL = "<< Назад";
    public static final String SKIP = "Пропустить";
    public static final String COMPLETE = "Создать";
    public static final String DONE = "Готово!";
    public static final String FROM = "с";
    public static final String TILL = "по";
    public static final String START = "отправление";
    public static final String END = "прибытие";

    public static final String HELP_ANSWER = String.format("""
                    Я помогу тебе создать и управлять твоими путешествиями ✈️.
                    Ты можешь управлять мною с помощью следующих команд:
                    /%s - создать новое путешествие
                    /%s - управлять своими путешествиями
                    /%s - удалить путешествие""",
            CommandType.NEW_TRIP.getName(), CommandType.MY_TRIPS.getName(), CommandType.DELETE_TRIP.getName());
    public static final String START_ANSWER = """
            \uD83C\uDF0D Добро пожаловать, %s!
            Я помогу тебе планировать путешествия.
            Нажимай на кнопку под этим сообщением, чтоб выбрать следующее действие""";
    public static final String DEFAULT_ANSWER = """
            \uD83C\uDF0D Извините, я не понимаю.
            Попробуйте еще раз, либо нажмите на кнопку под этим сообщением, чтобы выбрать действие""";

    public static final String SOMETHING_WRONG = """
            \uD83C\uDF0D Что-то пошло не так, давайте попробуем начать сначала.
            Нажмите, пожалуйста, на кнопку под этим сообщением, чтобы выбрать действие""";

    public static final String TICKET_TYPE_PLANE = "Самолет";
    public static final String TICKET_TYPE_TRAIN = "Поезд";
    public static final String TICKET_TYPE_BUS = "Автобус";
    public static final String TICKET_TYPE_CAR = "Автомобиль";
    public static final String TICKET_TYPE_OTHER = "Неизвестный тип транспорта";

    public static final String ACCOMMODATION_TYPE_HOTEL = "Отель";
    public static final String ACCOMMODATION_TYPE_APARTMENTS = "Апартаменты";

    public static final String NEW_TRIP_BUTTON_DESCRIPTION = "Create a new trip";
    public static final String MY_TRIPS_BUTTON_DESCRIPTION = "List all your trips";
    public static final String DELETE_TRIP_BUTTON_DESCRIPTION = "Delete trip";
    public static final String DUPLICATE_TRIP_BUTTON_DESCRIPTION = "Create a copy of an existing trip";
    public static final String EDIT_TRIP_BUTTON_DESCRIPTION = "Edit trip";
    public static final String ADD_ACCOMMODATION_BUTTON_DESCRIPTION = "Add a accommodation";
    public static final String EDIT_ACCOMMODATION_BUTTON_DESCRIPTION = "Edit accommodation details";
    public static final String MY_ACCOMMODATIONS_BUTTON_DESCRIPTION = "List of accommodation bookings";
    public static final String DELETE_ACCOMMODATION_BUTTON_DESCRIPTION = "Delete a accommodation";
    public static final String ADD_TICKET_BUTTON_DESCRIPTION = "Add a new ticket";
    public static final String MY_TICKETS_BUTTON_DESCRIPTION = "List a tickets";
    public static final String DELETE_TICKET_BUTTON_DESCRIPTION = "Delete a ticket";
    public static final String EDIT_TICKET_BUTTON_DESCRIPTION = "Edit ticket details";
    public static final String ADD_NOTE_BUTTON_DESCRIPTION = "Add a note or task";
    public static final String MY_NOTES_BUTTON_DESCRIPTION = "List of notes";
    public static final String DELETE_NOTE_BUTTON_DESCRIPTION = "Delete a note";
    public static final String EDIT_NOTE_BUTTON_DESCRIPTION = "Edit a note";
    public static final String ADD_MEMBER_BUTTON_DESCRIPTION = "Invite a participant to the trip (only for admin of a trip)";
    public static final String MEMBERS_BUTTON_DESCRIPTION = "List all trip members";
    public static final String DELETE_MEMBER_BUTTON_DESCRIPTION = "Delete a participant (only for admin of a trip)";
    public static final String MY_MEMBERS_BUTTON_DESCRIPTION = "List all your members for all trips";
    public static final String LEAVE_TRIP_BUTTON_DESCRIPTION = "Leave the trip";
    public static final String HELP_BUTTON_DESCRIPTION = "Show help and available commands";
    public static final String SUMMARY_BUTTON_DESCRIPTION = "Show summary of the trip";
    public static final String MENU_BUTTON_DESCRIPTION = "Open main menu with navigation options";
    public static final String START_BUTTON_DESCRIPTION = "Start using the bot";
    public static final String LANGUAGE_BUTTON_DESCRIPTION = "Change your preferred language";
    public static final String DEFAULT_BUTTON_DESCRIPTION = "Service command for unavailable command";
    public static final String CANCEL_BUTTON_DESCRIPTION = "Service command for canceling";
}
