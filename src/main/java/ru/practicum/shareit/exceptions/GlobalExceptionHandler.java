package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ItemNotFoundException.class, UserNotFoundException.class})
    public void handleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
    @ExceptionHandler({EmailAlreadyExistsException.class})
    public void handleBadRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value());
    }
    @ExceptionHandler({ValidatorExceptions.class})
    public void handleBadValidator(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
