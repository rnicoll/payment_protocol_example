package org.lostics.payment_protocol.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;
import org.hibernate.Session;

/**
 *
 * @author jrn
 */
public class CheckoutServlet extends AbstractServlet {
    @Override
    public Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }

    @Override
    public Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, final Session session)
            throws Exception {
        // FIXME: Create an order to be paid for
        
        return null;
    }
}
