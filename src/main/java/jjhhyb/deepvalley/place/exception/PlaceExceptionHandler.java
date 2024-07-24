package jjhhyb.deepvalley.place.exception;

import jjhhyb.deepvalley.exception.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PlaceExceptionHandler {

    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePlaceNotFoundException(PlaceNotFoundException exception) {
        return ResponseEntity.status(PlaceNotFoundException.getHttpStatus()).body(new ExceptionResponse(exception.getMessage()));
    }
}