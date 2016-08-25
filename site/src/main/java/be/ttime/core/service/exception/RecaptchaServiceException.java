package be.ttime.core.service.exception;

public class RecaptchaServiceException extends RuntimeException {

    public RecaptchaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
