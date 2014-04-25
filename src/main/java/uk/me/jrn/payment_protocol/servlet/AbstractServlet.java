package uk.me.jrn.payment_protocol.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet2Params;
import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import uk.me.jrn.payment_protocol.model.Network;
import uk.me.jrn.payment_protocol.servlet.throwable.InputValidationThrowable;
import uk.me.jrn.payment_protocol.servlet.throwable.MissingParameterThrowable;
import uk.me.jrn.payment_protocol.servlet.throwable.InvalidParameterThrowable;

/**
 *
 * @author jrn
 */
public abstract class AbstractServlet extends HttpServlet {
    public static final BigDecimal AMOUNT_PIP = new BigDecimal("0.00000001");
    
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
    public static final String CONTENT_TYPE_APPLICATION_XML_HTML = "application/xhtml+xml";
    public static final String CONTEXT_PARAMETER_FREEMARKER_TEMPLATE_DIR = "org.lostics.payment_protocol.servlet.freemarkerTemplateDir";
    
    public static final String DEFAULT_FREEMARKER_TEMPLATE_DIR = "/WEB-INF/freemarker";
    
    private Configuration freemarkerCfg;

    private Session buildHibernateSession() {
        final SessionFactory sessionFactory = ContextListener.getSessionFactory(this.getServletContext());
        
        // TODO: Handle lack of a session factory
        
        return sessionFactory.openSession();
    }
    
    public final Map<String, Object> buildRoot(final HttpServletRequest request,
        final HttpServletResponse response)
        throws ServletException {
        final Map<String, Object> root = new HashMap<>();
        
        root.put("request", request);
        root.put("response", response);
        
        return root;
    }

    /**
     * Build a Freemarker configuration based on details loaded from servlet
     * context.
     * 
     * @param context servlet context to use for resolving file location on
     * disk.
     * @param templateDirectory name of the template directory, relative to the
     * web application context.
     * @return a Freemarker configuration.
     */
    private freemarker.template.Configuration configureFreemarker(final ServletContext context, final String templateDirectory)
        throws ServletException {
        final freemarker.template.Configuration freemarkerConfiguration = new freemarker.template.Configuration();

        try {
            final File abstractTemplateDir = new File(context.getRealPath(templateDirectory));

            if (false == abstractTemplateDir.exists() ||
                false == abstractTemplateDir.isDirectory()) {
                throw new ServletException("Supplied Freemarker template directory \""
                    + abstractTemplateDir.getCanonicalPath() + "\" (generated from \""
                    + templateDirectory + "\") is not a directory.");
            }
            freemarkerConfiguration.setDirectoryForTemplateLoading(abstractTemplateDir);
            freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            freemarkerConfiguration.setOutputEncoding(CHARSET_UTF_8.name());
        } catch(IOException e) {
            throw new ServletException(e);
        }

        final BeansWrapper wrapper = new BeansWrapper();
        wrapper.setExposureLevel(BeansWrapper.EXPOSE_SAFE);
        freemarkerConfiguration.setObjectWrapper(wrapper);

        return freemarkerConfiguration;
    }

    @Override
    public void init(final ServletConfig config)
        throws ServletException {
        super.init(config);

        final ServletContext context = config.getServletContext();
        String templateDirectory = context.getInitParameter(CONTEXT_PARAMETER_FREEMARKER_TEMPLATE_DIR);

        if (null == templateDirectory) {
            templateDirectory = DEFAULT_FREEMARKER_TEMPLATE_DIR;
        }
        
        this.freemarkerCfg = configureFreemarker(context, templateDirectory);
    }
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        final Map<String, Object> root = buildRoot(request, response);
        final Template template;
        
        try {
            final Session session = buildHibernateSession();
            try {
                template = doGet(request, response, root, session);
            } finally {
                session.close();
            }
        }
        catch(HttpThrowable t)
        {
            t.send(response);
            return;
        }
        catch(ServletException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }
        
        if (null == template)
        {
            return;
        }
        
        response.setContentType(CONTENT_TYPE_APPLICATION_XML_HTML);
        
        try {
            template.process(root, response.getWriter());
        } catch(TemplateException e) {
            throw new ServletException("Could not process template.", e);
        }
    }
    
    public abstract Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, Session session)
            throws HttpThrowable, Exception;
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        final Map<String, Object> root = buildRoot(request, response);
        final Template template;
        
        try {
            final Session session = buildHibernateSession();
            try {
                template = doPost(request, response, root, session);
            } finally {
                session.close();
            }
        }
        catch(HttpThrowable t)
        {
            t.send(response);
            return;
        }
        catch(ServletException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }
        
        if (null == template)
        {
            return;
        }
        
        try {
            template.process(root, response.getWriter());
        } catch(TemplateException e) {
            throw new ServletException("Could not process template.", e);
        }
    }
    
    public abstract Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root, Session session)
            throws HttpThrowable, Exception;

    public Configuration getConfiguration() {
        return this.freemarkerCfg;
    }

    public Address getAddressParameter(final HttpServletRequest request, final String parameterName,
            final NetworkParameters networkParams)
            throws InputValidationThrowable {
        final String value = getStringParameter(request, parameterName);
        
        try {
            return new Address(networkParams, value);
        } catch(AddressFormatException e) {
            throw new InvalidParameterThrowable(parameterName);
        }
    }

    /**
     * Get a currency quantity from a parameter sent by the client.
     * 
     * @param request
     * @param parameterName
     * @return
     * @throws InputValidationThrowable 
     */
    public BigDecimal getAmountParameter(final HttpServletRequest request, final String parameterName)
            throws InputValidationThrowable {
        final BigDecimal amountDecimal = getBigDecimalParameter(request, parameterName);
        
        if (amountDecimal.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidParameterThrowable(parameterName);
        }
        
        return amountDecimal;
    }

    public BigDecimal getBigDecimalParameter(final HttpServletRequest request, final String parameterName)
            throws InputValidationThrowable {
        final String value = getStringParameter(request, parameterName);
        
        try {
            return new BigDecimal(value);
        } catch(IllegalArgumentException e) {
            throw new InvalidParameterThrowable(parameterName);
        }
    }

    public <T extends Enum> T getEnumParameter(final HttpServletRequest request, final String parameterName,
            final Class<T> aClass)
            throws InputValidationThrowable {
        final String value = getStringParameter(request, parameterName);
        
        try {
            return (T)Enum.valueOf(aClass, value);
        } catch(IllegalArgumentException e) {
            throw new InvalidParameterThrowable(parameterName, EnumSet.allOf(aClass));
        }
    }
    
    public NetworkParameters getNetworkParameters(final Network network) {
        switch (network) {
            case BITCOIN_MAIN:
                return new MainNetParams();
            case BITCOIN_TEST:
                return new TestNet2Params();
            default:
                break;
        }
        
        throw new IllegalArgumentException("Unsupported network \""
            + network.name() + "\".");
    }

    public String getStringParameter(final HttpServletRequest request, final String parameterName)
            throws InputValidationThrowable {
        final String value = request.getParameter(parameterName);
        
        if (null == value) {
            throw new MissingParameterThrowable(parameterName);
        }
        
        return value;
    }
}
