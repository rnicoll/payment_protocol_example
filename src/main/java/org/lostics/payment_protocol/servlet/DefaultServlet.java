package org.lostics.payment_protocol.servlet;

import freemarker.template.Template;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jrn
 */
public class DefaultServlet extends AbstractServlet {

    @Override
    public Template doGet(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }

    @Override
    public Template doPost(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }
    
}
