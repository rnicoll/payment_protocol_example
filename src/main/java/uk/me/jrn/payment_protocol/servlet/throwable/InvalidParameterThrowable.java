package uk.me.jrn.payment_protocol.servlet.throwable;

import java.util.Collection;

/**
 *
 * @author jrn
 */
public class InvalidParameterThrowable extends InputValidationThrowable {

    public InvalidParameterThrowable(final String parameterName, Collection<?> possibleValues) {
        super("There provided value for the parameter \""
            + parameterName + "\" was invalid; expected one of: "
            + possibleValues);
    }
    
}
