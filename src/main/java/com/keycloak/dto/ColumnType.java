package com.keycloak.dto;
import com.fasterxml.jackson.annotation.JsonValue;

 

public enum ColumnType {
    BUTTON("button"),
    ICON("icon"),
    RADIO("radio"),
    TEXT("text"),
    SELECT("select");

    private String name;
    private ColumnType(String name) {
        this.name = name;
    }

 

    @JsonValue
    public String getName() {
        return this.name;
    }
}