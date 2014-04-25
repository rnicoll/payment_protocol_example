package uk.me.jrn.payment_protocol.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Throwable for returning HTTP statuses outwith the normal flow of execution
 * for code. Used as a way of ensuring execution of a servlet stops immediately,
 * rather than risking typos causing status code to be set and execution to then
 * continue.
 */
public class HttpThrowable extends Throwable {
    private final int statusCode;
    
    public          HttpThrowable(final int statusCode) {
        super();
        
        if (statusCode < HttpServletResponse.SC_CONTINUE
                || statusCode >= 600) {
            throw new IllegalArgumentException("HTTP status codes must be in the range 100-599 inclusive.");
        }
        
        this.statusCode = statusCode;
    }
    
    public          HttpThrowable(final int statusCode, final String message) {
        super(message);
        
        if (statusCode < HttpServletResponse.SC_CONTINUE
                || statusCode >= 600) {
            throw new IllegalArgumentException("HTTP status codes must be in the range 100-599 inclusive.");
        }
        
        this.statusCode = statusCode;
    }
    
    public void send(final HttpServletResponse response)
        throws IllegalStateException, IOException {
        response.reset();
    
        if (null != this.getMessage()) {
            response.sendError(getStatusCode(), this.getMessage());
        } else {
            response.setStatus(this.getStatusCode());
        }
    }

    public int getStatusCode() {
        return statusCode;
    }
}
