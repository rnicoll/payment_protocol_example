package org.lostics.payment_protocol.servlet;

import freemarker.template.Template;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for handling Payment messages sent back from the client wallet.
 */
public class PaymentServlet extends AbstractServlet {

    @Override
    public Template doGet(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root)
            throws HttpThrowable {
        throw new HttpThrowable(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This servlet requires a POST request.");
    }

    @Override
    public Template doPost(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
