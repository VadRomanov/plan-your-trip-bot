package com.planyourtrip.bot.constant;

public class BotAnswer {
    public static final String NEW_TRIP_INIT_RESPONSE = "Введите название поездки (например, \"Барселона %s\")";
    public static final String NEW_TRIP_NAME_RESPONSE = "Введите дату начала поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_START_DT_RESPONSE =
            "Введите дату конца поездки в формате дд.ММ.гггг \uD83D\uDCC5";
    public static final String NEW_TRIP_FINAL_RESPONSE =
            "Поездка <b>%s</b> создана!✅\nТеперь вы можете добавить бронирования и заметки.";

    public static final String MY_TRIPS_INIT_RESPONSE = "Список ваших путешествий:";
    public static final String MY_TRIPS_EMPTY_RESPONSE = "У вас пока что нет ни одного путешествия.\n" +
            "Создайте свое первое путешествие ✈️";

    public static final String DELETE_INIT_RESPONSE = "Выберите путешествие, которое хотите удалить:";
    public static final String DELETE_FINAL_RESPONSE = "Путешествие удалено";
    public static final String DELETE_CONFIRMATION_REQUEST = "Вы действительно хотите удалить путешествие <b>%s%s</b>?";
    public static final String DELETE_CONFIRMATION_BUTTON = "Подтвердить удаление";

    public static final String EDIT_TRIP_RESPONSE = "Выберите действие";

    public static final String ADD_TICKET_COMMAND_RESPONSE = "Выберите поездку, к которой хотели бы добавить билет";
    public static final String ADD_TICKET_INIT_RESPONSE = "Выберите тип билета";
    public static final String ADD_TICKET_DEPARTURE_RESPONSE = "Введите место отправления";
    public static final String ADD_TICKET_DEPARTURE_DT_RESPONSE =
            "Введите время отправления в формате дд.ММ.гггг ЧЧ:мм\uD83D\uDCC5";
    public static final String ADD_TICKET_ARRIVAL_RESPONSE = "Введите место назначения";
    public static final String ADD_TICKET_ARRIVAL_DT_RESPONSE =
            "Введите время прибытия в формате дд.ММ.гггг ЧЧ:мм\uD83D\uDCC5";
    public static final String ADD_TICKET_FILE_RESPONSE = "Добавьте билет";
    public static final String ADD_TICKET_FINAL_RESPONSE = "Билет добавлен!✅";

    public static final String EXPIRED = " (Завершено)";
    public static final String CONFIRMED = "ДА";
    public static final String CANCEL = "<< Назад";

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
    public static final String RESTART_ANSWER = """
            \uD83C\uDF0D C возвращением, %s!
            Я помогу тебе планировать путешествия.
            Нажимай на кнопку под этим сообщением, чтоб выбрать следующее действие""";
    public static final String DEFAULT_ANSWER = """
            \uD83C\uDF0D Извините, я не понимаю.
            Попробуйте еще раз, либо нажмите на кнопку под этим сообщением, чтобы выбрать действие""";

    public static final String SOMETHING_WRONG = """
            \uD83C\uDF0D Что-то пошло не так, давайте попробуем начать сначала.
            Нажмите, пожалуйста, на кнопку под этим сообщением, чтобы выбрать действие""";
}
