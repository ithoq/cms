package be.ttime.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PagePersistenceException extends AbstractCmsException {

    public PagePersistenceException(final String message) {
        super(message);
    }

    public PagePersistenceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    protected String viewName() {
        return GlobalExceptionController.VIEW_GENERAL;
    }

    @Override
    protected String errorKey() {
        return "error.general";
    }

}
