package be.ttime.core.error;

import org.springframework.security.authentication.AccountStatusException;

public class IpLockedException extends AccountStatusException {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs a <code>LockedException</code> with the specified message.
     *
     * @param msg the detail message.
     */
    public IpLockedException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>LockedException</code> with the specified message and root
     * cause.
     *
     * @param msg the detail message.
     * @param t root cause
     */
    public IpLockedException(String msg, Throwable t) {
        super(msg, t);
    }
}
