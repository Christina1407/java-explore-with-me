package ru.practicum.model.enums;

public enum SortEnum {
    EVENT_DATE("eventDate");
    /*
     Не понимаю, как использовать VIEWS, поэтому не использую:)
     Сортировать одну страницу по просмотрам - нелогично. У меня есть вариант: получить все подходящие события,
     добавить к ним просмотры, отсортировать по ним и отдать требуемую страницу. Но если событий много? Как-то не очень идея.
     Хранить views из базы статистики в основной базе тоже как-то не очень.
     */
    //VIEWS("views");
    private String name;

    SortEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
