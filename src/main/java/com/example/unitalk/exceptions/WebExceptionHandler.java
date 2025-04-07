package com.example.unitalk.exceptions;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
@ControllerAdvice(basePackages = "com.example.unitalk.web")
public class WebExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        model.addAttribute("status", "400 Bad Request");
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler({TypeMismatchException.class})
    public String handleTypeMismatchException(TypeMismatchException e, Model model) {
        model.addAttribute("status", "400 Bad Request");
        model.addAttribute("error", "Wrong data type: " + e.getMessage());
        return "error";
    }

    @ExceptionHandler({Exception.class})
    public String handleAllOtherExceptions(Exception e, Model model) {
        model.addAttribute("status", "500 Internal Server Error");
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error";
    }

    /**
     * Handles 403 Forbidden access errors
     */
    @ExceptionHandler(UserNotEnrolledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUserNotEnrolledException(UserNotEnrolledException ex, Model model) {
        model.addAttribute("status", "403 Forbidden");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * Handles 404 Not Found errors
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute("status", "404 Not Found");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}