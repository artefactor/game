package com.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetails {
    Exception e;
    private String error;
    private String errorPoint;
    private String errorClass;
    private String errorMessage;
}
