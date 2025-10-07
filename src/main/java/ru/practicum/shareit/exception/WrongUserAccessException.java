package ru.practicum.shareit.exception;

public class WrongUserAccessException extends RuntimeException {
    public WrongUserAccessException(String message) {
        super(message);
    }
}
