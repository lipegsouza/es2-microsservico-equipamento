package com.microsservico.equipamento.exception;

public class InvalidActionException extends RuntimeException {

    public InvalidActionException(String message) {
        super(message);
    }
}