package be.ttime.core.config;

import be.ttime.Application;
import be.ttime.core.config.security.AuthenticationFailureHandler;
import be.ttime.core.handler.AddModelParamsInterceptor;
import be.ttime.core.handler.ForceLocalUrlFilter;
import be.ttime.core.handler.UrlLocaleChangeInterceptor;
import be.ttime.core.handler.UrlLocaleResolver;
import be.ttime.core.model.DatabaseMessageSourceBase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring4.extension.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Locale;

/**
 * We can use the tag @EnableWebMvc, this tag add the configuration of WebMvcConfigurationSupport.
 * To be able to customize we extends WebMvcConfigurationSupport
 * <p>
 * We exclude controller from ComponentScan because they are Servlet Contect class
 */
@Configuration
@ComponentScan(basePackageClasses = Application.class, includeFilters = @Filter({Controller.class}), useDefaultFilters = false)
class WebMvcConfig extends WebMvcConfigurationSupport {

    private static final String VIEWS = "/WEB-INF/templates/";
    private static final String RESOURCES_LOCATION = "/resources/";
    private static final String RESOURCES_HANDLER = RESOURCES_LOCATION + "**";

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private MessageSource messageSource;

    @Value("${app.mode}")
    private String appMode;

    @Value("${locale.default}")
    private String defaultLocale;

    @Value("${locale.force.url}")
    private boolean forceUrl;

    @Value("${locale.force.url.except.default}")
    private boolean forceUrlExceptDefault;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

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
        return requestMappingHandlerMapping;
    }

    @Bean
    public AddModelParamsInterceptor currentUserInterceptor() {
        return new AddModelParamsInterceptor();
    }

    @Bean
    public UrlLocaleChangeInterceptor urlLocaleChangeInterceptor() {
        return new UrlLocaleChangeInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(urlLocaleChangeInterceptor());
        registry.addInterceptor(currentUserInterceptor());
    }

    /*
     * C'est une implémentation d'un LocaleResolver qui utilise un cookie.
     * paramétrer la façon dont la locale va être encapsulée dans un cookie. On sait qu'un cookie peut servir de mémoire
     * de l'utilisateur, puisque le navigateur client le renvoie systématiquement au serveur. L'intercepteur
     * [localeChangeInterceptor] précédent crée un cookie encapsulant la locale. La ligne 39 donne le nom [lang] à ce cookie.
     * Le cookie est également utilisé pour changer la locale ;
    */
    @Bean(name="localeResolver")
    public UrlLocaleResolver localeResolver() {
        UrlLocaleResolver localeResolver = new UrlLocaleResolver();
        localeResolver.setCookieName("lang");
        localeResolver.setDefaultLocale(new Locale(defaultLocale));
        return localeResolver;
    }

    @Bean
    public Loader templateLoader() {
        return new ServletLoader(servletContext);
    }

    @Bean(name = "pebbleEngine")
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder().loader(templateLoader()).extension(springExtension()).cacheActive(appMode.equals("PRODUCTION")).build();
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

        if (appMode.equals("PRODUCTION")) {
            registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(RESOURCES_LOCATION).setCachePeriod(365 * 24 * 60 * 60); // 1 year
        } else {
            registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(RESOURCES_LOCATION);
        }
    }

    /**
     * Handles favicon.ico requests assuring no <code>404 Not Found</code> error is returned.
     */
    @Controller
    static class FaviconController {
        @RequestMapping("favicon.ico")
        String favicon() {
            return "forward:/resources/images/favicon.ico";
        }
    }
}