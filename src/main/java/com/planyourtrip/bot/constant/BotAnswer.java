package com.planyourtrip.bot.constant;

public class BotAnswer {
    public static final String NEW_TRIP_NAME_REQUEST = "Введите название поездки (например, \"Барселона %s\")";
    public static final String NEW_TRIP_START_DT_REQUEST = "Введите дату начала поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_END_DT_REQUEST = "Введите дату конца поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_FINAL_RESPONSE =
            "Поездка <b>%s</b> создана!✅\nТеперь вы можете добавить бронирования и заметки.";
    public static final String MY_TRIPS_LIST_RESPONSE = "Список ваших путешествий:";
    public static final String MY_TRIPS_EMPTY_RESPONSE = "У вас пока что нет ни одного путешествия.\n" +
            "Создайте свое первое путешествие ✈️";
    public static final String DELETE_TRIP_CONFIRMATION_REQUEST = "Вы действительно хотите удалить путешествие <b>%s%s</b>?";
    public static final String DELETE_TRIP_FINAL_RESPONSE = "Путешествие удалено";
    public static final String CHOOSE_TRIP_REQUEST = "Выберите путешествие:";

    public static final String ADD_TICKET_TYPE_REQUEST = "Выберите тип билета";
    public static final String ADD_TICKET_DEPARTURE_REQUEST = "Введите место отправления";
    public static final String ADD_TICKET_DEPARTURE_DT_REQUEST =
            "Введите дату и время отправления в формате дд.ММ.гггг ЧЧ:мм\uD83D\uDCC5";
    public static final String ADD_TICKET_ARRIVAL_REQUEST = "Введите место назначения";
    public static final String ADD_TICKET_ARRIVAL_DT_REQUEST =
            "Введите дату и время прибытия в формате дд.ММ.гггг ЧЧ:мм\uD83D\uDCC5";
    public static final String ADD_TICKET_FILE_REQUEST = "Добавьте билет";
    public static final String ADD_TICKET_FINAL_RESPONSE = "Билет добавлен!✅";
    public static final String MY_TICKETS_EMPTY_RESPONSE = "У Вас пока что нет билетов для этой поездки";
    public static final String DELETE_TICKET_CONFIRMATION_REQUEST = "Вы действительно хотите удалить билет <b>%s</b>?";
    public static final String DELETE_TICKET_FINAL_RESPONSE = "Билет удален";
    public static final String CHOOSE_TICKET_REQUEST = "Выберите билет:";

    public static final String ADD_HOTEL_TYPE_REQUEST = "Выберите тип размещения";
    public static final String ADD_HOTEL_NAME_REQUEST = "Введите название";
    public static final String ADD_HOTEL_ADDRESS_REQUEST = "Введите адрес";
    public static final String ADD_HOTEL_CHECK_IN_REQUEST = "Введите дату заселения в формате дд.ММ.гггг\uD83D\uDCC5";
    public static final String ADD_HOTEL_CHECK_OUT_REQUEST = "Введите дату выезда в формате дд.ММ.гггг\uD83D\uDCC5";
    public static final String ADD_HOTEL_FILE_REQUEST = "Добавьте подтверждение бронирования";
    public static final String ADD_HOTEL_FINAL_RESPONSE = "Размещение добавлено!✅";
    public static final String MY_HOTELS_EMPTY_RESPONSE = "У Вас пока что нет размещений для этой поездки";
    public static final String DELETE_HOTEL_CONFIRMATION_REQUEST = "Вы действительно хотите удалить размещение <b>%s (%s - %s)</b>?";
    public static final String DELETE_HOTEL_FINAL_RESPONSE = "Размещение удалено";
    public static final String CHOOSE_HOTEL_REQUEST = "Выберите размещение:";

    public static final String ADD_NOTE_TITLE_REQUEST = "Введите заголовок заметки";
    public static final String ADD_NOTE_CONTENT_REQUEST = "Введите заметку";
    public static final String ADD_NOTE_FINAL_RESPONSE = "Заметка добавлена!✅";
    public static final String MY_NOTES_EMPTY_RESPONSE = "У Вас пока что нет заметок для этой поездки";
    public static final String DELETE_NOTE_CONFIRMATION_REQUEST = "Вы действительно хотите удалить заметку: <b>%s</b>?";
    public static final String DELETE_NOTE_FINAL_RESPONSE = "Заметка удалена";
    public static final String CHOOSE_NOTE_REQUEST = "Выберите заметку:";

    public static final String DELETE_CONFIRMATION_REQUEST = "Подтвердить удаление";
    public static final String EDIT_CHOOSE_FIELD_REQUEST = "Выберите, что хотели бы изменить:";
    public static final String NEW_VALUE_REQUEST = "Введите новое значение:";
    public static final String EXPIRED = " (Завершено)";
    public static final String CONFIRMED = "Да";
    public static final String CANCEL = "<< Назад";
    public static final String SKIP = "Пропустить";
    public static final String DONE = "Готово!";

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
}
