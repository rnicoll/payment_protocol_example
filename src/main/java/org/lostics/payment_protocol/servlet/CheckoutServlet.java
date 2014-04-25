package org.lostics.payment_protocol.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;

/**
 *
 * @author jrn
 */
public class CheckoutServlet extends AbstractServlet {
    @Override
    public Template doGet(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }

    @Override
    public Template doPost(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root)
            throws Exception {
        // FIXME: Create an order to be paid for
        
        return null;
    }
}
