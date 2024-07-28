package jjhhyb.deepvalley.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    // InValid Request Body
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Server Error
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // --------------------------- Resister --------------------------
    // Register: Missing Field
    @ExceptionHandler(RegisterException.MissingFieldException.class)
    public ResponseEntity<String> handleMissingFieldException(RegisterException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Register: Email Already Exists
    @ExceptionHandler(RegisterException.EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(RegisterException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    // Resiger: Nickname Already Exists
    @ExceptionHandler(RegisterException.NicknameAlreadyExistsException.class)
    public ResponseEntity<String> handleNicknameAlreadyExistsException(RegisterException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    // --------------------------- Login --------------------------
    // Login: Invalid Credentials
    @ExceptionHandler(LoginException.InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(LoginException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);  //401
    }

    // Login: User Not Found
    @ExceptionHandler(LoginException.UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(LoginException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // --------------------------- MyProfile --------------------------
    // MyProfile: User Not Found
    @ExceptionHandler(MyProfileException.ProfileNotFoundException.class)
    public ResponseEntity<String> handleProfileNotFoundException(MyProfileException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // MyProfile: Unauthorized Access
    @ExceptionHandler(MyProfileException.UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccessException(MyProfileException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // MyProfile: Nickname Already Exists
    @ExceptionHandler(MyProfileException.NicknameAlreadyExistsException.class)
    public ResponseEntity<String> handleNicknameAlreadyExistsException(MyProfileException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    // MyProfile: password is invalid
    @ExceptionHandler(MyProfileException.InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(MyProfileException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MyProfileException.SamePasswordException.class)
    public ResponseEntity<String> handleSamePasswordException(MyProfileException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
