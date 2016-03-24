package be.ttime.core.config;

import be.ttime.core.handler.ForceLocalUrlFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

/*
 * ** WAY 1 -  "implements WebApplicationInitializer" **
 * <p>
 * This facility builds on Servlet 3's javax.servlet.ServletContainerInitializer facility. Spring provides a an implementation the SpringServletContainerInitializer which knows how to handle WebApplicationInitializer classes.
 * Basically, you declare a class that implements org.springframework.web.WebApplicationInitializer and this class will be scanned by Spring on application startup and bootstrapped.
 * This class has one responsibility: assemble the web application's moving parts, like you would in a web.xml, but in code
 * <p>
 * ** WAY 2 - "Spring helper"
 * <p>
 * Spring also provides a couple of base classes to extend to make your life easier the AbstractAnnotationConfigDispatcherServletInitializer
 * is one of those. It registers a ContextLoaderlistener (optionally) and a DispatcherServlet and allows you to easily add configuration
 * classes to load for both classes and to apply filters to the DispatcherServlet and to provide the servlet mapping.
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * Global ContextLoaderListener
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ApplicationConfig.class, JpaConfig.class, CachingConfig.class, SecurityConfig.class};
    }

    /**
     * Servlet Context
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebMvcConfig.class};
    }

    /*
     * Spring registers the Filters
     * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer#getServletFilters()
     */
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        //ForceLocalUrlFilter urlFilter = new ForceLocalUrlFilter();
        return new Filter[]{characterEncodingFilter};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("defaultHtmlEscape", "true");
        registration.setInitParameter("spring.profiles.active", "default");

        final String location = "/tmp";
        final int maxFileSize = 50 * 1024 * 1024;
        final int maxRequestSize = maxFileSize * 2;
        final int minFileSize = 0;

        registration.setMultipartConfig(new MultipartConfigElement(location, maxFileSize, maxRequestSize, minFileSize));
    }
}