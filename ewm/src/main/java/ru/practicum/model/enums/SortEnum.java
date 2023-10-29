package ru.practicum.model.enums;

public enum SortEnum {
    EVENT_DATE("eventDate");
    /*
     Не понимаю, как использовать VIEWS, поэтому не использую :)
     Сортировать одну страницу по просмотрам - нелогично. У меня вариант: получить все подходящие события,
     отсортировать и отдать требуемую страницу. Но если событий много?
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
