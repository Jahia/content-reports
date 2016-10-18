package org.jahia.modules.governor.exception;

/**
 * GovernorException Class.
 *
 * Created by Juan Carlos .
 */
public class GovernorException extends Exception {

    /**
     * GovernorException Constructor.
     */
    public GovernorException() {
        super();
    }

    /**
     * GovernorException Constructor.
     *
     * @param message {@link String}
     */
    public GovernorException(String message) {
        super(message);
    }

    /**
     * GovernorException Constructor.
     *
     * @param message {@link String}
     * @param cause {@link Throwable}
     */
    public GovernorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * GovernorException Constructor.
     *
     * @param cause {@link Throwable}
     */
    public GovernorException(Throwable cause) {
        super(cause);
    }

}
