package be.ttime.core.util;


import org.springframework.validation.FieldError;

import java.util.List;

public class ControllerUtils {

    public static String getValidationErrorsInUl(List<FieldError> errors) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<ul>");
        for (FieldError error : errors) {
            buffer.append("<li>");
            setErrorMessage(buffer, error);
            buffer.append("</li>");
        }
        buffer.append("</ul>");
        return buffer.toString();
    }

    public static String getValidationErrorsInString(List<FieldError> errors) {
        StringBuffer buffer = new StringBuffer();
        for (FieldError error : errors) {
            setErrorMessage(buffer, error);
        }
        return buffer.toString();
    }

    private static void setErrorMessage(StringBuffer buffer, FieldError error) {
        buffer.append(String.format("[%s: %s]", error.getField(), error.getDefaultMessage()));
    }
}
