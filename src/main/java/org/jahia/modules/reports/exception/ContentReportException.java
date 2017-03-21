package org.jahia.modules.reports.exception;

public class ContentReportException extends Exception {

    /**
     * ContentReportException Constructor.
     */
    public ContentReportException() {
        super();
    }

    /**
     * ContentReportException Constructor.
     *
     * @param message {@link String}
     */
    public ContentReportException(String message) {
        super(message);
    }

    /**
     * ContentReportException Constructor.
     *
     * @param message {@link String}
     * @param cause {@link Throwable}
     */
    public ContentReportException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * ContentReportException Constructor.
     *
     * @param cause {@link Throwable}
     */
    public ContentReportException(Throwable cause) {
        super(cause);
    }

}
