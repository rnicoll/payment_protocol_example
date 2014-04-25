package uk.me.jrn.payment_protocol.servlet.throwable;

/**
 * Throwable to represent a missing input from the user. 
 */
public class MissingParameterThrowable extends InputValidationThrowable {

    public MissingParameterThrowable(final String parameterName) {
        super("The required parameter \""
            + parameterName + "\" was missing from your request.");
    }
}
