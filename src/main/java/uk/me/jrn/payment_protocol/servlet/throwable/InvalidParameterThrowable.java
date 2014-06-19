package uk.me.jrn.payment_protocol.servlet.throwable;

import com.google.dogecoin.core.AddressFormatException;
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

    public InvalidParameterThrowable(String parameterName) {
        super("There provided value for the parameter \""
            + parameterName + "\" was invalid.");
    }

    public InvalidParameterThrowable(final String parameterName, final Throwable cause) {
        super("There provided value for the parameter \""
            + parameterName + "\" was invalid.", cause);
    }
    
}
