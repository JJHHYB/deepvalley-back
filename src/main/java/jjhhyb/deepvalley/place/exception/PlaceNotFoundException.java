package jjhhyb.deepvalley.place.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class PlaceNotFoundException extends RuntimeException {

    @Getter
    private final static HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public PlaceNotFoundException(String message) {
        super(message);
    }
}
