package be.ttime.core.error;

public abstract class AbstractCmsException extends RuntimeException {

    public final String viewName;
    public final String errorKey;

    public AbstractCmsException() {
        this.viewName = viewName();
        this.errorKey = errorKey();
    }

    public AbstractCmsException(final String message) {
        super(message);
        this.viewName = viewName();
        this.errorKey = errorKey();
    }

    public AbstractCmsException(final String message, final Throwable cause) {
        super(message, cause);
        this.viewName = viewName();
        this.errorKey = errorKey();
    }

    protected abstract String viewName();

    protected abstract String errorKey();

}
