package jjhhyb.deepvalley.user.exception;

public class RegisterException extends RuntimeException {
    public RegisterException(String message) {
        super(message);
    }

    public static class MissingFieldException extends RegisterException {
        public MissingFieldException(String message) {
            super(message);
        }
    }

    public static class EmailAlreadyExistsException extends RegisterException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class NicknameAlreadyExistsException extends RegisterException {
        public NicknameAlreadyExistsException(String message) {
            super(message);
        }
    }
}