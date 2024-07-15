package jjhhyb.deepvalley.user.exception;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }

    public static class InvalidCredentialsException extends LoginException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends LoginException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}