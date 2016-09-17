package be.ttime.core.config;

import be.ttime.core.config.condition.H2Condition;
import be.ttime.core.handler.AddModelParamsInterceptor;
import be.ttime.core.handler.UrlLocaleResolver;
import be.ttime.core.pebble.ext.PebbleExtension;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring4.extension.SpringExtension;
import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * We can use the tag @EnableWebMvc, this tag add the configuration of WebMvcConfigurationSupport.
 * To be able to customize we extends WebMvcConfigurationSupport
 * <p>
 * We exclude controller from ComponentScan because they are Servlet Contect class
 */
@Configuration
@ComponentScan(basePackages = {
        "be.ttime.core.controller",
        "be.ttime.core.error",
        "be.ttime.core.filter"
})
@Import({
        ServicesConfig.class,
        JpaConfig.class
})
public class WebMvcConfig extends WebMvcConfigurationSupport implements ServletContextAware {

    private static final String VIEWS = "/WEB-INF/templates/";
    private static final String RESOURCES_LOCATION = "/resources/";
    private static final String RESOURCES_HANDLER = RESOURCES_LOCATION + "**";

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AddModelParamsInterceptor paramsInterceptor;

    @Autowired
    private PebbleExtension pebbleExtension;

    @Value("${app.mode}")
    private String appMode;

    @Bean
    @Override
    /**
     * SEEM NOT WORK :(
     */
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter requestMappingHandlerAdapter
                = super.requestMappingHandlerAdapter();
        requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);
        return requestMappingHandlerAdapter;
    }

    /**
     * = <mvc:default-servlet-handler/>
     * This allows for mapping the DispatcherServlet to "/" (thus overriding the mapping of the container’s default Servlet), while still allowing
     * static resource requests to be handled by the container’s default Servlet. It configures a DefaultServletHttpRequestHandler with a URL mapping
     * of "/**" and the lowest priority relative to other URL mappings.
     * <p>
     * This handler will forward all requests to the default Servlet. Therefore it is important that it remains last in the order of all other URL
     * HandlerMappings. That will be the case if you use or alternatively if you are setting up your own customized HandlerMapping instance be sure to set
     * its order property to a value lower than that of the DefaultServletHttpRequestHandler, which is Integer.MAX_VALUE.
     */

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        // Default
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<>());
        converters.add(new AllEncompassingFormHttpMessageConverter());

        // Rome Tools
        //converters.add(new AtomFeedHttpMessageConverter());
        //converters.add(new RssChannelHttpMessageConverter());

        // Gson with Exclude Fields Annotation
        converters.add(createGsonHttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    private GsonHttpMessageConverter createGsonHttpMessageConverter() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();
        GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        gsonConverter.setGson(gson);

        return gsonConverter;
    }

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = super.requestMappingHandlerMapping();
        requestMappingHandlerMapping.setUseSuffixPatternMatch(false); /* if true, "/users" also matches to "/users.*" */
        requestMappingHandlerMapping.setUseTrailingSlashMatch(false); // if false, "/user" != "/user/"
        //requestMappingHandlerMapping.setDefaultHandler(new CmsController());
        return requestMappingHandlerMapping;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(paramsInterceptor);
    }

    /*
     * C'est une implémentation d'un LocaleResolver qui utilise un cookie.
     * paramétrer la façon dont la locale va être encapsulée dans un cookie. On sait qu'un cookie peut servir de mémoire
     * de l'utilisateur, puisque le navigateur client le renvoie systématiquement au serveur. L'intercepteur
     * [localeChangeInterceptor] précédent crée un cookie encapsulant la locale. La ligne 39 donne le nom [lang] à ce cookie.
     * Le cookie est également utilisé pour changer la locale ;
    */
    @Bean(name = "localeResolver")
    public UrlLocaleResolver localeResolver() {
        return new UrlLocaleResolver();
    }

    @Bean
    public Loader templateLoader() {
        return new ServletLoader(servletContext);
    }

    @Bean(name = "pebbleEngine")
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder().loader(templateLoader()).extension(springExtension(), pebbleExtension).cacheActive(appMode.equals("PRODUCTION")).build();
    }

    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean
    public ViewResolver viewResolver() {
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix(VIEWS);
        viewResolver.setSuffix(".peb");
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setCache(appMode.equals("PRODUCTION"));
        viewResolver.setPebbleEngine(pebbleEngine());
        viewResolver.setOrder(0);
        return viewResolver;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    /**
     * Set the resource directory
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler(RESOURCES_HANDLER)
                .addResourceLocations(RESOURCES_LOCATION)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        /*
        if (appMode.equals("PRODUCTION")) {
            registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(RESOURCES_LOCATION).setCachePeriod(365 * 24 * 60 * 60); // 1 year
        } else {
            registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(RESOURCES_LOCATION);
        }*/
    }

    @Override
    protected void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        // Turn off suffix-based content negotiation
        configurer.favorPathExtension(false);
    }

    /*
    // auto
    @Bean
    public FilterRegistrationBean forceLocalUrlFilterRegistrationBean(final ForceLocalUrlFilter filter) {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }*/

    @Bean
    @Conditional(H2Condition.class)
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
        registration.addUrlMappings("/h2-console/*");
        registration.addInitParameter("webAllowOthers", "true");
        return registration;
    }
}
