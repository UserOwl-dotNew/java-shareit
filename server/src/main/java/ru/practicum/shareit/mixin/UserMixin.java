package ru.practicum.shareit.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UserMixin {
    @JsonProperty("id")
    public abstract Long getId();

    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("email")
    public abstract String getEmail();
}