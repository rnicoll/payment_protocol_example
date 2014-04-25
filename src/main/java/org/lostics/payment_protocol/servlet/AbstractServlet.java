package org.lostics.payment_protocol.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

/**
 *
 * @author jrn
 */
public abstract class AbstractServlet extends HttpServlet {
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
    public static final String CONTENT_TYPE_APPLICATION_XML_HTML = "application/xhtml+xml";
    public static final String CONTEXT_PARAMETER_FREEMARKER_TEMPLATE_DIR = "org.lostics.payment_protocol.servlet.freemarkerTemplateDir";
    
    public static final String DEFAULT_FREEMARKER_TEMPLATE_DIR = "/WEB-INF/freemarker";
    
    private Configuration freemarkerCfg;
    
    public final Map<String, Object> buildRoot(final HttpServletRequest request,
        final HttpServletResponse response)
        throws ServletException {
        final Map<String, Object> root = new HashMap<>();
        
        root.put("request", request);
        root.put("response", response);
        
        return root;
    }

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
            template = doGet(request, response, root);
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
            final Map<String, Object> root)
            throws HttpThrowable, Exception;
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        final Map<String, Object> root = buildRoot(request, response);
        final Template template;
        
        try {
            template = doPost(request, response, root);
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
            final Map<String, Object> root)
            throws HttpThrowable, Exception;

    public Configuration getConfiguration() {
        return this.freemarkerCfg;
    }
}
