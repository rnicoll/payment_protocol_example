package uk.me.jrn.payment_protocol.servlet.throwable;

/**
 * Abstract throwable for cases where user input is invalid. Used to ensure execution
 * aborts on a serious input error.
 */
public abstract class InputValidationThrowable extends Throwable {
    public  InputValidationThrowable(final String message) {
        super(message);
    }
    
    public  InputValidationThrowable(final String message, final Throwable cause) {
        super(message, cause);
    }
}
