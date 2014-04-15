/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.lostics.payment_protocol.servlet;

import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jrn
 */
public abstract class AbstractServlet extends FreemarkerServlet {
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        final Map<String, Object> root = buildRoot(request, response);
        final Template template;
        
        try {
            template = doGet(request, response, root);
        }
        catch(ServletException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }
        
        try {
            template.process(root, response.getWriter());
        } catch(TemplateException e) {
            throw new ServletException("Could not process template.", e);
        }
    }
    
    public abstract Template doGet(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root)
            throws Exception;
    
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        final Map<String, Object> root = buildRoot(request, response);
        final Template template;
        
        try {
            template = doPost(request, response, root);
        }
        catch(ServletException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }
        
        try {
            template.process(root, response.getWriter());
        } catch(TemplateException e) {
            throw new ServletException("Could not process template.", e);
        }
    }
    
    public abstract Template doPost(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Object> root)
            throws Exception;

    private Map<String, Object> buildRoot(HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
