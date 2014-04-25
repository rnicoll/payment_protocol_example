package uk.me.jrn.payment_protocol.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;

import freemarker.template.Template;
import org.hibernate.Session;

import uk.me.jrn.payment_protocol.model.Network;
import uk.me.jrn.payment_protocol.servlet.throwable.InputValidationThrowable;

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
        final Network network;
        
        try {
            network = this.getEnumParameter(request, "network", Network.class);
        } catch(InputValidationThrowable e) {
            throw new ServletException(e.getMessage(), e);
        }
        
        return null;
    }
}
