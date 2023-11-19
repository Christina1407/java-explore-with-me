package ru.practicum.model.enums;

public enum SortEnum {
    EVENT_DATE("eventDate"),
    VIEWS("views");
    private String name;

    SortEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
