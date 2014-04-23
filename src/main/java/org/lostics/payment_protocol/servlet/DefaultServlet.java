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
    public static final String NETWORK_BITCOIN_MAIN = "main";
    public static final String NETWORK_BITCOIN_TEST = "test";
    public static final String NETWORK_DOGECOIN_MAIN = "doge-main";
    public static final String NETWORK_DOGECOIN_TEST = "doge-test";
    
    public static final long EXPIRE_INTERVAL = 60 * 60 * 1000; // One hour

    @Override
    public Template doGet(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }

    @Override
    public Template doPost(HttpServletRequest request, HttpServletResponse response, Map<String, Object> root) throws Exception {
        return this.getConfiguration().getTemplate("default.ftl");
    }
    
}
