package org.lostics.payment_protocol.servlet;

import freemarker.template.Template;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

/**
 * Servlet for handling Payment messages sent back from the client wallet.
 */
public class PaymentServlet extends AbstractServlet {

    @Override
    public Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws HttpThrowable {
        throw new HttpThrowable(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This servlet requires a POST request.");
    }

    @Override
    public Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws HttpThrowable, Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
