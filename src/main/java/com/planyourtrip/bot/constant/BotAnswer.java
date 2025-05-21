package com.planyourtrip.bot.constant;

public class BotAnswer {
    public static final String NEW_TRIP_INIT_RESPONSE = "Введите название поездки (например, \"Барселона %s\")";
    public static final String NEW_TRIP_NAME_RESPONSE = "Введите дату начала поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_START_DT_RESPONSE =
            "Введите дату конца поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_FINAL_RESPONSE =
            "Поездка <b>%s</b> создана!✅\nТеперь вы можете добавить бронирования и заметки.";

    public static final String MY_TRIPS_INIT_RESPONSE = "Список ваших путешествий:";
    public static final String MY_TRIPS_EMPTY_RESPONSE = String.format("У вас пока что нет ни одного путешествия.\n" +
            "Создайте первое с помощью /%s", CommandType.NEW_TRIP.getName());

    public static final String DELETE_INIT_RESPONSE = "Выберите путешествие, которое хотите удалить:";
    public static final String DELETE_FINAL_RESPONSE = "Путешествие удалено.";
    public static final String DELETE_CONFIRMATION_REQUEST = "Вы действительно хотите удалить путешествие <b>%s%s</b>?";
    public static final String DELETE_CONFIRMATION_BUTTON = "Подтвердить удаление.";

    public static final String EXPIRED = " (Завершено)";

    public static final String HELP_ANSWER = String.format("""
                    Я помогу тебе создать и управлять твоими путешествиями ✈️.
                    Ты можешь управлять мною с помощью следующих команд:
                    /%s - создать новое путешествие
                    /%s - управлять своими путешествиями
                    /%s - удалить путешествие""",
            CommandType.NEW_TRIP.getName(), CommandType.MY_TRIPS.getName(), CommandType.DELETE_TRIP.getName());
    public static final String START_ANSWER = """
            \uD83C\uDF0D Добро пожаловать! Я помогу тебе планировать путешествия.
            Нажимай на кнопку под этим сообщением, чтоб выбрать действие""";
    public static final String DEFAULT_ANSWER = """
            \uD83C\uDF0D Команда не распознана.
            Нажимай на кнопку под этим сообщением, чтоб выбрать действие""";
}
