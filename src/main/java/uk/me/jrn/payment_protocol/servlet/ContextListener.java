package uk.me.jrn.payment_protocol.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Servlet context listener, sets up the Hibernate session builder on context
 * initialisation, and tears it down on shutdown.
 */
public class ContextListener implements ServletContextListener {
    private static final String CONTEXT_ATTR_SESSION_BUILDER = "org.lostics.payment_protocol.sessionFactory";

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        final Configuration configuration = new Configuration();
        final ServiceRegistry serviceRegistry;
        
        configuration.configure();
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
            configuration.getProperties()).build();
        
        servletContext.setAttribute(CONTEXT_ATTR_SESSION_BUILDER, configuration.buildSessionFactory(serviceRegistry));
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        final SessionFactory sessionFactory = ContextListener.getSessionFactory(sce.getServletContext());
        
        if (null != sessionFactory){
            sessionFactory.close();
        }
    }

    /**
     * Get the Hibernate session builder from the servlet context, if set.
     * 
     * @param servletContext servlet context to extract session builder from.
     * @return the session builder, or null if none is set/available.
     */
    public static SessionFactory getSessionFactory(final ServletContext servletContext) {
        return (SessionFactory)servletContext.getAttribute(CONTEXT_ATTR_SESSION_BUILDER);
    }
}
