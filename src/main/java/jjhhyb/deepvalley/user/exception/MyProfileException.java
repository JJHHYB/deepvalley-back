package jjhhyb.deepvalley.user.exception;

public class MyProfileException extends RuntimeException {
    public MyProfileException(String message) {
        super(message);
    }

    public static class ProfileNotFoundException extends MyProfileException {
        public ProfileNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedAccessException extends MyProfileException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public static class NicknameAlreadyExistsException extends MyProfileException {
        public NicknameAlreadyExistsException(String message) {
            super(message);
        }
    }
}